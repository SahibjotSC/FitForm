package com.example.fitform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.fitform.exercise.helper.DataObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private List<DataObject> dataObjects;

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        GridLayout gridLayout = view.findViewById(R.id.gridLayout);

        dataObjects = new ArrayList<>();
        dataObjects.add(MainActivity.getDataObject(getContext(), "Squats"));
        dataObjects.add(MainActivity.getDataObject(getContext(), "Pushups"));
        dataObjects.add(MainActivity.getDataObject(getContext(), "Lunges"));
        dataObjects.add(MainActivity.getDataObject(getContext(), "JumpingJacks"));

        for (int i = 0; i < dataObjects.size() && i < 4; i++) {
            DataObject dataObject = dataObjects.get(i);
            LinearLayout itemLayout = (LinearLayout) inflater.inflate(R.layout.grid_item, gridLayout, false);

            TextView title = itemLayout.findViewById(R.id.title);
            TextView integerValue = itemLayout.findViewById(R.id.integerValue);
            TextView floatValue = itemLayout.findViewById(R.id.floatValue);

            title.setText("Item " + (i + 1));
            integerValue.setText(String.valueOf(dataObject.getDateTimes().size()));
            floatValue.setText(String.valueOf((float)dataObject.getDateTimes().size() / dataObject.getIncorrect()));

            gridLayout.addView(itemLayout);
        }

        return view;
    }
}