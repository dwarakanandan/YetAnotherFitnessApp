package team.charlie.yetanotherfitnesstracker.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.DashboardActivity;
import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.api.ActivityInsertRequest;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.ui.FitnessActivityDetailsActivity;
import team.charlie.yetanotherfitnesstracker.ui.MapViewActivity;
import team.charlie.yetanotherfitnesstracker.ui.MapViewFullDayActivity;

public class ActivityFragment extends Fragment implements ActivityItemClickListener {

    private static final String TAG = "ActivityFragment";

    private ActivityViewModel activityViewModel;
    private View rootView;
    private static ArrayList<FitnessActivity> fitnessActivityItems;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activityViewModel =
                ViewModelProviders.of(this).get(ActivityViewModel.class);
        View root = inflater.inflate(R.layout.fragment_activity, container, false);
        rootView = root;

        FloatingActionButton floatingActionButton = root.findViewById(R.id.fragment_activity_floating_action_button);
        floatingActionButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(ActivityFragment.this.getActivity(), FitnessActivityDetailsActivity.class);
            ActivityFragment.this.startActivity(myIntent);
        });
        return root;
    }

    @Override
    public void onResume() {
        fitnessActivityItems = new ArrayList<>();
        new GetFitnessActivities(this, new WeakReference<>(this.getContext()), new WeakReference<>(rootView)).execute();
        super.onResume();
    }

    @Override
    public void onActivityClick(int position) {
        FitnessActivity i = fitnessActivityItems.get(position);
        Log.d("DWARAKA", "Clicked posisiton: "+position +" Item="+ i.getId());
        if (i.getActivityType() == FitnessUtility.SLEEP_CODE) {
            return;
        }
        Intent myIntent = new Intent(this.getActivity(), MapViewActivity.class);
        myIntent.putExtra("ACTIVITY_ID", fitnessActivityItems.get(position).getId());
        this.startActivity(myIntent);
    }

    @Override
    public void onActivityLongClick(int position) {
        FitnessActivity i = fitnessActivityItems.get(position);
        Log.d("DWARAKA", "Long clicked posisiton: "+position +" Item="+ i.getId());
        Intent myIntent = new Intent(this.getActivity(), FitnessActivityDetailsActivity.class);
        myIntent.putExtra("ACTIVITY_ID", fitnessActivityItems.get(position).getId());
        this.startActivity(myIntent);
    }

    @Override
    public void onDaySeparatorClick(int position) {
        ArrayList<Integer> activitiesToDisplay = new ArrayList<>();
        for (int i = position+1; i < fitnessActivityItems.size(); i++) {
            FitnessActivity currentActivity = fitnessActivityItems.get(i);
            if (currentActivity == null) {
                break;
            }
            activitiesToDisplay.add(currentActivity.getId());
        }
        Intent myIntent = new Intent(this.getActivity(), MapViewFullDayActivity.class);
        myIntent.putExtra("ACTIVITY_IDS", activitiesToDisplay);
        this.startActivity(myIntent);
    }

    private static class GetFitnessActivities extends AsyncTask<Integer, Void, Void> {

        ActivityFragment activityFragment;
        WeakReference<Context> context;
        WeakReference<View> view;
        private List<List<FitnessActivity>> fitnessActivitiesPastWeek = new ArrayList<>();

        GetFitnessActivities(ActivityFragment activityFragment, WeakReference<Context> context, WeakReference<View> view) {
            this.activityFragment = activityFragment;
            this.context = context;
            this.view = view;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            List<FitnessActivity> currentActivitiesList;
            for (int day = 0; day < 7; day++) {
                currentActivitiesList = fitnessDatabase.fitnessActivityDao().
                        getAllActivities(FitnessUtility.getNMinusTodayStartInMilliseconds(day),
                                FitnessUtility.getNMinusTodayEndInMilliseconds(day));
                fitnessActivitiesPastWeek.add(currentActivitiesList);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setupRecyclerView();
        }


        private boolean hasDisplayableActivity(List<FitnessActivity> currentActivitiesList) {
            for (FitnessActivity i: currentActivitiesList) {
                if (i.getActivityType() != DetectedActivity.STILL) {
                    return true;
                }
            }
            return false;
        }

        private void setupRecyclerView() {

            ArrayList<FitnessActivityItem> fitnessActivityItemsForView = new ArrayList<>();
            Calendar cal = Calendar.getInstance();

            for (int day = 0; day < 7; day++) {
                List<FitnessActivity> currentActivitiesList = fitnessActivitiesPastWeek.get(day);

                if (!hasDisplayableActivity(currentActivitiesList)) {
                    cal.add(Calendar.DATE, -1);
                    continue;
                }

                if (day == 0) {
                    fitnessActivityItemsForView.add(new FitnessActivityItem(0, "Today...","","","","",""));
                } else {
                    String dayText = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                    fitnessActivityItemsForView.add(new FitnessActivityItem(0, dayText,"","","","",""));
                }
                cal.add(Calendar.DATE, -1);

                fitnessActivityItems.add(null);

                for (int iteration = currentActivitiesList.size()-1; iteration >=0 ; iteration--) {

                    FitnessActivity i = currentActivitiesList.get(iteration);
                    //Log.d(TAG, "setupRecyclerView: " + i.getId());
                    if (i.getActivityType() == DetectedActivity.STILL) {
                        continue;
                    }

                    String endTime = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(i.getEndTimeMilliSeconds());
                    String startTime = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(i.getStartTimeMilliSeconds());

                    fitnessActivityItemsForView.add(new FitnessActivityItem(
                            FitnessUtility.getImageResourceForActivityType(i.getActivityType()),
                            FitnessUtility.getActivityDisplayName(i.getActivityType()),
                            startTime + " - " + endTime,
                            Math.round(i.getDistanceInMeters())+" m",
                            i.getTimeInMinutes()+" min",
                            String.valueOf(Math.round(i.getCaloriesBurnt())),
                            String.valueOf(Math.round(i.getStepCount()))
                    ));
                    fitnessActivityItems.add(i);
                }
            }

            ProgressBar progressBar = view.get().findViewById(R.id.fragment_activity_progress_loader);
            progressBar.setVisibility(View.GONE);

            RecyclerView recyclerView = view.get().findViewById(R.id.fragment_activity_recycler_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context.get());
            RecyclerView.Adapter adapter = new FitnessActivityItemAdapter(fitnessActivityItemsForView, activityFragment);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "Adapter count: " + adapter.getItemCount());
        }
    }
}