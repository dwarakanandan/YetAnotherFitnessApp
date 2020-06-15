package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ContestDetailsPageAdapter extends FragmentPagerAdapter {

    private Context context;
    private int totalTabs;
    private ContestItem contestItem;

    ContestDetailsPageAdapter(Context context, FragmentManager fragmentManager, int totalTabs, ContestItem contestItem) {
        super(fragmentManager);
        this.context = context;
        this.totalTabs = totalTabs;
        this.contestItem = contestItem;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ContestDetailsLeaderBoardFragment(contestItem);
        } else {
            return new ContestDetailsMapFragment(contestItem);
        }
    }

    @Override
    public int getCount() {
        return this.totalTabs;
    }
}
