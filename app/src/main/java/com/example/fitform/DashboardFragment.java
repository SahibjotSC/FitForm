package com.example.fitform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fitform.exercise.helper.DataObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DataObject squatsData;
    private DataObject pushupsData;
    private DataObject lungesData;
    private DataObject jumpingJacksData;

    private float SQUATS_CALORIES_PER_REP = 0.32f;
    private float PUSHUPS_CALORIES_PER_REP = 0.3f;
    private float LUNGES_CALORIES_PER_REP = 0.29f;
    private float JUMPING_JACKS_CALORIES_PER_REP = 0.35f;

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize DataObjects
        squatsData = MainActivity.getDataObject(getContext(), "Squats");
        pushupsData = MainActivity.getDataObject(getContext(), "Pushups");
        lungesData = MainActivity.getDataObject(getContext(), "Lunges");
        jumpingJacksData = MainActivity.getDataObject(getContext(), "JumpingJacks");

        // Set click listeners for exercise cards
        view.findViewById(R.id.squatsCard).setOnClickListener(v -> openGraphFragment("Squats"));
        view.findViewById(R.id.pushupCard).setOnClickListener(v -> openGraphFragment("Pushups"));
        view.findViewById(R.id.lungesCard).setOnClickListener(v -> openGraphFragment("Lunges"));
        view.findViewById(R.id.jumpingjacksCard).setOnClickListener(v -> openGraphFragment("JumpingJacks"));

        // Populate UI for each exercise
        populateExerciseData(view, squatsData, R.id.squatsCount, R.id.squatsCalories, R.id.squatsTime, R.id.squatsProgress, R.id.squatsGoalProgress, SQUATS_CALORIES_PER_REP);
        populateExerciseData(view, pushupsData, R.id.pushupsCount, R.id.pushupsCalories, R.id.pushupsTime, R.id.pushupsProgress, R.id.pushupsGoalProgress, PUSHUPS_CALORIES_PER_REP);
        populateExerciseData(view, lungesData, R.id.lungesCount, R.id.lungesCalories, R.id.lungesTime, R.id.lungesProgress, R.id.lungesGoalProgress, LUNGES_CALORIES_PER_REP);
        populateExerciseData(view, jumpingJacksData, R.id.jumpingJacksCount, R.id.jumpingJacksCalories, R.id.jumpingJacksTime, R.id.jumpingJacksProgress, R.id.jumpingJacksGoalProgress, JUMPING_JACKS_CALORIES_PER_REP);

        return view;
    }

    private void openGraphFragment(String exerciseName) {
        GraphFragment graphFragment = new GraphFragment();

        Bundle args = new Bundle();
        args.putString("exerciseName", exerciseName);
        graphFragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container1, graphFragment)
                .commit();
    }

    private void populateExerciseData(View view, DataObject data, int countId, int caloriesId, int accuracyId, int progressId, int progressBarId, float caloriesPerRep) {
        // Get today's and yesterday's data
        List<Date> todayList = new ArrayList<>();
        List<Date> yesterdayList = new ArrayList<>();
        splitDataByDate(data.getDateTimes(), todayList, yesterdayList);

        List<Date> todayListError = new ArrayList<>();
        List<Date> yesterdayListError = new ArrayList<>();
        splitDataByDate(data.getIncorrectTimes(), todayListError, yesterdayListError);

        // Calculate metrics
        int todayReps = todayList.size();
        int yesterdayReps = yesterdayList.size();
        double calories = todayReps * caloriesPerRep;
        double accuracy = todayReps == 0 ? 0 : 1 - ((double) todayListError.size() / todayReps);
        float vsLast = ((float)todayReps / Math.max((float)yesterdayReps, 1)) * 100f;

        // Update UI
        TextView countView = view.findViewById(countId);
        TextView caloriesView = view.findViewById(caloriesId);
        TextView accuracyView = view.findViewById(accuracyId);
        TextView progressView = view.findViewById(progressId);
        ProgressBar progressBar = view.findViewById(progressBarId);

        countView.setText(String.valueOf(todayReps));
        caloriesView.setText(String.format(Locale.getDefault(), "%.1f", calories));
        accuracyView.setText(String.format(Locale.getDefault(), "%.0f%%", accuracy * 100));

        // Update VS LAST and ProgressBar
        progressView.setText(String.format(Locale.getDefault(), "%+.0f%%", vsLast));
        progressView.setTextColor(ContextCompat.getColor(getContext(), vsLast >= 0 ? R.color.stat_highlight : R.color.mp_color_error));
        progressBar.setProgress(Math.max(0, Math.min(100, (int) Math.abs(vsLast))));
    }

    private void splitDataByDate(List<Date> dateTimes, List<Date> todayList, List<Date> yesterdayList) {
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String todayStr = sdf.format(today.getTime());
        String yesterdayStr = sdf.format(yesterday.getTime());

        for (Date date : dateTimes) {
            String dateStr = sdf.format(date);
            if (dateStr.equals(todayStr)) {
                todayList.add(date);
            } else if (dateStr.equals(yesterdayStr)) {
                yesterdayList.add(date);
            }
        }
    }
}