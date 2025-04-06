package com.example.fitform;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.fitform.MainActivity;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.example.fitform.exercise.helper.DataObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class GraphFragment extends Fragment {

    private LineChart lineChart;
    private Spinner spinnerGraphType;
    private Spinner spinnerXAxisRange;
    private List<Date> dataPoints;

    public GraphFragment() {
        // Required empty public constructor
    }

    public static GraphFragment newInstance(String param1, String param2) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DataObject dataObject = MainActivity.getDataObject(getContext(), "Lunges");
        //dataPoints = dataObject.getDateTimes();
        dataPoints = generateRandomTestData();
    }

    private List<Date> generateRandomTestData() {
        List<Date> randomDates = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            long randomTime = currentTime - (long) (Math.random() * 1000 * 60 * 60 * 24 * 30); // Random dates within the last 30 days
            randomDates.add(new Date(randomTime));
        }
        return randomDates;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        lineChart = view.findViewById(R.id.line_chart);
        spinnerGraphType = view.findViewById(R.id.spinner_graph_type);
        spinnerXAxisRange = view.findViewById(R.id.spinner_x_axis_range);

        // Set up the graph type spinner
        ArrayAdapter<CharSequence> adapterGraphType = ArrayAdapter.createFromResource(getContext(),
                R.array.graph_types, android.R.layout.simple_spinner_item);
        adapterGraphType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGraphType.setAdapter(adapterGraphType);

        spinnerGraphType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up the x-axis range spinner
        ArrayAdapter<CharSequence> adapterXAxisRange = ArrayAdapter.createFromResource(getContext(),
                R.array.x_axis_ranges, android.R.layout.simple_spinner_item);
        adapterXAxisRange.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerXAxisRange.setAdapter(adapterXAxisRange);

        spinnerXAxisRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("MainActivity", spinnerXAxisRange.getSelectedItem().toString());
                updateGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Customize the LineChart
        customizeLineChart();

        // Initial graph setup
        updateGraph();

        return view;
    }

    private void customizeLineChart() {
        lineChart.setBackgroundColor(getResources().getColor(R.color.dark_background));
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
    }

    private void updateGraph() {
        String selectedRange = spinnerXAxisRange.getSelectedItem().toString();
        Map<Long, Integer> groupedData = groupDataPointsByTimeSegment(selectedRange);

        List<Entry> entries = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : groupedData.entrySet()) {
            entries.add(new Entry(entry.getKey(), entry.getValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Data Points");
        dataSet.setColor(Color.CYAN);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.CYAN);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh
    }

    private Map<Long, Integer> groupDataPointsByTimeSegment(String timeSegment) {
        Map<Long, Integer> groupedData = new TreeMap<>();
        long timeUnit;

        switch (timeSegment) {
            case "Minutes":
                timeUnit = TimeUnit.MINUTES.toMillis(1);
                break;
            case "Hours":
                timeUnit = TimeUnit.HOURS.toMillis(1);
                break;
            case "Days":
                timeUnit = TimeUnit.DAYS.toMillis(1);
                break;
            case "Weeks":
                timeUnit = TimeUnit.DAYS.toMillis(7);
                break;
            default:
                timeUnit = TimeUnit.DAYS.toMillis(1);
                break;
        }

        for (Date date : dataPoints) {
            long timeKey = date.getTime() / timeUnit;
            groupedData.put(timeKey, groupedData.getOrDefault(timeKey, 0) + 1);
        }

        return groupedData;
    }
}