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

public class UpdateUserLocationRequest extends ApiBase {

    private ApiClientActivity activity;
    private static final String TAG = "UpdateUserLocationReque";
    private String url = "http://" + URL_BASE + "/user/location";

    public UpdateUserLocationRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void updateLocation(String latitude, String longitude) {
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            activity.onUpdateUserLocationSuccess();
                        } else {
                            activity.onUpdateUserLocationFailure();
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
                params.put("lat", latitude);
                params.put("long", longitude);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
