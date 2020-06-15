package team.charlie.yetanotherfitnesstracker.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;

public class LoginRequest extends ApiBase {
    private ApiClientActivity activity;
    private static final String TAG = "LoginRequest";

    private String email;
    private String password;
    private String url = "http://" + URL_BASE + "/login";

    public LoginRequest(ApiClientActivity activity, Context context, String email, String password) {
        super(context);
        this.activity = activity;
        this.email = email;
        this.password = password;
    }

    public void login() {

        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            activity.onLoginSuccess();
                        } else {
                            activity.onLoginFailure();
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
                params.put("email", email);
                params.put("password", password);
                params.put("remember_me", "true");
                return params;
            }
        };

        queue.add(stringRequest);
    }

}
