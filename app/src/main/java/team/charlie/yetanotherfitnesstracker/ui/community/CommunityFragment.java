package team.charlie.yetanotherfitnesstracker.ui.community;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import team.charlie.yetanotherfitnesstracker.R;

public class CommunityFragment extends Fragment {

    CommunityViewModel communityViewModel;

    View root;
    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        communityViewModel = ViewModelProviders.of(this).get(CommunityViewModel.class);
        root = inflater.inflate(R.layout.fragment_community, container, false);
        Log.d("Kinshuk", "onCreateView: Inflate done");
        tabLayout = root.findViewById(R.id.communityTabLayout);
        viewPager = root.findViewById(R.id.communityViewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Contests"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

         adapter = new PageAdapter(this.getContext(),getChildFragmentManager(),tabLayout.getTabCount(),root);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("Kinshuk:","OnTabSelected");
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //viewPager.refreshDrawableState();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    return root;
    }

}