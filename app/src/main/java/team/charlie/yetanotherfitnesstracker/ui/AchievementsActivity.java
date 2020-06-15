package team.charlie.yetanotherfitnesstracker.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.api.GetAchievementsRequest;
import team.charlie.yetanotherfitnesstracker.ui.community.friends.SearchAllFriendsFragment;

public class AchievementsActivity extends AppCompatActivity implements ApiClientActivity {

    private List<AchievementUnlocked> unlockedList;
    private static final String TAG = "AchievementsActivity";

    public AchievementsActivity() {
        this.unlockedList = new ArrayList<>();
        this.unlockedList.add(new AchievementUnlocked("Hello"));
        this.unlockedList.add(new AchievementUnlocked("Hello"));
        this.unlockedList.add(new AchievementUnlocked("Hello"));
        this.unlockedList.add(new AchievementUnlocked("Hello"));
        this.unlockedList.add(new AchievementUnlocked("Hello"));
        this.unlockedList.add(new AchievementUnlocked("Hello"));
        this.unlockedList.add(new AchievementUnlocked("Hello"));
        this.unlockedList.add(new AchievementUnlocked("Hello"));
        this.unlockedList.add(new AchievementUnlocked("Hello"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

    }

    @Override
    public void onResume() {
        super.onResume();
        new GetAchievementsAsyncTask(this,this).execute();
    }

    @Override
    public void onGetAchievementsSuccess(List<AchievementUnlocked> response) {
        Log.d(TAG, "onGetAchievementsSuccess: "+response);
        RecyclerView recyclerView = findViewById(R.id.achievementRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        AchievementsAdapter adapter = new AchievementsAdapter(response);
        recyclerView.setAdapter(adapter);
    }

    public static class GetAchievementsAsyncTask extends AsyncTask<Integer,Void,Void>{
        Context context;
        AchievementsActivity achievementsActivity;

        GetAchievementsAsyncTask(Context context, AchievementsActivity achievementsActivity){
            this.context = context;
            this.achievementsActivity = achievementsActivity;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            GetAchievementsRequest getAchievementsRequest = new GetAchievementsRequest(achievementsActivity,context);
            getAchievementsRequest.gettingAchievementsRequest();
            return null;
        }
    }
}
