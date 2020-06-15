package team.charlie.yetanotherfitnesstracker.ui.history;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HistoryPageAdapter extends FragmentPagerAdapter {

    private Context context;
    private int totalTabs;
    private String fitnessParameter;

    HistoryPageAdapter(Context context, FragmentManager fragmentManager, int totalTabs, String fitnessParameter) {
        super(fragmentManager);
        this.context = context;
        this.totalTabs = totalTabs;
        this.fitnessParameter = fitnessParameter;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new HistoryWeekFragment(fitnessParameter);
        } else {
            return new HistoryMonthFragment(fitnessParameter);
        }
    }

    @Override
    public int getCount() {
        return this.totalTabs;
    }
}
