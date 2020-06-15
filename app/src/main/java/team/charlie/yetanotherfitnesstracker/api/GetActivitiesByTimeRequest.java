package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;

public class GetActivitiesByTimeRequest extends ApiBase {

    private ApiClientActivity activity;
    private static final String TAG = "GetActivitiesByTimeRequ";
    private String url = "http://" + URL_BASE + "/activities/get_by_time";

    public GetActivitiesByTimeRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void getActivities(long startDate, long endDate) {
        Uri.Builder builder = Uri.parse(url).buildUpon();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(startDate);
        builder.appendQueryParameter("start", sdf.format(calendar.getTime()));

        calendar.setTimeInMillis(endDate);
        builder.appendQueryParameter("end", sdf.format(calendar.getTime()));

        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.GET, builder.build().toString(),
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            ArrayList<FitnessActivity> fitnessActivities = new ArrayList<>();
                            ArrayList<String> remoteIds = new ArrayList<>();
                            ArrayList<LocationWithStepCount> locationWithStepCounts = new ArrayList<>();
                            JSONArray jsonArray = jsonResponse.getJSONArray("activities");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                FitnessActivity tempActivity = new FitnessActivity(FitnessUtility.parseActivityDisplayName(jsonObject.getString("activityType")),
                                        sdf.parse(jsonObject.getString("startDateTime")).getTime(),
                                        sdf.parse(jsonObject.getString("endDateTime")).getTime(),
                                        Float.parseFloat(jsonObject.getString("stepCount")),
                                        jsonObject.getLong("duration"),
                                        jsonObject.getDouble("caloriesBurned"),
                                        jsonObject.getDouble("distance"));

                                fitnessActivities.add(tempActivity);
                                remoteIds.add(jsonObject.getString("_id"));

                                JSONArray locations = jsonObject.getJSONObject("locations").getJSONArray("coordinates");
                                for (int j = 0; j < locations.length(); j++) {
                                    JSONArray coordinates = locations.getJSONArray(j);
                                    LocationWithStepCount locationWithStepCount = new LocationWithStepCount(tempActivity.getStartTimeMilliSeconds(),coordinates.getDouble(1),coordinates.getDouble(0),15.0f,0);
                                    locationWithStepCounts.add(locationWithStepCount);
                                }
                            }
                            activity.onGetActivitiesByTimeSuccess(fitnessActivities, remoteIds, locationWithStepCounts);
                        } else {
                            activity.onGetActivitiesByTimeFailure();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d(TAG, "onErrorResponse: " + error)
        );

        queue.add(stringRequest);
    }

}
