package team.charlie.yetanotherfitnesstracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivityBackupStatus;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;

import static team.charlie.yetanotherfitnesstracker.DashboardActivity.CHANNEL_ID;

public class ActivityTransitionReceiverService extends Service {
    private static final String TAG = "ActivityTransitionRecei";
    private static BroadcastReceiver mActivityTransitionReceiver;
    private String ACTION_STOP_SERVICE = "team.charlie.yetanotherfitnesstracker.ACTION_STOP_SERVICE";
    static float stepCount;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityTransitionReceiver();
        setupStepSensor();
    }

    public static float getStepCount() {
        return stepCount;
    }

    private SensorEventListener mStepSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            stepCount = event.values[0];
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void setupStepSensor() {
        SensorManager mSensorManager;
        Sensor mStep;
        // Get sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Get the default sensor of specified type
        mStep = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (mStep != null) {
            mSensorManager.registerListener(mStepSensorListener, mStep, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d(TAG,"User killed service");
            stopSelf();
        }
        Intent notificationIntent = new Intent(this, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent stopSelf = new Intent(this, ActivityTransitionReceiverService.class);
        stopSelf.setAction(this.ACTION_STOP_SERVICE);
        PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf,PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Yet Another Fitness Tracker")
                .setContentText("Tracking activities...")
                .setSmallIcon(R.drawable.ic_run)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_run, "Stop Tracking",
                        pStopSelf)
                .build();

        startForeground(1, notification);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mActivityTransitionReceiver);
        mActivityTransitionReceiver = null;
    }

    private void registerActivityTransitionReceiver() {
        mActivityTransitionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: Registered Activity Transition Receiver");
                if (ActivityTransitionResult.hasResult(intent)) {
                    ActivityTransitionResult activityTransitionResult = ActivityTransitionResult.extractResult(intent);
                    processTransition(activityTransitionResult);
                }
            }

            void processTransition(final ActivityTransitionResult activityTransitionResult) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Calendar cal = Calendar.getInstance();
                        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
                        Date todayStart = cal.getTime();
                        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
                        Date todayEnd = cal.getTime();

                        for (ActivityTransitionEvent activityTransitionEvent : activityTransitionResult.getTransitionEvents()) {

                            if (activityTransitionEvent.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                                Log.d(TAG, "run: "+FitnessUtility.getActivityDisplayName(activityTransitionEvent.getActivityType())+ " TRANSITION_ENTER");
                            } else {
                                Log.d(TAG, "run: "+FitnessUtility.getActivityDisplayName(activityTransitionEvent.getActivityType())+ " TRANSITION_EXIT");
                            }

                            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(ActivityTransitionReceiverService.this);
                            FitnessActivity fitnessActivity;
                            if (activityTransitionEvent.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                                List<FitnessActivity> allActivities = fitnessDatabase.fitnessActivityDao()
                                        .getAllActivities(todayStart.getTime(), todayEnd.getTime());
                                if (allActivities.size() == 0 || (allActivities.get(allActivities.size() - 1).getActivityType()
                                        != activityTransitionEvent.getActivityType())) {
                                    long milliTime = new Date().getTime();
                                    fitnessActivity = new FitnessActivity(activityTransitionEvent.getActivityType(),
                                            milliTime,
                                            milliTime,
                                            0,
                                            0,
                                            0,
                                            0);
                                    fitnessDatabase.fitnessActivityDao().insert(fitnessActivity);
                                    if (fitnessActivity.getActivityType() != DetectedActivity.STILL) {
                                        List<FitnessActivity> latestActivity = fitnessDatabase.fitnessActivityDao().getLatestActivity();
                                        fitnessDatabase.fitnessActivityBackupStatusDao().insert(new FitnessActivityBackupStatus(latestActivity.get(0).getId(), 0, 0, 0, ""));
                                    }
                                }
                            } else {
                                List<FitnessActivity> allActivitiesForType = fitnessDatabase.fitnessActivityDao()
                                        .getAllActivitiesForType(activityTransitionEvent.getActivityType(), todayStart.getTime(), todayEnd.getTime());
                                fitnessActivity = allActivitiesForType.get(allActivitiesForType.size() - 1);
                                if (fitnessActivity.getStartTimeMilliSeconds() == fitnessActivity.getEndTimeMilliSeconds()) {
                                    long endTime = new Date().getTime();
                                    fitnessActivity.setEndTimeMilliSeconds(endTime);
                                    List<LocationWithStepCount> allLocationsForActivity = fitnessDatabase
                                            .locationWithStepCountDao().getAllLocationWithStepCount(fitnessActivity.getStartTimeMilliSeconds(), endTime);
                                    float activityStepCount = FitnessUtility.getStepCount(allLocationsForActivity);
                                    long activityTimeInMinutes = FitnessUtility.getActivityTimeInMinutes(fitnessActivity.getStartTimeMilliSeconds(), fitnessActivity.getEndTimeMilliSeconds());
                                    double activityCaloriesBurnt = FitnessUtility.getCaloriesBurnt(75, fitnessActivity.getActivityType(), activityTimeInMinutes);
                                    SharedPreferences sharedPref = getSharedPreferences(
                                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                    double activityDistanceInMeters = FitnessUtility.getDistanceInMeters(allLocationsForActivity , activityStepCount, sharedPref.getFloat(UserProfile.STRIDE_LENGTH, 0.743f));
                                    fitnessActivity.setStepCount(activityStepCount);
                                    fitnessActivity.setTimeInMinutes(activityTimeInMinutes);
                                    fitnessActivity.setCaloriesBurnt(activityCaloriesBurnt);
                                    fitnessActivity.setDistanceInMeters(activityDistanceInMeters);
                                    fitnessDatabase.fitnessActivityDao().update(fitnessActivity);
                                }
                            }
                        }
                    }
                });
            }
        };
        IntentFilter intentFilter = new IntentFilter("team.charlie.yetanotherfitnesstracker.ACTIVITY_TRANSITION");
        registerReceiver(mActivityTransitionReceiver, intentFilter);
    }


}
