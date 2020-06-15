package team.charlie.yetanotherfitnesstracker.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;

public class UserProfileRequest extends ApiBase {

    private ApiClientActivity activity;
    private static final String TAG = "UserProfileRequest";
    private String url = "http://" + URL_BASE + "/user/profile";

    public UserProfileRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void getUserProfile() {

        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            activity.onUserProfileSuccess(jsonResponse.toString());
                        } else {
                            activity.onUserProfileFailure();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d(TAG, "onErrorResponse: " + error)
        );

        queue.add(stringRequest);
    }
}
