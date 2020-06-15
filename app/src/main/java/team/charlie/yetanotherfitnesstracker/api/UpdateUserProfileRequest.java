package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.UserProfile;

public class UpdateUserProfileRequest extends ApiBase {

    private ApiClientActivity activity;
    private static final String TAG = "UpdateUserProfileReques";
    private String url = "http://" + URL_BASE + "/user/update";

    public UpdateUserProfileRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void updateProfile(UserProfile userProfile) {
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            activity.onUpdateUserProfileSuccess();
                        } else {
                            activity.onUpdateUserProfileFailure();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d(TAG, "onErrorResponse: " + error)
        ) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", userProfile.getName());
                params.put("age", String.valueOf(userProfile.getAge()));
                params.put("email", userProfile.getEmail());
                params.put("gender", userProfile.getGender().substring(0,1));
                params.put("height", String.valueOf(userProfile.getHeight()));
                params.put("stepGoal", String.valueOf(userProfile.getStepGoal()));
                params.put("distanceGoal", String.valueOf(userProfile.getDistanceGoal()));
                params.put("calorieGoal", String.valueOf(userProfile.getCaloriesGoal()));
                params.put("activityGoal", String.valueOf(userProfile.getActiveTimeGoal()));
                params.put("sleepGoal", String.valueOf(userProfile.getSleepGoal()));
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
