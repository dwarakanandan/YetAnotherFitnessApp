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

public class ConfirmFriendRequest extends ApiBase {
    ApiClientActivity apiClientActivity;
    private static final String TAG = "ConfirmFriendRequest";
    private final String url =  "http://" + URL_BASE + "/friends/confirm";
    Uri.Builder builder = Uri.parse(url).buildUpon();


    public ConfirmFriendRequest(ApiClientActivity apiClientActivity, Context context){
        super(context);
        this.apiClientActivity = apiClientActivity;
    }

    public void confirmingFriendRequest(String emailId){
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST, builder.build().toString(),
                response -> {
            Log.d(TAG, "confirmingFriendRequest: "+response);
            try{
                JSONObject jsonResponse = new JSONObject(response);
                if(jsonResponse.getString("status").equals("success")){
                    apiClientActivity.onConfirmFriendRequestSuccess(jsonResponse.toString());
                }
                else
                    apiClientActivity.onConfirmFriendRequestFailure();

            }
            catch (JSONException e){
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
                params.put("requestToEmailId",emailId);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}

