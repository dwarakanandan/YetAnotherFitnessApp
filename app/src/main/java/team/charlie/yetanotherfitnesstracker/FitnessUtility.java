package team.charlie.yetanotherfitnesstracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;
import team.charlie.yetanotherfitnesstracker.ui.community.contests.ContestItem;

public class FitnessUtility {

    public static final String STILL = "STILL";
    public static final String WALKING = "WALKING";
    public static final String RUNNING = "RUNNING";
    public static final String BICYCLE = "BICYCLE";
    public static final String VEHICLE = "VEHICLE";
    public static final String SLEEP = "SLEEP";
    public static final String UNKNOWN = "UNKNOWN";
    public static final int SLEEP_CODE = -1;

    public static final String FITNESS_PARAMETER_STEPS = "STEPS";
    public static final String FITNESS_PARAMETER_DISTANCE = "DISTANCE";
    public static final String FITNESS_PARAMETER_CALORIES = "CALORIES";
    public static final String FITNESS_PARAMETER_ACTIVE_MINUTES = "ACTIVE TIME";
    public static final String FITNESS_PARAMETER_SLEEP = "SLEEP";


    public static final double CALORIE_SLEEP = 0.8;
    public static final double CALORIE_WALKING = 3.5;
    public static final double CALORIE_CYCLING = 6.5;
    public static final double CALORIE_RUNNING = 10.0;


    public static String getActivityDisplayName(int activityId) {
        switch (activityId) {
            case DetectedActivity.STILL:
                return STILL;
            case DetectedActivity.WALKING:
                return WALKING;
            case DetectedActivity.RUNNING:
                return RUNNING;
            case DetectedActivity.ON_BICYCLE:
                return BICYCLE;
            case DetectedActivity.IN_VEHICLE:
                return VEHICLE;
            case FitnessUtility.SLEEP_CODE:
                return SLEEP;
            default:
                return UNKNOWN;
        }
    }

    public static int parseActivityDisplayName(String displayName) {
        switch (displayName) {
            case STILL:
                return DetectedActivity.STILL;
            case WALKING:
                return DetectedActivity.WALKING;
            case RUNNING:
                return DetectedActivity.RUNNING;
            case BICYCLE:
                return DetectedActivity.ON_BICYCLE;
            case VEHICLE:
                return DetectedActivity.IN_VEHICLE;
            case SLEEP:
                return FitnessUtility.SLEEP_CODE;
            default:
                return DetectedActivity.UNKNOWN;
        }
    }

    public static double getDistanceInMeters(List<LocationWithStepCount> locationWithStepCounts, Float activityStepCount, Float strideLength) {
        List<LatLng> path = new ArrayList<>();
        for (LocationWithStepCount i : locationWithStepCounts) {
            if (i.getAccuracy() < 100) {
                path.add(new LatLng(i.getLatitude(), i.getLongitude()));
            }
        }

        if (path.size() <= (locationWithStepCounts.size() / 10)) {
            return strideLength * activityStepCount;
        }

        return SphericalUtil.computeLength(path);
    }

    public static double getCaloriesBurntPerMinute(int weight, int activityType) {
        double factor = 1.0;
        switch (activityType) {
            case DetectedActivity.WALKING:
                factor = CALORIE_WALKING;
                break;
            case DetectedActivity.RUNNING:
                factor = CALORIE_RUNNING;
                break;
            case DetectedActivity.ON_BICYCLE:
                factor = CALORIE_CYCLING;
                break;
            case FitnessUtility.SLEEP_CODE:
                factor = CALORIE_SLEEP;
                break;
        }
        return 0.0175 * factor * weight;
    }

    public static double getCaloriesBurnt(int weight, int activityType, long activityLengthInMins) {
        return getCaloriesBurntPerMinute(weight, activityType) * activityLengthInMins;
    }

    public static float getStepCount(List<LocationWithStepCount> locationWithStepCounts) {
        if (locationWithStepCounts.size() == 0) {
            return 0;
        }
        if (locationWithStepCounts.get(locationWithStepCounts.size() - 1).getStepCount() >= locationWithStepCounts.get(0).getStepCount()) {
            return locationWithStepCounts.get(locationWithStepCounts.size() - 1).getStepCount() - locationWithStepCounts.get(0).getStepCount();
        }
        int currentIndex = 0;
        for (int i = 0; i < locationWithStepCounts.size() - 1; i++) {
            float iStepCount = locationWithStepCounts.get(i).getStepCount();
            float iPlusOneStepCount = locationWithStepCounts.get(i + 1).getStepCount();
            if (iStepCount > iPlusOneStepCount) {
                break;
            }
            currentIndex++;
        }
        float stepsBeforeReboot = locationWithStepCounts.get(currentIndex).getStepCount() - locationWithStepCounts.get(0).getStepCount();
        float stepsAfterReboot = locationWithStepCounts.get(locationWithStepCounts.size() - 1).getStepCount() - locationWithStepCounts.get(currentIndex + 1).getStepCount();
        return stepsBeforeReboot + stepsAfterReboot;
    }

    public static long getActivityTimeInMinutes(long startTime, long endTime) {
        long differenceInMilliSeconds = endTime - startTime;
        long timeInMins = TimeUnit.MILLISECONDS.toMinutes(differenceInMilliSeconds);
        if (timeInMins == 0) {
            timeInMins = 1;
        }
        return timeInMins;
    }

    public static long getNMinusTodayStartInMilliseconds(int n) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 0 - n);
        Date todayStart = cal.getTime();
        return todayStart.getTime();
    }

    public static long getNMinusTodayEndInMilliseconds(int n) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        cal.add(Calendar.DATE, 0 - n);
        Date todayStart = cal.getTime();
        return todayStart.getTime();
    }

    public static int getImageResourceForActivityType(int activityId) {
        switch (activityId) {
            case DetectedActivity.WALKING:
                return R.drawable.ic_walk;
            case DetectedActivity.RUNNING:
                return R.drawable.ic_run;
            case DetectedActivity.IN_VEHICLE:
                return R.drawable.ic_car;
            case DetectedActivity.ON_BICYCLE:
                return R.drawable.ic_cycling;
            case FitnessUtility.SLEEP_CODE:
                return R.drawable.ic_sleep;
            default:
                return R.drawable.ic_activity_24px;
        }
    }

    public static int getImageResourceForContestType(String contestType) {
        if (contestType.equalsIgnoreCase(ContestItem.TYPE_PUBLIC)) {
            return R.drawable.ic_contest_public;
        } else {
            return R.drawable.ic_contest_private;
        }
    }

    public static int getImageResourceForContestGoalType(String contestGoalType) {
        if (contestGoalType.equalsIgnoreCase(ContestItem.GOAL_TYPE_DISTANCE)) {
            return R.drawable.ic_distance;
        } else {
            return R.drawable.ic_steps;
        }
    }

    public static float aggregateFitnessActivitiesOnParameter(List<FitnessActivity> currentFitnessActivitiesList, String fitnessParameter) {
        float aggregateValue = 0;
        for (FitnessActivity currentFitnessActivity : currentFitnessActivitiesList) {
            if ((currentFitnessActivity.getActivityType() == DetectedActivity.STILL) ||
                    currentFitnessActivity.getActivityType() == DetectedActivity.IN_VEHICLE) {
                continue;
            }
            switch (fitnessParameter) {
                case FITNESS_PARAMETER_STEPS:
                    if (currentFitnessActivity.getActivityType() == DetectedActivity.ON_BICYCLE) {
                        continue;
                    }
                    aggregateValue += Math.round(currentFitnessActivity.getStepCount());
                    break;
                case FITNESS_PARAMETER_DISTANCE:
                    aggregateValue += Math.round(currentFitnessActivity.getDistanceInMeters());
                    break;
                case FITNESS_PARAMETER_ACTIVE_MINUTES:
                    if (currentFitnessActivity.getActivityType() == FitnessUtility.SLEEP_CODE) {
                        continue;
                    }
                    aggregateValue += Math.round(currentFitnessActivity.getTimeInMinutes());
                    break;
                case FITNESS_PARAMETER_CALORIES:
                    aggregateValue += Math.round(currentFitnessActivity.getCaloriesBurnt());
                    break;
                case FITNESS_PARAMETER_SLEEP:
                    if (currentFitnessActivity.getActivityType() == FitnessUtility.SLEEP_CODE) {
                        aggregateValue += (currentFitnessActivity.getTimeInMinutes() / 60.0);
                    }
                    break;
            }
        }
        return aggregateValue;
    }

    public static float getStrideLengthInMeters(float heightInCentimeters) {
        double heightInInches = heightInCentimeters / 2.54;
        double strideLengthInInches = heightInInches * 0.413;
        return (float) (strideLengthInInches / 39.37);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
