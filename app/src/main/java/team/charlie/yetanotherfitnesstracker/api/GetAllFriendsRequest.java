package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;

public class GetAllFriendsRequest extends ApiBase {

    private static final String TAG = "GetAllFriendsRequest";
    private ApiClientActivity activity;
    private String url = "http://" + URL_BASE + "/user/all";

    public GetAllFriendsRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void getAllFriends(){
        Map<String,String> userInfo = new HashMap<>();
        Uri.Builder builder = Uri.parse(url).buildUpon();


        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.GET, builder.build().toString(),
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray jsonArray = jsonResponse.getJSONArray("users");
                            for(int index=0;index < jsonArray.length();index++){
                                JSONObject jsonObject = jsonArray.getJSONObject(index);
                                userInfo.put(jsonObject.getString("name"),jsonObject.getString("email"));
                            }

                            activity.onGetAllFriendsSuccess(userInfo);
                        } else {
                            activity.onGetAllFriendsFailure();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d(TAG, "onErrorResponse: " + error)
        );

        queue.add(stringRequest);
    }


}
