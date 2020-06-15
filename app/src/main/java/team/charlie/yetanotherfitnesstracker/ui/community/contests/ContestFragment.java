package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.api.GetContestsRequest;

public class ContestFragment extends Fragment implements ContestItemClickListener, ApiClientActivity {

    private static final String TAG = "ContestFragment";

    View root;
    ArrayList<ContestItem> contestItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_contest, container, false);

        FloatingActionButton floatingActionButton = root.findViewById(R.id.fragment_contest_floating_action_button);
        floatingActionButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(ContestFragment.this.getActivity(), AddContestActivity.class);
            ContestFragment.this.startActivity(myIntent);
        });

        return root;
    }

    @Override
    public void onResume() {
        GetContestsRequest getContestsRequest = new GetContestsRequest(this, getContext());
        getContestsRequest.getContests();
        super.onResume();
    }

    @Override
    public void onClick(int position) {
        Intent myIntent = new Intent(ContestFragment.this.getActivity(), ContestDetailsActivity.class);
        myIntent.putExtra("CONTEST", this.contestItems.get(position));
        ContestFragment.this.startActivity(myIntent);
    }

    @Override
    public void onGetContestsSuccess(ArrayList<ContestItem> contestItems) {
        this.contestItems = contestItems;

        RecyclerView recyclerView = root.findViewById(R.id.fragment_contest_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        RecyclerView.Adapter adapter = new ContestItemAdapter(this, this.contestItems);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter count: " + adapter.getItemCount());
    }
}
