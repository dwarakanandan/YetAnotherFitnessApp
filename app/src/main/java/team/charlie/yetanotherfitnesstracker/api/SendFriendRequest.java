package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;

public class SendFriendRequest extends ApiBase {
    ApiClientActivity apiClientActivity;
    private static final String TAG = "SendFriendRequest";
    private String url = "http://" + URL_BASE + "/friends/request";
    Uri.Builder builder = Uri.parse(url).buildUpon();


    public SendFriendRequest(ApiClientActivity activity , Context context) {
        super(context);
        this.apiClientActivity = activity;

    }
    public void sendingFriendRequest(String email){
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST, builder.build().toString(),
                response -> {
            Log.d(TAG, "sendingFriendRequest: onResponse"+response);
            try{
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("status").equals("success"))
                {
                    apiClientActivity.onSendFriendRequestSuccess(jsonObject.toString());
                }
                else{
                    apiClientActivity.onSendFriendRequestFailure();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d(TAG, "onErrorResponse: " + error)
        ){
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
                params.put("requestToEmailId",email);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    }
