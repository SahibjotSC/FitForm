package com.example.fitform;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;

import com.example.fitform.exercise.helper.DataObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.Locale;

import android.widget.SeekBar;

public class GraphFragment extends Fragment {

    private LineChart lineChart;
    private SeekBar sliderXAxisRange;
    private List<Date> dataPointsSquats;
    private List<Date> dataPointsPushups;
    private List<Date> dataPointsLunges;
    private List<Date> dataPointsJumpingJacks;
    private List<String> xAxisLabels = new ArrayList<>();
    private static final int MAX_DISPLAYED_POINTS = 15; // Maximum number of data points to display at once

    // Colors for the different datasets
    private static final int COLOR_SQUATS = Color.CYAN;
    private static final int COLOR_PUSHUPS = Color.GREEN;
    private static final int COLOR_LUNGES = Color.YELLOW;
    private static final int COLOR_JUMPINGJACK = Color.MAGENTA;

    private static final String TAG = "GraphFragment";

    public GraphFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String exerciseName = getArguments() != null ? getArguments().getString("exerciseName") : "";

        dataPointsSquats = new ArrayList<>();
        dataPointsPushups = new ArrayList<>();
        dataPointsLunges = new ArrayList<>();
        dataPointsJumpingJacks = new ArrayList<>();

        if (exerciseName == "")
        {
            DataObject dataObject = MainActivity.getDataObject(getContext(), "Squats");
            dataPointsSquats = dataObject.getDateTimes();

            dataObject = MainActivity.getDataObject(getContext(), "Pushups");
            dataPointsPushups = dataObject.getDateTimes();

            dataObject = MainActivity.getDataObject(getContext(), "Lunges");
            dataPointsLunges = dataObject.getDateTimes();

            dataObject = MainActivity.getDataObject(getContext(), "JumpingJacks");
            dataPointsJumpingJacks = dataObject.getDateTimes();
        } else
        {
            switch (exerciseName) {
                case "Squats":
                    DataObject dataObject = MainActivity.getDataObject(getContext(), "Squats");
                    dataPointsSquats = dataObject.getDateTimes();
                    break;
                case "Pushups":
                    dataObject = MainActivity.getDataObject(getContext(), "Pushups");
                    dataPointsPushups = dataObject.getDateTimes();
                    break;
                case "Lunges":
                    dataObject = MainActivity.getDataObject(getContext(), "Lunges");
                    dataPointsLunges = dataObject.getDateTimes();
                    break;
                case "JumpingJacks":
                    dataObject = MainActivity.getDataObject(getContext(), "JumpingJacks");
                    dataPointsJumpingJacks = dataObject.getDateTimes();
                    break;
                default:
                    Log.w(TAG, "Unknown exercise name: " + exerciseName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        lineChart = view.findViewById(R.id.line_chart);
        sliderXAxisRange = view.findViewById(R.id.slider_x_axis_range);

        sliderXAxisRange.setProgress(50);

        ArrayAdapter<CharSequence> adapterGraphType = ArrayAdapter.createFromResource(getContext(),
                R.array.graph_types, android.R.layout.simple_spinner_item);
        adapterGraphType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sliderXAxisRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateGraph();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        customizeLineChart();
        updateGraph();
        return view;
    }

    private void customizeLineChart() {
        lineChart.setBackgroundColor(getResources().getColor(R.color.dark_background));
        lineChart.getDescription().setEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);

        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.DKGRAY);
        xAxis.setLabelRotationAngle(67.5f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(16, true); // Force exactly 8 labels

        // Customize Y-axis (left)
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextSize(12f);
        lineChart.getAxisLeft().setDrawAxisLine(true);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setGridColor(Color.DKGRAY);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setGranularity(1f);

        // Customize Y-axis (right)
        lineChart.getAxisRight().setEnabled(false);

        // Customize legend
        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.getLegend().setTextSize(12f);
        lineChart.getLegend().setForm(com.github.mikephil.charting.components.Legend.LegendForm.CIRCLE);
        lineChart.getLegend().setWordWrapEnabled(true);

        // Set axis description
        lineChart.getDescription().setEnabled(true);
        lineChart.getDescription().setText("Time");
        lineChart.getDescription().setTextColor(Color.WHITE);
        lineChart.getDescription().setTextSize(12f);
    }

    private void updateGraph() {
        if (getContext() == null || lineChart == null) return;

        try {
            Log.d(TAG, "Starting updateGraph");

            int progress = sliderXAxisRange.getProgress();
            Log.d(TAG, "Slider progress: " + progress);

            // Get time unit and window from slider position
            long timeUnit = getTimeUnitFromProgress(progress);
            long timeWindow = getTimeWindowFromProgress(progress);

            Log.d(TAG, "Time unit: " + timeUnit + " ms, Time window: " + timeWindow + " ms");

            // Calculate cutoff time based on time window
            long currentTime = System.currentTimeMillis();
            long cutoffTime = currentTime - timeWindow;

            // Group data points for each dataset
            Map<Long, Integer> squatsData = groupDataPointsByTimeUnit(dataPointsSquats, timeUnit, cutoffTime);
            Map<Long, Integer> pushupsData = groupDataPointsByTimeUnit(dataPointsPushups, timeUnit, cutoffTime);
            Map<Long, Integer> lungesData = groupDataPointsByTimeUnit(dataPointsLunges, timeUnit, cutoffTime);
            Map<Long, Integer> jumpingJacksData = groupDataPointsByTimeUnit(dataPointsJumpingJacks, timeUnit, cutoffTime);

            // Combine all timekeys to create a unified x-axis
            TreeMap<Long, Boolean> allTimeKeys = new TreeMap<>();
            for (Long key : squatsData.keySet()) allTimeKeys.put(key, true);
            for (Long key : pushupsData.keySet()) allTimeKeys.put(key, true);
            for (Long key : lungesData.keySet()) allTimeKeys.put(key, true);
            for (Long key : jumpingJacksData.keySet()) allTimeKeys.put(key, true);

            if (allTimeKeys.isEmpty()) {
                Log.w(TAG, "No data points within the selected time window");
                lineChart.clear();
                lineChart.invalidate();
                return;
            }

            // Clear previous labels
            xAxisLabels.clear();

            // Convert the timekeys to a sorted list (ascending order)
            List<Long> sortedTimeKeys = new ArrayList<>(allTimeKeys.keySet());
            Collections.sort(sortedTimeKeys);

            // Create entries for each dataset with most recent (highest timeKey) on the left (x=0)
            List<Entry> squatsEntries = new ArrayList<>();
            List<Entry> pushupsEntries = new ArrayList<>();
            List<Entry> runningEntries = new ArrayList<>();
            List<Entry> yogaEntries = new ArrayList<>();

            // Generate uniform x-axis labels
            int totalPoints = sortedTimeKeys.size();
            int labelInterval = Math.max(1, totalPoints / 8); // We want about 8 labels

            // Process entries in reverse order (most recent first)
            for (int i = sortedTimeKeys.size() - 1; i >= 0; i--) {
                Long timeKey = sortedTimeKeys.get(i);
                float x = sortedTimeKeys.size() - 1 - i; // Reverse the index

                // Add entries for each dataset (using 0 if no data for that time point)
                squatsEntries.add(new Entry(x, squatsData.getOrDefault(timeKey, 0)));
                pushupsEntries.add(new Entry(x, pushupsData.getOrDefault(timeKey, 0)));
                runningEntries.add(new Entry(x, lungesData.getOrDefault(timeKey, 0)));
                yogaEntries.add(new Entry(x, jumpingJacksData.getOrDefault(timeKey, 0)));

                // Add labels at regular intervals
                if ((sortedTimeKeys.size() - 1 - i) % labelInterval == 0 || i == 0 || i == sortedTimeKeys.size() - 1) {
                    String label = formatTimestamp(timeKey * timeUnit, timeUnit);
                    xAxisLabels.add(label);
                } else {
                    // Add empty labels to maintain positioning
                    xAxisLabels.add("");
                }
            }

            // Create a LineData object with multiple datasets
            LineData lineData = new LineData();

            // Add Squats dataset
            LineDataSet squatsDataSet = createDataSet(squatsEntries, "Squats", COLOR_SQUATS);
            lineData.addDataSet(squatsDataSet);

            // Add Pushups dataset
            LineDataSet pushupsDataSet = createDataSet(pushupsEntries, "Pushups", COLOR_PUSHUPS);
            lineData.addDataSet(pushupsDataSet);

            // Add Running dataset
            LineDataSet runningDataSet = createDataSet(runningEntries, "Lunges", COLOR_LUNGES);
            lineData.addDataSet(runningDataSet);

            // Add Yoga dataset
            LineDataSet yogaDataSet = createDataSet(yogaEntries, "JumpingJacks", COLOR_JUMPINGJACK);
            lineData.addDataSet(yogaDataSet);

            // Set up formatter for axis labels - using our prepared labels
            lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
            lineChart.getXAxis().setLabelCount(8, false); // Show about 8 labels

            // Set data and refresh
            lineChart.setData(lineData);

            // Set the visible range based on the number of points
            int visibleRange = Math.min(MAX_DISPLAYED_POINTS, sortedTimeKeys.size());
            lineChart.setVisibleXRangeMaximum(visibleRange);

            // Move to the start of the data (most recent)
            lineChart.moveViewToX(0);

            // Update chart title based on time unit
            lineChart.getDescription().setText(getChartTitle(timeUnit));

            lineChart.invalidate(); // refresh
            Log.d(TAG, "Chart updated successfully with " + sortedTimeKeys.size() + " data points");

        } catch (Exception e) {
            Log.e(TAG, "Error updating graph", e);
        }
    }

    private LineDataSet createDataSet(List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(color);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setFillAlpha(40);
        dataSet.setDrawValues(false); // Don't draw values on points to reduce clutter

        return dataSet;
    }

    private String getChartTitle(long timeUnit) {
        if (timeUnit <= TimeUnit.MINUTES.toMillis(1)) {
            return "Activity per Minute";
        } else if (timeUnit <= TimeUnit.HOURS.toMillis(1)) {
            return "Activity per Hour";
        } else if (timeUnit <= TimeUnit.DAYS.toMillis(1)) {
            return "Activity per Day";
        } else if (timeUnit <= TimeUnit.DAYS.toMillis(7)) {
            return "Activity per Week";
        } else {
            return "Activity per Month";
        }
    }

    private String formatTimestamp(long timestamp, long timeUnit) {
        SimpleDateFormat sdf;

        if (timeUnit <= TimeUnit.MINUTES.toMillis(1)) {
            // For minute-level data, show hours:minutes:seconds
            sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        } else if (timeUnit <= TimeUnit.MINUTES.toMillis(10)) {
            // For 10-minute level data, show hours:minutes
            sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        } else if (timeUnit <= TimeUnit.HOURS.toMillis(1)) {
            // For hour-level data, show day and hour
            sdf = new SimpleDateFormat("EEE, hh:mm a", Locale.getDefault());
        } else if (timeUnit <= TimeUnit.HOURS.toMillis(6)) {
            // For multi-hour level data, show day and hour
            sdf = new SimpleDateFormat("EEE, MMM dd, hh a", Locale.getDefault());
        } else if (timeUnit <= TimeUnit.DAYS.toMillis(1)) {
            // For day-level data, show month and day
            sdf = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
        } else if (timeUnit <= TimeUnit.DAYS.toMillis(7)) {
            // For week-level data, show month and day
            sdf = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
        } else {
            // For month-level data, show month and year
            sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        }

        return sdf.format(new Date(timestamp));
    }

    private long getTimeUnitFromProgress(int progress) {
        // Adjust time unit range from 1 hour to 1 month
        double minHours = 1; // 1 hour
        double maxMonths = 30 * 24; // 1 month in hours
        double logMin = Math.log(minHours);
        double logMax = Math.log(maxMonths);
        double scale = logMin + (logMax - logMin) * (progress / 100.0);
        return (long) Math.exp(scale) * 60 * 60 * 1000; // Convert hours to milliseconds
    }

    private long getTimeWindowFromProgress(int progress) {
        long timeUnit = getTimeUnitFromProgress(progress);
        long minWindow = TimeUnit.DAYS.toMillis(1); // Exactly 1 day minimum
        long maxWindow = 365 * TimeUnit.DAYS.toMillis(1); // 12 months

        // Calculate window size based on slider progress
        double ratio = progress / 100.0;
        double logMin = Math.log(minWindow);
        double logMax = Math.log(maxWindow);
        double logWindow = logMin + (logMax - logMin) * ratio;

        return (long) Math.exp(logWindow);
    }

    private Map<Long, Integer> groupDataPointsByTimeUnit(List<Date> dataPoints, long timeUnit, long cutoffTime) {
        Map<Long, Integer> groupedData = new TreeMap<>();

        for (Date date : dataPoints) {
            if (date.getTime() >= cutoffTime) { // Only include data within the time window
                long timeKey = date.getTime() / timeUnit;
                groupedData.put(timeKey, groupedData.getOrDefault(timeKey, 0) + 1);
            }
        }

        return groupedData;
    }
}