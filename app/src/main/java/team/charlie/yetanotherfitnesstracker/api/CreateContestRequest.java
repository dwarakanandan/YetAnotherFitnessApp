package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.ui.community.contests.ContestItem;

public class CreateContestRequest extends ApiBase {

    private ApiClientActivity activity;
    private static final String TAG = "CreateContestRequest";
    private String url = "http://" + URL_BASE + "/contest";

    public CreateContestRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void createContest(ContestItem contestItem) {
        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            activity.onCreateContestSuccess();
                        } else {
                            activity.onCreateContestFailure();
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();

                params.put("name", contestItem.getName());
                params.put("description", contestItem.getDescription());

                calendar.setTimeInMillis(contestItem.getStartDate());
                params.put("startDateTime", sdf.format(calendar.getTime()));

                calendar.setTimeInMillis(contestItem.getEndDate());
                params.put("endDateTime", sdf.format(calendar.getTime()));

                params.put("contestType", contestItem.getType());
                params.put("contestGoalType", contestItem.getGoalType());
                params.put("contestGoalValue", String.valueOf(contestItem.getGoalValue()));

                return params;
            }
        };

        queue.add(stringRequest);
    }
}
