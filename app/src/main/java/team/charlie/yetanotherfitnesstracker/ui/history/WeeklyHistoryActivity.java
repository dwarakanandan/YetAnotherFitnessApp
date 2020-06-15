package team.charlie.yetanotherfitnesstracker.ui.history;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;

public class WeeklyHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WeeklyHistoryActivity";
    private BarChart barChart;
    private String fitnessParameter;
    private int parameterGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_history);

        Intent startingIntent = this.getIntent();
        fitnessParameter = startingIntent.getStringExtra("FITNESS_PARAMETER");
        parameterGoal = startingIntent.getIntExtra("PARAMETER_GOAL", 0);

        barChart = findViewById(R.id.weekly_history_bar_chart);

        new WeeklyHistoryAsyncTask(new WeakReference<>(this)).execute();
    }

    @Override
    public void onClick(View v) {
        Intent myIntent = new Intent(this, HistoryActivity.class);
        myIntent.putExtra("FITNESS_PARAMETER", fitnessParameter);
        startActivity(myIntent);
    }

    private static class WeeklyHistoryAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<WeeklyHistoryActivity> weeklyHistoryActivity;
        private ArrayList<BarEntry> barEntries = new ArrayList<>();
        private ArrayList<Float> aggregateEntries = new ArrayList<>();

        public WeeklyHistoryAsyncTask(WeakReference<WeeklyHistoryActivity> weeklyHistoryActivity) {
            this.weeklyHistoryActivity = weeklyHistoryActivity;
        }

        @Override
        protected Void doInBackground(Integer... integers) {

            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(weeklyHistoryActivity.get());
            List<FitnessActivity> currentFitnessActivitiesList;
            float xAxis = 6f;

            for (int i = 0; i < 7; i++) {
                currentFitnessActivitiesList = fitnessDatabase.fitnessActivityDao().
                        getAllActivities(FitnessUtility.getNMinusTodayStartInMilliseconds(i),
                                FitnessUtility.getNMinusTodayEndInMilliseconds(i));
                float aggregate = FitnessUtility.aggregateFitnessActivitiesOnParameter(currentFitnessActivitiesList, weeklyHistoryActivity.get().fitnessParameter);
                barEntries.add(new BarEntry(xAxis--, aggregate));
                aggregateEntries.add(0, aggregate);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setupBarChart();
            setupWeeklySummary();
        }

        void setupWeeklySummary() {
            TextView textViewParameter = weeklyHistoryActivity.get().findViewById(R.id.weekly_history_fitness_parameter);
            textViewParameter.setText(weeklyHistoryActivity.get().fitnessParameter);
            ArrayList<String> dates = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -6);
            for (int i = 0; i < 6; i++) {
                dates.add(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
                cal.add(Calendar.DATE, 1);
            }
            dates.add("Today");

            ArrayList<Integer> resourceIds = new ArrayList<>();
            resourceIds.add(R.id.weekly_history_day7);
            resourceIds.add(R.id.weekly_history_day6);
            resourceIds.add(R.id.weekly_history_day5);
            resourceIds.add(R.id.weekly_history_day4);
            resourceIds.add(R.id.weekly_history_day3);
            resourceIds.add(R.id.weekly_history_day2);
            resourceIds.add(R.id.weekly_history_day1);

            for (int i = 6; i >= 0; i--) {
                TextView textViewDay = weeklyHistoryActivity.get().findViewById(resourceIds.get(i));
                textViewDay.setText(dates.get(i));
            }

            ArrayList<Integer> resourceIdsValues = new ArrayList<>();
            resourceIdsValues.add(R.id.weekly_history_day7_value);
            resourceIdsValues.add(R.id.weekly_history_day6_value);
            resourceIdsValues.add(R.id.weekly_history_day5_value);
            resourceIdsValues.add(R.id.weekly_history_day4_value);
            resourceIdsValues.add(R.id.weekly_history_day3_value);
            resourceIdsValues.add(R.id.weekly_history_day2_value);
            resourceIdsValues.add(R.id.weekly_history_day1_value);


            for (int i = 6; i >= 0; i--) {
                TextView textViewDay = weeklyHistoryActivity.get().findViewById(resourceIdsValues.get(i));
                textViewDay.setText(Math.round(aggregateEntries.get(i)) + getUnit());
            }

        }

        private String getUnit() {
            String units = "";
            if (weeklyHistoryActivity.get().fitnessParameter.equals(FitnessUtility.FITNESS_PARAMETER_STEPS)) {
                units = " steps";
            } else if (weeklyHistoryActivity.get().fitnessParameter.equals(FitnessUtility.FITNESS_PARAMETER_CALORIES)) {
                units = " calories";
            } else if (weeklyHistoryActivity.get().fitnessParameter.equals(FitnessUtility.FITNESS_PARAMETER_DISTANCE)) {
                units = " meters";
            } else if (weeklyHistoryActivity.get().fitnessParameter.equals(FitnessUtility.FITNESS_PARAMETER_ACTIVE_MINUTES)) {
                units = " minutes";
            } else if (weeklyHistoryActivity.get().fitnessParameter.equals(FitnessUtility.FITNESS_PARAMETER_SLEEP)) {
                units = " hours";
            }
            return units;
        }

        private int getYAxisMax(int goal, int maxAggregate) {
            goal = goal > maxAggregate ? goal : maxAggregate;
            if (goal > 7000) {
                return goal + 2000;
            } else if (goal > 3000) {
                return goal + 1000;
            } else if (goal > 1000) {
                return goal + 100;
            } else if (goal > 500) {
                return goal + 50;
            } else if (goal > 50) {
                return goal + 20;
            } else {
                return goal + 1;
            }
        }

        void setupBarChart() {
            BarChart barChart = weeklyHistoryActivity.get().barChart;
            BarDataSet barDataSet = new BarDataSet(barEntries, getUnit());

            barDataSet.setColor(weeklyHistoryActivity.get().getColor(R.color.app_main_secondary));
            barDataSet.setHighlightEnabled(true);
            barDataSet.setHighLightColor(Color.BLACK);
            barDataSet.setDrawValues(false);


            YAxis rightAxis = barChart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setEnabled(false);

            YAxis leftAxis = barChart.getAxisLeft();
            //leftAxis.setDrawGridLines(false);
            leftAxis.setTextSize(12f);
            LimitLine limitLine = new LimitLine(weeklyHistoryActivity.get().parameterGoal, "Goal");
            limitLine.setLineColor(Color.BLACK);
            leftAxis.addLimitLine(limitLine);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(getYAxisMax(weeklyHistoryActivity.get().parameterGoal, Math.round(Collections.max(aggregateEntries))));

            XAxis xAxis = barChart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(12f);


            ArrayList<String> dates = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -6);
            for (int i = 0; i < 6; i++) {
                dates.add(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
                cal.add(Calendar.DATE, 1);
            }
            dates.add("Today");

            ValueFormatter formatter = new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return dates.get((int) value);
                }
            };
            xAxis.setValueFormatter(formatter);


            BarData barData = new BarData(barDataSet);
            barChart.setData(barData);
            barChart.setFitBars(true);
            barChart.setScaleYEnabled(false);
            barChart.setScaleXEnabled(false);
            barChart.setDoubleTapToZoomEnabled(false);
            barChart.setDrawBorders(false);
            barChart.getDescription().setEnabled(false);
            barChart.getLegend().setYOffset(-10);
            barChart.invalidate();

            barChart.setOnClickListener(weeklyHistoryActivity.get());
        }
    }
}
