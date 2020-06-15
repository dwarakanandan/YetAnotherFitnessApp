package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.api.JoinContestRequest;

public class ContestDetailsActivity extends AppCompatActivity implements ApiClientActivity {

    private static final String TAG = "ContestDetailsActivity";
    TabLayout tabLayout;
    ViewPager viewPager;
    ContestDetailsPageAdapter contestDetailsPageAdapter;
    ContestItem contestItem;
    ImageView imageViewJoined;
    Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_details);
        contestItem = (ContestItem) getIntent().getSerializableExtra("CONTEST");
        setupTabLayout();
        setupContestDetails();
    }

    private void setupContestDetails() {
        ImageView imageViewContestDetails = findViewById(R.id.activity_contest_details_image);
        ImageView imageViewGoalType = findViewById(R.id.activity_contest_details_image_goal_type);
        TextView textViewName = findViewById(R.id.activity_contest_details_name);
        TextView textViewDescription = findViewById(R.id.activity_contest_details_description);
        TextView textViewCreator = findViewById(R.id.activity_contest_details_creator);
        TextView textViewContestType = findViewById(R.id.activity_contest_details_type);
        TextView textViewGoalType = findViewById(R.id.activity_contest_details_goal_type);
        TextView textViewGoalValue = findViewById(R.id.activity_contest_details_goal_value);
        TextView textViewGoalMeters = findViewById(R.id.activity_contest_details_goal_meters);

        imageViewContestDetails.setImageResource(this.contestItem.getTypeImage());
        imageViewGoalType.setImageResource(this.contestItem.getGoalTypeImage());
        textViewName.setText(this.contestItem.getName());
        textViewDescription.setText(this.contestItem.getDescription());
        textViewCreator.setText("Creator:  " + this.contestItem.getCreator().getEmail());
        textViewContestType.setText(this.contestItem.getType());
        textViewGoalType.setText(this.contestItem.getGoalType());
        textViewGoalValue.setText("Goal:  " + this.contestItem.getGoalValue());

        if (this.contestItem.getGoalType().equalsIgnoreCase(ContestItem.GOAL_TYPE_STEPS)) {
            textViewGoalMeters.setVisibility(View.GONE);
        }

        java.text.DateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy   hh : mm  aa", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        TextView textViewStartDay = findViewById(R.id.activity_contest_details_start_day);
        TextView textViewEndDay = findViewById(R.id.activity_contest_details_end_day);

        calendar.setTimeInMillis(this.contestItem.getStartDate());
        textViewStartDay.setText(dateFormat.format(calendar.getTime()));

        calendar.setTimeInMillis(this.contestItem.getEndDate());
        textViewEndDay.setText(dateFormat.format(calendar.getTime()));

        imageViewJoined = findViewById(R.id.activity_contest_details_join_image);
        joinButton = findViewById(R.id.activity_contest_details_join_button);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContestDetailsActivity.this.contestItem.isEnded()) {
                    Toast.makeText(ContestDetailsActivity.this,"Contest has already ended!!", Toast.LENGTH_LONG).show();
                } else {
                    JoinContestRequest joinContestRequest = new JoinContestRequest(ContestDetailsActivity.this, ContestDetailsActivity.this.getApplicationContext());
                    joinContestRequest.joinContest(contestItem);
                }
            }
        });

        if (this.contestItem.isJoined()) {
            joinButton.setVisibility(View.GONE);
        } else {
            imageViewJoined.setVisibility(View.GONE);
        }
    }

    @Override
    public void onContestJoinSuccess() {
        this.contestItem.setJoined(true);
        joinButton.setVisibility(View.GONE);
        imageViewJoined.setVisibility(View.VISIBLE);
    }

    private void setupTabLayout() {
        tabLayout = findViewById(R.id.activity_contest_details_tab_layout);
        viewPager = findViewById(R.id.activity_contest_details_view_pager);

        tabLayout.addTab(tabLayout.newTab().setText("Leaderboard"));
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        contestDetailsPageAdapter = new ContestDetailsPageAdapter(getApplicationContext(), getSupportFragmentManager(), tabLayout.getTabCount(), contestItem);
        viewPager.setAdapter(contestDetailsPageAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
