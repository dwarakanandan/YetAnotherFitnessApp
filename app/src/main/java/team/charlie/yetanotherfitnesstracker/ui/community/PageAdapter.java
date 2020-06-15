package team.charlie.yetanotherfitnesstracker.ui.community;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import team.charlie.yetanotherfitnesstracker.ui.community.contests.ContestFragment;
import team.charlie.yetanotherfitnesstracker.ui.community.friends.FriendsFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;
    View parentView;

    public PageAdapter(Context context, FragmentManager fragmentManager, int totalTabs, View root){
        super(fragmentManager);
        this.context = context;
        this.totalTabs = totalTabs;
        this.parentView = root;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("Kinshuk:PageAdapter","In page Adapter getItem");
        switch (position){
            case 0:
                FriendsFragment friendFragment = new FriendsFragment(parentView);
                return friendFragment;
            case 1:
                ContestFragment contestFragment = new ContestFragment();
                return contestFragment;
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return this.totalTabs;
    }
}
