package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import team.charlie.yetanotherfitnesstracker.R;

public class ContestDetailsLeaderBoardFragment extends Fragment {

    private static final String TAG = "ContestDetailsLeaderBoa";

    private View root;
    private ContestItem contestItem;

    ContestDetailsLeaderBoardFragment(ContestItem contestItem) {
        this.contestItem = contestItem;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_contest_details_leaderboard, container, false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView recyclerView = root.findViewById(R.id.fragment_contest_details_leaderboard_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        RecyclerView.Adapter adapter = new ContestDetailsLeaderBoardItemAdapter(this.contestItem.getContestItemParticipants());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter count: " + adapter.getItemCount());
    }
}
