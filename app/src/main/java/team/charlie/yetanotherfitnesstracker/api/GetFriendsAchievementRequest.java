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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.ui.AchievementUnlocked;

public class GetFriendsAchievementRequest extends ApiBase {

    ApiClientActivity apiClientActivity;
    private static final String TAG = "GetFriendsAchievementsRequest";
    private String url = "http://" + URL_BASE + "/friends/achievements";
    Uri.Builder builder = Uri.parse(url).buildUpon();

    public GetFriendsAchievementRequest(ApiClientActivity activity , Context context) {
        super(context);
        this.apiClientActivity = activity;

    }

    public void getFriendsAchievementRequest(String email){
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST, builder.build().toString(),
                response -> {
                    Log.d(TAG, "getFriendsAchievementRequest: "+response);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("status").equals("success"))
                        {
                            JSONObject userInfo = jsonObject.getJSONObject("userInfo");
                            JSONObject result =  userInfo.getJSONObject("achievements");
                            Iterator<String> keys =result.keys();
                            List<AchievementUnlocked> achievementUnlockeds = new ArrayList<>();
                            while(keys.hasNext()){
                                String key = keys.next();
                                if(result.get(key) instanceof JSONObject) {
                                    Log.d(TAG, "gettingAchievementsRequest: "+key);
                                    AchievementUnlocked unlocked =new AchievementUnlocked();
                                    List<Double> level = new ArrayList<>();
                                    unlocked.setName( ((JSONObject) result.get(key)).getString("name"));
                                    unlocked.setType(key);
                                    unlocked.setTemplate_description(((JSONObject) result.get(key)).getString("template_description"));
                                    if(((JSONObject) result.get(key)).has("value"))
                                        unlocked.setValue(((JSONObject) result.get(key)).getInt("value"));
                                    JSONArray jsonArray = ((JSONObject) result.get(key)).getJSONArray("levels");

                                    for(int i=0;i<jsonArray.length();i++){
                                        level.add(jsonArray.getDouble(i));
                                        Log.d(TAG, "Adding levels: "+jsonArray.getDouble(i));
                                    }
                                    unlocked.setLevels(level);
                                    achievementUnlockeds.add(unlocked);
                                }
                            }

                            apiClientActivity.onGetFriendsAchievementSuccess(achievementUnlockeds);
                        }
                        else{
                            apiClientActivity.onGetFriendsAchievementsFailure();
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
                params.put("email",email);
                return params;
            }
        };
        queue.add(stringRequest);

    }
}
