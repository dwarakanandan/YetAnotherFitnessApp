package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.ui.community.friends.FriendsItems;

public class GetCurrentFriendsRequest extends ApiBase {
    private static final String TAG = "GetAllFriendsRequest";
    private ApiClientActivity activity;
    private String url = "http://" + URL_BASE + "/friends";
    Uri.Builder builder = Uri.parse(url).buildUpon();


    public GetCurrentFriendsRequest(ApiClientActivity activity, Context context){
        super(context);
        this.activity =activity;
    }

    public void getCurrentFriends(){
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.GET, builder.build().toString(),
                response -> {
            Log.d(TAG, "getCurrentFriends: "+response);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if(jsonResponse.getString("status").equals("success")){
                    JSONArray jsonArray = jsonResponse.getJSONArray("friends");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONArray friendList = jsonObject.getJSONArray("friendList");
                    List<FriendsItems> friendsItemsList = new ArrayList<>();
                    for(int i=0;i<friendList.length();i++){
                        JSONObject obj = friendList.getJSONObject(i);
                        JSONObject userID = obj.getJSONObject("friendUserId");
                        FriendsItems items = new FriendsItems(userID.getString("name"),userID.getString("email"),obj.getString("requestStatus"));
                        Log.d(TAG, "getCurrentFriends: CHECK"+items.getmFriendName()+items.getmFriendEmail()+items.getFriendsRequestStatus());
                        friendsItemsList.add(items);
                    }

                    activity.onGetCurrentFriendsSuccess(friendsItemsList);
                }
                else{
                    activity.onGetCurrentFriendsFailure();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        },error -> Log.d(TAG, "getCurrentFriends: "+error)
        ){

            @Override
        public Map<String, String> getHeaders() {
            return getCookies();
        }

        };
        queue.add(stringRequest);
    }
}
