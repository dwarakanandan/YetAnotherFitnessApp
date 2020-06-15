package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;

public class RejectFriendRequest extends ApiBase {
    ApiClientActivity apiClientActivity;
    private static final String TAG = "RejectFriendRequest";
    private String url = "http://" + URL_BASE + "/friends/reject";
    Uri.Builder builder = Uri.parse(url).buildUpon();

    public RejectFriendRequest(ApiClientActivity activity , Context context) {
        super(context);
        this.apiClientActivity = activity;

    }

    public void rejectingFriendRequest(String emailId){
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST,builder.build().toString(),
                response -> {
                    Log.d(TAG, "rejectingFriendRequest: "+response);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("status").equals("success"))
                        {
                            apiClientActivity.onRejectFriendSuccess(jsonObject.toString());
                        }
                        else{
                            apiClientActivity.onRejectFriendFailure();
                        }
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                },error -> Log.d(TAG, "rejectingFriendRequest: "+error)){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
            @Override
            public Map<String, String> getHeaders() {
                return getCookies();
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("requestToEmailId",emailId);
                return params;
            }
        };
        queue.add(stringRequest);
        }
    }

