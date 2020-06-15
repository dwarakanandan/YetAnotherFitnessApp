package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivityBackupStatus;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;

public class ActivityUpdateRequest extends ApiBase {

    private ApiClientActivity activity;
    private static final String TAG = "ActivityUpdateRequest";
    private String url = "http://" + URL_BASE + "/activity/update";

    public ActivityUpdateRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void updateActivity(FitnessActivity fitnessActivity, List<LocationWithStepCount> locationWithStepCountList, FitnessActivityBackupStatus fitnessActivityBackupStatus, boolean delete) {
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            activity.onActivityUpdateSuccess(fitnessActivityBackupStatus.getId(), jsonResponse.getString("activityId"));
                        } else {
                            activity.onActivityUpdateFailure();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d(TAG, "onErrorResponse: " + error)
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject requestBody = new JSONObject();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();

                try {
                    requestBody.put("activityId", fitnessActivityBackupStatus.getRemoteBackupId());

                    if (delete) {
                        requestBody.put("isDeleted", "true");
                    }

                    requestBody.put("name", FitnessUtility.getActivityDisplayName(fitnessActivity.getActivityType()));
                    requestBody.put("activityType", FitnessUtility.getActivityDisplayName(fitnessActivity.getActivityType()));

                    calendar.setTimeInMillis(fitnessActivity.getStartTimeMilliSeconds());
                    requestBody.put("startDateTime", sdf.format(calendar.getTime()));

                    calendar.setTimeInMillis(fitnessActivity.getEndTimeMilliSeconds());
                    requestBody.put("endDateTime", sdf.format(calendar.getTime()));

                    requestBody.put("stepCount", String.valueOf(fitnessActivity.getStepCount()));
                    requestBody.put("duration", String.valueOf(fitnessActivity.getTimeInMinutes()));
                    requestBody.put("caloriesBurned", String.valueOf(fitnessActivity.getCaloriesBurnt()));
                    requestBody.put("distance", String.valueOf(fitnessActivity.getDistanceInMeters()));

                    JSONArray coordinates = new JSONArray();
                    for (LocationWithStepCount locationWithStepCount : locationWithStepCountList) {
                        JSONArray longLat = new JSONArray();
                        longLat.put(locationWithStepCount.getLongitude());
                        longLat.put(locationWithStepCount.getLatitude());
                        coordinates.put(longLat);
                    }
                    requestBody.put("locations", coordinates);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return requestBody.toString().getBytes();
            }
        };

        queue.add(stringRequest);
    }
}
