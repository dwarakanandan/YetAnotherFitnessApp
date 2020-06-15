package team.charlie.yetanotherfitnesstracker.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;

public class RegisterRequest extends ApiBase {
    private ApiClientActivity activity;
    private static final String TAG = "RegisterRequest";
    private String url = "http://" + URL_BASE + "/user/register";
    private String name;
    private String email;
    private String password;

    public RegisterRequest(ApiClientActivity activity, Context context, String name, String email, String password) {
        super(context);
        this.activity = activity;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void register() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (! jsonResponse.has("status")) {
                            activity.onRegisterSuccess();
                        } else {
                            activity.onRegisterFailure(jsonResponse.getString("message"));
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
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };

        queue.add(stringRequest);
    }

}
