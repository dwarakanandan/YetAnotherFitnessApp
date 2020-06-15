package team.charlie.yetanotherfitnesstracker.ui.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import team.charlie.yetanotherfitnesstracker.R;

public class HistoryActivity extends AppCompatActivity {

    private String fitnessParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent startingIntent = this.getIntent();
        fitnessParameter = startingIntent.getStringExtra("FITNESS_PARAMETER");

        setupTabLayout();
    }

    private void setupTabLayout() {
        TabLayout tabLayout = findViewById(R.id.activity_history_tab_layout);
        ViewPager viewPager = findViewById(R.id.activity_history_view_pager);

        tabLayout.addTab(tabLayout.newTab().setText("Weekly"));
        tabLayout.addTab(tabLayout.newTab().setText("Monthly"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        HistoryPageAdapter historyPageAdapter = new HistoryPageAdapter(getApplicationContext(), getSupportFragmentManager(), tabLayout.getTabCount(), fitnessParameter);
        viewPager.setAdapter(historyPageAdapter);

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
