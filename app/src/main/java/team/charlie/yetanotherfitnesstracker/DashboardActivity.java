package team.charlie.yetanotherfitnesstracker;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import team.charlie.yetanotherfitnesstracker.api.GetActivitiesByTimeRequest;
import team.charlie.yetanotherfitnesstracker.api.LogoutRequest;
import team.charlie.yetanotherfitnesstracker.api.UserProfileRequest;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivityBackupStatus;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;
import team.charlie.yetanotherfitnesstracker.database.entities.WeightItem;
import team.charlie.yetanotherfitnesstracker.ui.AchievementsActivity;
import team.charlie.yetanotherfitnesstracker.ui.UserProfileActivity;


public class DashboardActivity extends AppCompatActivity implements ApiClientActivity {
    private static final String TAG = "DashboardActivity";
    private DrawerLayout drawerLayout;
    public static String CHANNEL_ID = "YetAnotherFitnessTracker";
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);

        Toolbar toolbar = findViewById(R.id.toolbar_support);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_activity, R.id.navigation_dashboard, R.id.navigation_community)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        setupTransitionEventListener();
        setupLocationEventListener();
        setupBackupTask();
        setupInactiveCheckListener();
        createNotificationChannel();
        setupNavigationView();

        if (!isMyServiceRunning()) {
            Intent serviceIntent = new Intent(this, ActivityTransitionReceiverService.class);
            startService(serviceIntent);
        }

        if (sharedPref.contains("FIRST_LOGIN_AFTER_REGISTER")) {
            sharedPref.edit().remove("FIRST_LOGIN_AFTER_REGISTER").apply();

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent myIntent = new Intent(this.getApplicationContext(), UserProfileActivity.class);
                        this.startActivity(myIntent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please setup your User profile!").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("Later", dialogClickListener).show();
        }

        if (sharedPref.contains("ACTIVITY_SYNC_AFTER_FIRST_LOGIN")) {
            if (FitnessUtility.isNetworkAvailable(this.getApplicationContext())) {
                GetActivitiesByTimeRequest getActivitiesByTimeRequest = new GetActivitiesByTimeRequest(this, this.getApplicationContext());
                getActivitiesByTimeRequest.getActivities(FitnessUtility.getNMinusTodayStartInMilliseconds(7), FitnessUtility.getNMinusTodayEndInMilliseconds(0));
            }
        }

        if (sharedPref.contains("PROFILE_SYNC_AFTER_FIRST_LOGIN")) {
            UserProfileRequest userProfileRequest = new UserProfileRequest(this, this.getApplicationContext());
            userProfileRequest.getUserProfile();
        }
    }

    @Override
    public void onGetActivitiesByTimeSuccess(ArrayList<FitnessActivity> fitnessActivities, ArrayList<String> remoteIds, ArrayList<LocationWithStepCount> locationWithStepCounts) {
        sharedPref.edit().remove("ACTIVITY_SYNC_AFTER_FIRST_LOGIN").apply();
        new SaveActivitiesAsyncTask(new WeakReference<>(this.getApplicationContext()), fitnessActivities, remoteIds, locationWithStepCounts).execute();
    }

    @Override
    public void onUserProfileSuccess(String profile) {
        sharedPref.edit().remove("PROFILE_SYNC_AFTER_FIRST_LOGIN").apply();
        new SaveProfileAsyncTask(new WeakReference<>(this.getApplicationContext()), profile).execute();
    }


    private void setupNavigationView() {
        NavigationView navigationView = findViewById(R.id.navigation);
        Button logoutButton = navigationView.getHeaderView(0).findViewById(R.id.navigation_fragment_logout);
        logoutButton.setOnClickListener(v -> {
            String email = null;
            if (sharedPref.contains(UserProfile.EMAIL)) {
                email = sharedPref.getString(UserProfile.EMAIL, "");
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            if (email != null) {
                editor.putString(UserProfile.EMAIL, email);
            }
            editor.apply();

            new ClearDatabasesAsyncTask(new WeakReference<>(this.getApplicationContext())).execute();

            LogoutRequest logoutRequest = new LogoutRequest(this, this.getApplicationContext());
            logoutRequest.logout();

            Toast.makeText(this,
                    "Logged out !!", Toast.LENGTH_SHORT).show();
            transitionToMainActivity();
        });

        Button profileButton = navigationView.getHeaderView(0).findViewById(R.id.navigation_fragment_profile);
        profileButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(this.getApplicationContext(), UserProfileActivity.class);
            this.startActivity(myIntent);
        });

        Button achievementsButton = navigationView.getHeaderView(0).findViewById(R.id.navigation_fragment_achievements);
        achievementsButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(this.getApplicationContext(), AchievementsActivity.class);
            this.startActivity(myIntent);
        });

        Button backupButton = navigationView.getHeaderView(0).findViewById(R.id.navigation_fragment_backup);
        backupButton.setOnClickListener(v -> {
            BackupTask backupTask = BackupTask.getInstance(this);
            backupTask.triggerBackup();
        });
    }

    void transitionToMainActivity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        this.startActivity(myIntent);
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ActivityTransitionReceiverService.class.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "isMyServiceRunning: ActivityTransitionReceiverService already running!");
                return true;
            }
        }
        return false;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Yet Another Fitness Tracker",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void setupTransitionEventListener() {
        ActivityTransitionRegister activityTransitionRegister = new ActivityTransitionRegister(this.getApplicationContext());
        activityTransitionRegister.registerForActivityTransitions();
    }

    private void setupLocationEventListener() {
        LocationRegister locationRegister = LocationRegister.getInstance();
        locationRegister.registerForLocationUpdates(this.getApplicationContext());
    }

    private void setupBackupTask() {
        BackupTaskRegister backupTaskRegister = BackupTaskRegister.getInstance();
        backupTaskRegister.registerNextBackupTask(this.getApplicationContext());
    }

    private void setupInactiveCheckListener() {
        InactiveStatusCheckRegister inactiveStatusCheckRegister = InactiveStatusCheckRegister.getInstance();
        inactiveStatusCheckRegister.registerNextInactiveCheck(this.getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }


    private static class SaveActivitiesAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<Context> context;
        List<FitnessActivity> activitiesList;
        List<String> remoteIds;
        List<LocationWithStepCount> locationWithStepCounts;

        public SaveActivitiesAsyncTask(WeakReference<Context> context, List<FitnessActivity> activitiesList, List<String> remoteIds, List<LocationWithStepCount> locationWithStepCounts) {
            this.context = context;
            this.activitiesList = activitiesList;
            this.remoteIds = remoteIds;
            this.locationWithStepCounts = locationWithStepCounts;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            for (int i=0; i<activitiesList.size(); i++) {
                FitnessActivity fitnessActivity = activitiesList.get(i);
                fitnessDatabase.fitnessActivityDao().insert(fitnessActivity);

                List<FitnessActivity> latestActivity = fitnessDatabase.fitnessActivityDao().getLatestActivity();
                fitnessDatabase.fitnessActivityBackupStatusDao().insert(new FitnessActivityBackupStatus(latestActivity.get(0).getId(), 1, 0, 0, remoteIds.get(i)));
            }

            for (LocationWithStepCount locationWithStepCount : locationWithStepCounts) {
                fitnessDatabase.locationWithStepCountDao().insert(locationWithStepCount);
            }

            return null;
        }
    }

    private static class ClearDatabasesAsyncTask extends AsyncTask<Integer, Void, Void> {
        WeakReference<Context> context;

        public ClearDatabasesAsyncTask(WeakReference<Context> context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            fitnessDatabase.purgeDatabase();
            return null;
        }
    }

    private static class SaveProfileAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<Context> context;
        String profile;

        public SaveProfileAsyncTask(WeakReference<Context> context, String profile) {
            this.context = context;
            this.profile = profile;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            SharedPreferences sharedPref = Objects.requireNonNull(context.get()).getSharedPreferences(
                    context.get().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            try {
                JSONObject jsonResponse = new JSONObject(profile);
                JSONObject profile = jsonResponse.getJSONObject("result");
                UserProfile userProfile = new UserProfile();
                userProfile.setName(profile.getString("name"));
                userProfile.setAge(profile.getInt("age"));
                String gender;
                if (profile.getString("gender").equalsIgnoreCase("M")) {
                    gender = "MALE";
                } else {
                    gender = "FEMALE";
                }
                userProfile.setGender(gender);
                userProfile.setHeight(Float.parseFloat(profile.getString("height")));
                userProfile.setStrideLength(FitnessUtility.getStrideLengthInMeters(userProfile.getHeight()));
                userProfile.setStepGoal(profile.getInt("stepGoal"));
                userProfile.setDistanceGoal(profile.getInt("distanceGoal"));
                userProfile.setSleepGoal(profile.getInt("sleepGoal"));
                userProfile.setCaloriesGoal(profile.getInt("calorieGoal"));
                userProfile.setActiveTimeGoal(profile.getInt("activityGoal"));
                userProfile.saveUserProfileToSharedPreferences(sharedPref);

                JSONArray weights = profile.getJSONArray("weights");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
                for (int i = 0; i < weights.length(); i++) {
                    JSONObject weightObject = weights.getJSONObject(i);
                    long timeStamp = sdf.parse(weightObject.getString("date")).getTime();
                    WeightItem weightItem = new WeightItem(timeStamp, Float.parseFloat(weightObject.getString("weight")), 1, 0, 0, weightObject.getString("_id"));
                    fitnessDatabase.weightItemDao().insert(weightItem);
                }

                List<WeightItem> weightItems = fitnessDatabase.weightItemDao().getLatestWeightItem();
                sharedPref.edit().putFloat(UserProfile.WEIGHT, weightItems.get(0).getWeight()).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}

