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
import team.charlie.yetanotherfitnesstracker.database.entities.WeightItem;

public class UpdateWeightRequest extends ApiBase {
    private ApiClientActivity activity;
    private static final String TAG = "UpdateWeightRequest";
    private String url = "http://" + URL_BASE + "/user/update_weight";

    public UpdateWeightRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void updateWeightItem(WeightItem weightItem, boolean delete) {
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            activity.onUpdateWeightSuccess(weightItem.getId(), weightItem.getRemoteId());
                        } else {
                            activity.onUpdateWeightFailure(weightItem.getId(), weightItem.getRemoteId());
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
                params.put("weightId", weightItem.getRemoteId());
                params.put("weight", String.valueOf(weightItem.getWeight()));
                params.put("date", String.valueOf(weightItem.getTimeStamp()));
                if (delete) {
                    params.put("delete", "true");
                }
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
