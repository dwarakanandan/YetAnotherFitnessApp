package team.charlie.yetanotherfitnesstracker.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;


public class LogoutRequest extends ApiBase {

    private ApiClientActivity activity;
    private static final String TAG = "LogoutRequest";
    private String url = "http://" + URL_BASE + "/logout";

    public LogoutRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void logout() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    removeCookies();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            activity.onLogoutSuccess();
                        } else {
                            activity.onLogoutFailure();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d(TAG, "onErrorResponse: " + error)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return getCookies();
            }
        };

        queue.add(stringRequest);
    }
}
