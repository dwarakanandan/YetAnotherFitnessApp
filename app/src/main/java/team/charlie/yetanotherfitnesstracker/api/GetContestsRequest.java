package team.charlie.yetanotherfitnesstracker.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.UserProfile;
import team.charlie.yetanotherfitnesstracker.ui.community.contests.ContestItem;
import team.charlie.yetanotherfitnesstracker.ui.community.contests.ContestItemParticipant;
import team.charlie.yetanotherfitnesstracker.ui.community.contests.ParticipantComparator;

public class GetContestsRequest extends ApiBase {

    private ApiClientActivity activity;
    private static final String TAG = "GetContestsRequest";
    private String url = "http://" + URL_BASE + "/contest";

    public GetContestsRequest(ApiClientActivity activity, Context context) {
        super(context);
        this.activity = activity;
    }

    public void getContests() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());

        StringRequest stringRequest = new CustomStringRequest(this, Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray contestsJson = jsonResponse.getJSONArray("contests");
                            ArrayList<ContestItem> contestItems = new ArrayList<>();
                            for (int i = 0; i < contestsJson.length(); i++) {
                                JSONObject contestJson = contestsJson.getJSONObject(i);
                                ContestItem contestItem = new ContestItem();
                                contestItem.setRemoteId(contestJson.getString("_id"));
                                contestItem.setName(contestJson.getString("name"));
                                contestItem.setDescription(contestJson.getString("description"));
                                contestItem.setType(contestJson.getString("contestType"));
                                contestItem.setStartDate(sdf.parse(contestJson.getString("startDateTime")).getTime());
                                contestItem.setEndDate(sdf.parse(contestJson.getString("endDateTime")).getTime());
                                contestItem.setGoalType(contestJson.getString("contestGoalType"));
                                contestItem.setGoalValue(contestJson.getDouble("contestGoalValue"));

                                contestItem.setTypeImage(FitnessUtility.getImageResourceForContestType(contestItem.getType()));
                                contestItem.setGoalTypeImage(FitnessUtility.getImageResourceForContestGoalType(contestItem.getGoalType()));

                                ContestItemParticipant creator = new ContestItemParticipant();
                                creator.setEmail(contestJson.getJSONObject("creatorID").getString("email"));
                                creator.setName(contestJson.getJSONObject("creatorID").getString("name"));
                                creator.setId(contestJson.getJSONObject("creatorID").getString("_id"));
                                contestItem.setCreator(creator);

                                boolean joined = false;
                                ArrayList<ContestItemParticipant> participants = new ArrayList<>();
                                JSONArray participantsJson = contestJson.getJSONArray("participant");
                                for (int j = 0; j < participantsJson.length(); j++) {
                                    ContestItemParticipant participant = new ContestItemParticipant();
                                    participant.setId(participantsJson.getJSONObject(j).getJSONObject("userID").getString("_id"));
                                    participant.setName(participantsJson.getJSONObject(j).getJSONObject("userID").getString("name"));
                                    participant.setEmail(participantsJson.getJSONObject(j).getJSONObject("userID").getString("email"));
                                    if (participantsJson.getJSONObject(j).getJSONObject("userID").has("location")) {
                                        JSONArray coordinates = participantsJson.getJSONObject(j).getJSONObject("userID").getJSONObject("location").getJSONArray("coordinates");
                                        participant.setLatitiude(coordinates.getDouble(1));
                                        participant.setLongitude(coordinates.getDouble(0));
                                    } else {
                                        participant.setLatitiude(49.878708);
                                        participant.setLongitude(8.646927);
                                    }

                                    if (participantsJson.getJSONObject(j).has("value")) {
                                        participant.setValue(participantsJson.getJSONObject(j).getDouble("value"));
                                    } else {
                                        participant.setValue(0.0);
                                    }
                                    if (participant.getEmail().equalsIgnoreCase(this.sharedPref.getString(UserProfile.EMAIL, ""))) {
                                        joined = true;
                                    }
                                    participants.add(participant);
                                }
                                Collections.sort(participants, new ParticipantComparator());
                                contestItem.setContestItemParticipants(participants);
                                contestItem.setJoined(joined);
                                contestItem.setEnded(contestJson.getBoolean("isEnded"));

                                contestItems.add(contestItem);
                            }
                            activity.onGetContestsSuccess(contestItems);
                        } else {
                            activity.onGetContestsFailure();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d(TAG, "onErrorResponse: " + error)
        );

        queue.add(stringRequest);
    }
}
