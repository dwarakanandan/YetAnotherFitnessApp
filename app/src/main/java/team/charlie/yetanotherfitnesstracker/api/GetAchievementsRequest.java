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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.ui.AchievementUnlocked;
import team.charlie.yetanotherfitnesstracker.ui.community.PageAdapter;

public class GetAchievementsRequest extends ApiBase {
    ApiClientActivity apiClientActivity;
    private static final String TAG = "GetAchievementsRequest";
    private String url = "http://" + URL_BASE + "/achievements/list";
    Uri.Builder builder = Uri.parse(url).buildUpon();

    public GetAchievementsRequest(ApiClientActivity activity , Context context) {
        super(context);
        this.apiClientActivity = activity;

    }

    public void gettingAchievementsRequest(){
        StringRequest request = new CustomStringRequest(this, Request.Method.GET,builder.build().toString(),
                response -> {
                    Log.d(TAG, "gettingAchievementsRequest: "+response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("status").equals("success")){
                            //Log.d(TAG, "ResponseAchievement "+jsonObject.toString());
                            JSONObject result = jsonObject.getJSONObject("result");
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
                            Log.d(TAG, "ResponseAchievement"+achievementUnlockeds.get(4).toString());
                            apiClientActivity.onGetAchievementsSuccess(achievementUnlockeds);
                        }
                        else{
                            apiClientActivity.onGetAchievementsFailure();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },error -> Log.d(TAG, "gettingAchievementsRequest: "+error)){
            @Override
            public Map<String, String> getHeaders() {
                return getCookies();
            }

        };
        queue.add(request);
    }

}
