package team.charlie.yetanotherfitnesstracker.ui.community.friends;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.api.GetFriendsAchievementRequest;
import team.charlie.yetanotherfitnesstracker.ui.AchievementUnlocked;
import team.charlie.yetanotherfitnesstracker.ui.AchievementsAdapter;

public class FriendsAchievementActivity extends Activity implements ApiClientActivity {
    String userName="";
    String userEmailId = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_achievements);
        userName = getIntent().getStringExtra("name");
        userEmailId = getIntent().getStringExtra("email");

    }
    public void setNameEmail(){
       TextView name =  findViewById(R.id.friendName);
       name.setText(this.userName);
       TextView email = findViewById(R.id.friendEmailID);
       email.setText(this.userEmailId);
    }
    @Override
    public void onGetFriendsAchievementSuccess(List<AchievementUnlocked> response) {

        RecyclerView recyclerView = findViewById(R.id.friendsAchievementRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        FriendsAchievementAdapter adapter = new FriendsAchievementAdapter(response);
        recyclerView.setAdapter(adapter);
        setNameEmail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetFriendsAchievementRequest getFriendsAchievementRequest = new GetFriendsAchievementRequest(this,FriendsAchievementActivity.this.getApplicationContext());
        getFriendsAchievementRequest.getFriendsAchievementRequest(userEmailId);
    }
}
