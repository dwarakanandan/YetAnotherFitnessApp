package team.charlie.yetanotherfitnesstracker.ui.history;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;

public class HistoryMonthFragment extends Fragment {

    private static final String TAG = "HistoryMonthFragment";

    private String fitnessParameter;
    private BarChart barChart;
    private View root;

    public HistoryMonthFragment(String fitnessParameter) {
        this.fitnessParameter = fitnessParameter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_activity_history_month, container, false);
        barChart = root.findViewById(R.id.activity_history_month_bar_chart);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        new MonthlyHistoryAsyncTask(new WeakReference<>(this)).execute();
    }

    private static class MonthlyHistoryAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<HistoryMonthFragment> weeklyHistoryActivity;
        private ArrayList<BarEntry> barEntries = new ArrayList<>();
        private ArrayList<Float> aggregateEntries = new ArrayList<>();

        MonthlyHistoryAsyncTask(WeakReference<HistoryMonthFragment> weeklyHistoryActivity) {
            this.weeklyHistoryActivity = weeklyHistoryActivity;
        }

        @Override
        protected Void doInBackground(Integer... integers) {

            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(weeklyHistoryActivity.get().getContext());
            List<FitnessActivity> currentFitnessActivitiesList;
            float xAxis = 6f;

            for (int i = -1; i < 5; i++) {
                currentFitnessActivitiesList = fitnessDatabase.fitnessActivityDao().
                        getAllActivities(getNMinusMonthStartInMilliseconds(i+1),
                                getNMinusMonthStartInMilliseconds(i));
                float aggregate = FitnessUtility.aggregateFitnessActivitiesOnParameter(currentFitnessActivitiesList, weeklyHistoryActivity.get().fitnessParameter);
                barEntries.add(new BarEntry(xAxis--, aggregate));
                aggregateEntries.add(0, aggregate);
            }

            return null;
        }

        long getNMinusMonthStartInMilliseconds(int n) {
            Calendar cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.MONTH, 0 - n);
            Date todayStart = cal.getTime();
            return todayStart.getTime();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setupBarChart();
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

        void setupBarChart() {
            BarChart barChart = weeklyHistoryActivity.get().barChart;
            BarDataSet barDataSet = new BarDataSet(barEntries, getUnit());

            barDataSet.setColor(weeklyHistoryActivity.get().getContext().getColor(R.color.app_main_secondary));
            barDataSet.setHighlightEnabled(true);
            barDataSet.setHighLightColor(Color.BLACK);
            barDataSet.setValueTextSize(12f);


            YAxis rightAxis = barChart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setEnabled(false);

            YAxis leftAxis = barChart.getAxisLeft();
            //leftAxis.setDrawGridLines(false);
            leftAxis.setTextSize(12f);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(Math.round(Collections.max(aggregateEntries)));

            XAxis xAxis = barChart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(12f);


            ArrayList<String> months = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -6);
            for (int i = 0; i < 7; i++) {
                months.add(cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
                cal.add(Calendar.MONTH, 1);
            }


            ValueFormatter formatter = new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return months.get((int) value);
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
            barChart.invalidate();
        }
    }
}
