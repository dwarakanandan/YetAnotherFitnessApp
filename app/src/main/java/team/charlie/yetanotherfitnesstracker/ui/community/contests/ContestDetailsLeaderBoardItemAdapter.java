package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import team.charlie.yetanotherfitnesstracker.R;

public class ContestDetailsLeaderBoardItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ContestItemParticipant> participants;

    ContestDetailsLeaderBoardItemAdapter(List<ContestItemParticipant> participants) {
        this.participants = participants;
    }

    private static class ContestDetailsLeaderBoardItemViewHolder extends RecyclerView.ViewHolder {

        TextView textViewEmail;
        TextView textViewValue;

        ContestDetailsLeaderBoardItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewEmail = itemView.findViewById(R.id.fragment_contest_details_leaderboard_list_item_email);
            this.textViewValue = itemView.findViewById(R.id.fragment_contest_details_leaderboard_list_item_value);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contest_details_leaderboard_list_item, parent, false);
        return new ContestDetailsLeaderBoardItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContestItemParticipant participant = participants.get(position);
        ((ContestDetailsLeaderBoardItemViewHolder) holder).textViewEmail.setText(participant.getEmail());
        ((ContestDetailsLeaderBoardItemViewHolder) holder).textViewValue.setText(String.valueOf(participant.getValue()));
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }
}
