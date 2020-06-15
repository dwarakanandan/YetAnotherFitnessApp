package team.charlie.yetanotherfitnesstracker.ui.weightTracking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.UserProfile;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.WeightItem;

public class WeightTrackingActivity extends AppCompatActivity implements WeightTrackingItemClickListener {

    private static final String TAG = "WeightTrackingActivity";
    ArrayList<WeightItem> weightItems;
    SharedPreferences sharedPref;
    float height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_tracking);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        height = sharedPref.getFloat(UserProfile.HEIGHT, 180) / 100;

        FloatingActionButton floatingActionButton = findViewById(R.id.activity_weight_tracking_floating_action_button);
        floatingActionButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, AddNewWeightActivity.class);
            this.startActivity(myIntent);
        });
    }

    @Override
    protected void onResume() {
        drawLayout();
        super.onResume();
    }

    void drawLayout() {
        weightItems = new ArrayList<>();
        new GetWeightItems(new WeakReference<>(this)).execute();
    }

    @Override
    public void onLongClick(int position) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    new DeleteWeightItemAsyncTask(new WeakReference<>(WeightTrackingActivity.this), weightItems.get(position)).execute();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(WeightTrackingActivity.this);
        builder.setMessage("Delete weight entry?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private static class GetWeightItems extends AsyncTask<Integer, Void, Void> {
        private WeakReference<WeightTrackingActivity> activity;

        private List<WeightItem> weightItems;

        GetWeightItems(WeakReference<WeightTrackingActivity> activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(activity.get().getApplicationContext());
            weightItems = fitnessDatabase.weightItemDao().getAllWeightItems();
            Log.d(TAG, "doInBackground: " + weightItems.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setupRecyclerView();
            activity.get().drawChart();
        }

        void setupRecyclerView() {
            ArrayList<WeightTrackingItem> weightTrackingItems = new ArrayList<>();


            java.text.DateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);

            for (WeightItem i : weightItems) {
                calendar.setTimeInMillis(i.getTimeStamp());
                WeightTrackingItem weightTrackingItem = new WeightTrackingItem(dateFormat.format(i.getTimeStamp()), df.format(i.getWeight()), df.format(activity.get().getBMI(i.getWeight())));
                weightTrackingItems.add(weightTrackingItem);
                activity.get().weightItems.add(i);
            }

            RecyclerView recyclerView = activity.get().findViewById(R.id.activity_weight_tracking_recycler_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity.get());
            RecyclerView.Adapter adapter = new WeightTrackingItemAdapter(activity.get(), weightTrackingItems);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "Adapter count: " + adapter.getItemCount());
        }
    }

    private static class DeleteWeightItemAsyncTask extends AsyncTask<Integer, Void, Void> {

        private WeakReference<WeightTrackingActivity> activity;
        private WeightItem weightItem;

        DeleteWeightItemAsyncTask(WeakReference<WeightTrackingActivity> activity, WeightItem weightItem) {
            this.activity = activity;
            this.weightItem = weightItem;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(activity.get().getApplicationContext());
            List<WeightItem> weightItems = fitnessDatabase.weightItemDao().getWeightItemById(weightItem.getId());
            if (weightItems.get(0).getBackupDone() == 0) {
                fitnessDatabase.weightItemDao().delete(weightItems.get(0));
            } else {
                weightItems.get(0).setMarkForDelete(1);
                fitnessDatabase.weightItemDao().update(weightItems.get(0));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            activity.get().drawLayout();
        }
    }

    float getBMI(float weight) {
        return (weight / (height * height));
    }

    void drawChart() {
        LineChart lineChart = findViewById(R.id.activity_weight_tracking_line_chart);


        List<Entry> weightEntries = new ArrayList<>();
        List<Entry> bmiEntries = new ArrayList<>();

        float xIndex = 0f;
        for (WeightItem weightItem : weightItems) {
            Entry weightEntry = new Entry(xIndex, weightItem.getWeight());
            weightEntries.add(weightEntry);
            Entry bmiEntry = new Entry(xIndex, getBMI(weightItem.getWeight()));
            bmiEntries.add(bmiEntry);
            xIndex++;
        }

        LineDataSet setComp1 = new LineDataSet(weightEntries, "Weight");


        LineDataSet setComp2 = new LineDataSet(bmiEntries, "BMI");


        setComp1.setColor(getColor(R.color.app_main_secondary));
        setComp1.setHighlightEnabled(true);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setValueTextSize(15f);
        setComp1.setLineWidth(5f);

        setComp2.setColor(Color.GRAY);
        setComp2.setHighlightEnabled(true);
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setValueTextSize(15f);
        setComp2.setLineWidth(5f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setTextSize(0);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextSize(0);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setTextSize(0);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextSize(15f);
        lineChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lineChart.setScaleYEnabled(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.invalidate();
    }
}
