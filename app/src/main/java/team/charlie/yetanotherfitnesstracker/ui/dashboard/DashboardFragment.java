package team.charlie.yetanotherfitnesstracker.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.UserProfile;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;
import team.charlie.yetanotherfitnesstracker.ui.history.WeeklyHistoryActivity;

public class DashboardFragment extends Fragment implements ApiClientActivity {

    private static final String TAG = "DashboardFragment";

    private SharedPreferences sharedPref;
    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        sharedPref = Objects.requireNonNull(this.getContext()).getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        setupDashboardTheme(root);
        setupTime(root);
        setupProgressGoals(root);
        setupLayoutEventListeners(root);

        rootView = root;

        return root;
    }

    @Override
    public void onResume() {
        new DailySummaryAsyncTask(new WeakReference<>(rootView), new WeakReference<>(getContext())).execute();
        super.onResume();
    }

    private void setupTime(View root) {
        Calendar currentTime = Calendar.getInstance();
        String day = currentTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String month = currentTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        int date = currentTime.get(Calendar.DAY_OF_MONTH);
        TextView dashboardDate = root.findViewById(R.id.fragment_dashboard_date);
        String displayTime = day + ", " + month + " " + date;
        dashboardDate.setText(displayTime);
    }

    private void setupDashboardTheme(View root) {
        Calendar currentTime = Calendar.getInstance();
        ImageView imageView = root.findViewById(R.id.fragment_dashboard_background_image);
        ImageView imageViewStep = root.findViewById(R.id.fragment_dashboard_image_steps);
        ImageView imageViewLocation = root.findViewById(R.id.fragment_dashboard_image_location_pin);
        TextView dashboardDate = root.findViewById(R.id.fragment_dashboard_date);
        TextView dashboardLocation = root.findViewById(R.id.fragment_dashboard_location);
        TextView dashboardStepCount = root.findViewById(R.id.fragment_dashboard_stepcount);
        TextView dashboardStepCountGoal = root.findViewById(R.id.fragment_dashboard_step_goal);
        TextView dashboardStepLabel = root.findViewById(R.id.fragment_dashboard_step_label);
        ProgressBar progressBarSteps = root.findViewById(R.id.fragment_dashboard_step_progress);

        if (currentTime.get(Calendar.HOUR_OF_DAY) <= 5 || currentTime.get(Calendar.HOUR_OF_DAY) >= 18) {
            imageView.setImageResource(R.drawable.night);
            imageViewLocation.setImageResource(R.drawable.ic_location_pin_light);
            imageViewStep.setImageResource(R.drawable.ic_steps_light);
            dashboardDate.setTextColor(ContextCompat.getColor(root.getContext(), R.color.app_main_primary));
            dashboardLocation.setTextColor(ContextCompat.getColor(root.getContext(), R.color.app_main_primary));
            dashboardStepCount.setTextColor(ContextCompat.getColor(root.getContext(), R.color.app_main_primary));
            dashboardStepCountGoal.setTextColor(ContextCompat.getColor(root.getContext(), R.color.app_main_primary));
            dashboardStepLabel.setTextColor(ContextCompat.getColor(root.getContext(), R.color.app_main_primary));
            progressBarSteps.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(root.getContext(), R.color.app_main_primary_dull)));
        } else {
            progressBarSteps.setProgressTintList(ColorStateList.valueOf(Color.BLACK));
        }
    }

    private void setupProgressGoals(View root) {
        ProgressBar progressBarSteps = root.findViewById(R.id.fragment_dashboard_step_progress);
        progressBarSteps.setMax(sharedPref.getInt(UserProfile.STEP_GOAL, 0));

        ProgressBar progressBarDistance = root.findViewById(R.id.fragment_dashboard_distance_progress);
        progressBarDistance.setMax(sharedPref.getInt(UserProfile.DISTANCE_GOAL, 0));
        progressBarDistance.setProgressTintList(ColorStateList.valueOf(Color.BLACK));

        ProgressBar progressBarCalories = root.findViewById(R.id.fragment_dashboard_calories_progress);
        progressBarCalories.setMax(sharedPref.getInt(UserProfile.CALORIES_GOAL, 0));
        progressBarCalories.setProgressTintList(ColorStateList.valueOf(Color.BLACK));

        ProgressBar progressBarActiveMinutes = root.findViewById(R.id.fragment_dashboard_active_minutes_progress);
        progressBarActiveMinutes.setMax(sharedPref.getInt(UserProfile.ACTIVE_TIME_GOAL, 0));
        progressBarActiveMinutes.setProgressTintList(ColorStateList.valueOf(Color.BLACK));

        ProgressBar progressBarSleep = root.findViewById(R.id.fragment_dashboard_sleep_progress);
        progressBarSleep.setMax(sharedPref.getInt(UserProfile.SLEEP_GOAL, 0));
        progressBarSleep.setProgressTintList(ColorStateList.valueOf(Color.BLACK));

        TextView textViewStepCount = root.findViewById(R.id.fragment_dashboard_step_goal);
        textViewStepCount.setText(String.valueOf(sharedPref.getInt(UserProfile.STEP_GOAL, 0)));

        TextView textViewDistance = root.findViewById(R.id.fragment_dashboard_distance_goal);
        textViewDistance.setText(String.valueOf(sharedPref.getInt(UserProfile.DISTANCE_GOAL, 0)));

        TextView textViewCalories = root.findViewById(R.id.fragment_dashboard_calories_goal);
        textViewCalories.setText(String.valueOf(sharedPref.getInt(UserProfile.CALORIES_GOAL, 0)));

        TextView textViewActiveTime = root.findViewById(R.id.fragment_dashboard_active_minutes_goal);
        textViewActiveTime.setText(String.valueOf(sharedPref.getInt(UserProfile.ACTIVE_TIME_GOAL, 0)));

        TextView textViewSleep = root.findViewById(R.id.fragment_dashboard_sleep_goal);
        textViewSleep.setText(String.valueOf(sharedPref.getInt(UserProfile.SLEEP_GOAL, 0)));

    }

    private void setupLayoutEventListeners(View root) {
        ConstraintLayout stepsLayout = root.findViewById(R.id.fragment_dashboard_layout_steps);
        stepsLayout.setOnClickListener(v -> {
            Intent myIntent = new Intent(DashboardFragment.this.getContext(), WeeklyHistoryActivity.class);
            myIntent.putExtra("FITNESS_PARAMETER", FitnessUtility.FITNESS_PARAMETER_STEPS);
            myIntent.putExtra("PARAMETER_GOAL", sharedPref.getInt(UserProfile.STEP_GOAL, 0));
            DashboardFragment.this.startActivity(myIntent);
        });

        ConstraintLayout distanceLayout = root.findViewById(R.id.fragment_dashboard_layout_distance);
        distanceLayout.setOnClickListener(v -> {
            Intent myIntent = new Intent(DashboardFragment.this.getContext(), WeeklyHistoryActivity.class);
            myIntent.putExtra("FITNESS_PARAMETER", FitnessUtility.FITNESS_PARAMETER_DISTANCE);
            myIntent.putExtra("PARAMETER_GOAL", sharedPref.getInt(UserProfile.DISTANCE_GOAL, 0));
            DashboardFragment.this.startActivity(myIntent);
        });

        ConstraintLayout caloriesLayout = root.findViewById(R.id.fragment_dashboard_layout_calories);
        caloriesLayout.setOnClickListener(v -> {
            Intent myIntent = new Intent(DashboardFragment.this.getContext(), WeeklyHistoryActivity.class);
            myIntent.putExtra("FITNESS_PARAMETER", FitnessUtility.FITNESS_PARAMETER_CALORIES);
            myIntent.putExtra("PARAMETER_GOAL", sharedPref.getInt(UserProfile.CALORIES_GOAL, 0));
            DashboardFragment.this.startActivity(myIntent);
        });

        ConstraintLayout activeTimeLayout = root.findViewById(R.id.fragment_dashboard_layout_active_time);
        activeTimeLayout.setOnClickListener(v -> {
            Intent myIntent = new Intent(DashboardFragment.this.getContext(), WeeklyHistoryActivity.class);
            myIntent.putExtra("FITNESS_PARAMETER", FitnessUtility.FITNESS_PARAMETER_ACTIVE_MINUTES);
            myIntent.putExtra("PARAMETER_GOAL", sharedPref.getInt(UserProfile.ACTIVE_TIME_GOAL, 0));
            DashboardFragment.this.startActivity(myIntent);
        });

        ConstraintLayout sleepLayout = root.findViewById(R.id.fragment_dashboard_layout_sleep);
        sleepLayout.setOnClickListener(v -> {
            Intent myIntent = new Intent(DashboardFragment.this.getContext(), WeeklyHistoryActivity.class);
            myIntent.putExtra("FITNESS_PARAMETER", FitnessUtility.FITNESS_PARAMETER_SLEEP);
            myIntent.putExtra("PARAMETER_GOAL", sharedPref.getInt(UserProfile.SLEEP_GOAL, 0));
            DashboardFragment.this.startActivity(myIntent);
        });
    }

    private static class DailySummaryAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<View> root;
        WeakReference<Context> context;
        float stepProgress;
        float distanceProgress;
        float caloriesProgress;
        float activeTimeProgress;
        float sleepProgress;
        double latitude;
        double longitude;
        String displayLocation = "Unknown";

        DailySummaryAsyncTask(WeakReference<View> root, WeakReference<Context> context) {
            this.root = root;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            List<FitnessActivity> fitnessActivitiesToday = fitnessDatabase.fitnessActivityDao().
                    getAllActivities(FitnessUtility.getNMinusTodayStartInMilliseconds(0),
                            FitnessUtility.getNMinusTodayEndInMilliseconds(0));
            stepProgress = FitnessUtility.aggregateFitnessActivitiesOnParameter(fitnessActivitiesToday, FitnessUtility.FITNESS_PARAMETER_STEPS);
            distanceProgress = FitnessUtility.aggregateFitnessActivitiesOnParameter(fitnessActivitiesToday, FitnessUtility.FITNESS_PARAMETER_DISTANCE);
            caloriesProgress = FitnessUtility.aggregateFitnessActivitiesOnParameter(fitnessActivitiesToday, FitnessUtility.FITNESS_PARAMETER_CALORIES);
            activeTimeProgress = FitnessUtility.aggregateFitnessActivitiesOnParameter(fitnessActivitiesToday, FitnessUtility.FITNESS_PARAMETER_ACTIVE_MINUTES);
            sleepProgress = FitnessUtility.aggregateFitnessActivitiesOnParameter(fitnessActivitiesToday, FitnessUtility.FITNESS_PARAMETER_SLEEP);

            long currentTime = new Date().getTime();
            List<LocationWithStepCount> locationWithStepCounts = fitnessDatabase.locationWithStepCountDao().getAllLocationWithStepCount(currentTime - 120000, currentTime);
            if (locationWithStepCounts.size() > 0) {
                LocationWithStepCount latestLocationWithStepCount = locationWithStepCounts.get(locationWithStepCounts.size() - 1);
                latitude = latestLocationWithStepCount.getLatitude();
                longitude = latestLocationWithStepCount.getLongitude();
            }
            if (FitnessUtility.isNetworkAvailable(context.get())) {
                getDisplayLocation();
            }
            getDisplayLocation();
            return null;
        }

        void getDisplayLocation() {
            Geocoder geocoder = new Geocoder(context.get(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                Log.d(TAG, "displayLocation: " + address);
                displayLocation = address.getLocality() + ", " + address.getAdminArea();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            TextView textViewLocation = root.get().findViewById(R.id.fragment_dashboard_location);
            textViewLocation.setText(displayLocation);

            TextView textViewStepCount = root.get().findViewById(R.id.fragment_dashboard_stepcount);
            TextView textViewDistance = root.get().findViewById(R.id.fragment_dashboard_distance);
            TextView textViewCalories = root.get().findViewById(R.id.fragment_dashboard_calories);
            TextView textViewActiveTime = root.get().findViewById(R.id.fragment_dashboard_active_minutes);
            TextView textViewSleep = root.get().findViewById(R.id.fragment_dashboard_sleep);
            TextView textViewInactive = root.get().findViewById(R.id.fragment_dashboard_inactive);

            textViewStepCount.setText(String.valueOf(Math.round(stepProgress)));
            textViewDistance.setText(String.valueOf(Math.round(distanceProgress)));
            textViewCalories.setText(String.valueOf(Math.round(caloriesProgress)));
            textViewActiveTime.setText(String.valueOf(Math.round(activeTimeProgress)));
            textViewSleep.setText(String.valueOf(Math.round(sleepProgress)));
            textViewInactive.setText(String.valueOf(Math.round((1440-(activeTimeProgress+(sleepProgress*60)))/60)));

            ProgressBar progressBarSteps = root.get().findViewById(R.id.fragment_dashboard_step_progress);
            progressBarSteps.setProgress(Math.round(stepProgress));

            ProgressBar progressBarDistance = root.get().findViewById(R.id.fragment_dashboard_distance_progress);
            progressBarDistance.setProgress(Math.round(distanceProgress));

            ProgressBar progressBarCalories = root.get().findViewById(R.id.fragment_dashboard_calories_progress);
            progressBarCalories.setProgress(Math.round(caloriesProgress));

            ProgressBar progressBarActiveMinutes = root.get().findViewById(R.id.fragment_dashboard_active_minutes_progress);
            progressBarActiveMinutes.setProgress(Math.round(activeTimeProgress));

            ProgressBar progressBarSleep = root.get().findViewById(R.id.fragment_dashboard_sleep_progress);
            progressBarSleep.setProgress(Math.round(sleepProgress));
        }
    }
}