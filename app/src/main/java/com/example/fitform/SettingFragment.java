package com.example.fitform;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.fitform.databinding.FragmentSettingBinding;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SettingFragment extends Fragment {

    private Context mainContext;
    private MainViewModel viewModel;
    private ExecutorService backgroundExecutor;
    private FragmentSettingBinding fragmentSettingBinding;

    public SettingFragment(Context context) {
        this.mainContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        backgroundExecutor.execute(() -> {
            if (((MainActivity) mainContext).getPoseLandmarkerHelper().isClose()) {
                ((MainActivity) mainContext).getPoseLandmarkerHelper().setupPoseLandmarker();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        //viewModel.setModel(poseLandmarkerHelper.getCurrentModel());
        viewModel.setMinPoseDetectionConfidence(((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseDetectionConfidence());
        viewModel.setMinPoseTrackingConfidence(((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseTrackingConfidence());
        viewModel.setMinPosePresenceConfidence(((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPosePresenceConfidence());

        backgroundExecutor.execute(() -> ((MainActivity) mainContext).getPoseLandmarkerHelper().clearPoseLandmarker());
    }

    @Override
    public void onDestroyView() {
        fragmentSettingBinding = null;
        super.onDestroyView();

        backgroundExecutor.shutdown();
        try {
            backgroundExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSettingBinding = FragmentSettingBinding.inflate(inflater, container, false);
        return fragmentSettingBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        backgroundExecutor = Executors.newSingleThreadExecutor();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBottomSheetControls();
    }

    private void initBottomSheetControls() {
        fragmentSettingBinding.detectionThresholdValue.setText(
                String.format(Locale.US, "%.2f", viewModel.getCurrentMinPoseDetectionConfidence())
        );
        fragmentSettingBinding.trackingThresholdValue.setText(
                String.format(Locale.US, "%.2f", viewModel.getCurrentMinPoseTrackingConfidence())
        );
        fragmentSettingBinding.presenceThresholdValue.setText(
                String.format(Locale.US, "%.2f", viewModel.getCurrentMinPosePresenceConfidence())
        );

        fragmentSettingBinding.detectionThresholdMinus.setOnClickListener(v -> {
            if (((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseDetectionConfidence() >= 0.2) {
                ((MainActivity) mainContext).getPoseLandmarkerHelper().setMinPoseDetectionConfidence(((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseDetectionConfidence() - 0.1f);
                updateControlsUi();
            }
        });

        fragmentSettingBinding.detectionThresholdPlus.setOnClickListener(v -> {
            if (((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseDetectionConfidence() <= 0.8) {
                ((MainActivity) mainContext).getPoseLandmarkerHelper().setMinPoseDetectionConfidence(((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseDetectionConfidence() + 0.1f);
                updateControlsUi();
            }
        });

        fragmentSettingBinding.trackingThresholdMinus.setOnClickListener(v -> {
            if (((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseTrackingConfidence() >= 0.2) {
                ((MainActivity) mainContext).getPoseLandmarkerHelper().setMinPoseTrackingConfidence(((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseTrackingConfidence() - 0.1f);
                updateControlsUi();
            }
        });

        fragmentSettingBinding.trackingThresholdPlus.setOnClickListener(v -> {
            if (((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseTrackingConfidence() <= 0.8) {
                ((MainActivity) mainContext).getPoseLandmarkerHelper().setMinPoseTrackingConfidence(((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseTrackingConfidence() + 0.1f);
                updateControlsUi();
            }
        });

        fragmentSettingBinding.presenceThresholdMinus.setOnClickListener(v -> {
            if (((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPosePresenceConfidence() >= 0.2) {
                ((MainActivity) mainContext).getPoseLandmarkerHelper().setMinPosePresenceConfidence(((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPosePresenceConfidence() - 0.1f);
                updateControlsUi();
            }
        });

        fragmentSettingBinding.presenceThresholdPlus.setOnClickListener(v -> {
            if (((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPosePresenceConfidence() <= 0.8) {
                ((MainActivity) mainContext).getPoseLandmarkerHelper().setMinPosePresenceConfidence(((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPosePresenceConfidence() + 0.1f);
                updateControlsUi();
            }
        });

        fragmentSettingBinding.spinnerModel.setSelection(((MainActivity) mainContext).getPoseLandmarkerHelper().getCurrentModel());
        fragmentSettingBinding.spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) mainContext).getPoseLandmarkerHelper().setCurrentModel(position);
                updateControlsUi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no op
            }
        });
    }

    private void updateControlsUi() {
        fragmentSettingBinding.detectionThresholdValue.setText(
                String.format(Locale.US, "%.2f", ((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseDetectionConfidence())
        );
        fragmentSettingBinding.trackingThresholdValue.setText(
                String.format(Locale.US, "%.2f", ((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPoseTrackingConfidence())
        );
        fragmentSettingBinding.presenceThresholdValue.setText(
                String.format(Locale.US, "%.2f", ((MainActivity) mainContext).getPoseLandmarkerHelper().getMinPosePresenceConfidence())
        );

        backgroundExecutor.execute(() -> {
            ((MainActivity) mainContext).getPoseLandmarkerHelper().clearPoseLandmarker();
            ((MainActivity) mainContext).getPoseLandmarkerHelper().setupPoseLandmarker();
        });
    }
}