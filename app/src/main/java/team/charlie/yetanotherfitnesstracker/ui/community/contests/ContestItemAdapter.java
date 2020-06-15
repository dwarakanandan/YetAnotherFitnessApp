package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.charlie.yetanotherfitnesstracker.R;

public class ContestItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ContestItemAdapter";
    ContestItemClickListener activity;
    ArrayList<ContestItem> contestItems;

    ContestItemAdapter(ContestItemClickListener activity, ArrayList<ContestItem> contestItems) {
        this.activity = activity;
        this.contestItems = contestItems;
    }

    private static class ContestItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ContestItemClickListener activity;
        ImageView imageViewType;
        ImageView imageViewGoalType;
        TextView textViewName;
        TextView textViewGoalType;
        TextView textViewGoalValue;
        TextView textViewType;
        TextView textViewDescription;
        TextView textViewCreator;
        TextView textViewMeters;
        ImageView imageViewJoined;

        ContestItemViewHolder(@NonNull View itemView, ContestItemClickListener activity) {
            super(itemView);
            this.activity = activity;
            imageViewType = itemView.findViewById(R.id.fragment_contest_list_item_image);
            imageViewGoalType = itemView.findViewById(R.id.fragment_contest_list_item_goal_image);
            textViewName = itemView.findViewById(R.id.fragment_contest_list_item_name);
            textViewGoalType = itemView.findViewById(R.id.fragment_contest_list_item_goal_type);
            textViewGoalValue = itemView.findViewById(R.id.fragment_contest_list_item_goal_value);
            textViewType = itemView.findViewById(R.id.fragment_contest_list_item_type);
            textViewDescription = itemView.findViewById(R.id.fragment_contest_list_item_description);
            textViewCreator = itemView.findViewById(R.id.fragment_contest_list_item_creator);
            textViewMeters = itemView.findViewById(R.id.fragment_contest_list_item_goal_value_meters);
            imageViewJoined = itemView.findViewById(R.id.fragment_contest_list_item_joined_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            activity.onClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contest_list_item, parent, false);
        ContestItemViewHolder contestItemViewHolder = new ContestItemViewHolder(v, this.activity);
        return contestItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContestItem contestItem = this.contestItems.get(position);
        ((ContestItemViewHolder) holder).imageViewType.setImageResource(contestItem.getTypeImage());
        ((ContestItemViewHolder) holder).imageViewGoalType.setImageResource(contestItem.getGoalTypeImage());
        ((ContestItemViewHolder) holder).textViewName.setText(contestItem.getName());
        ((ContestItemViewHolder) holder).textViewDescription.setText(contestItem.getDescription());
        ((ContestItemViewHolder) holder).textViewType.setText(contestItem.getType());
        ((ContestItemViewHolder) holder).textViewGoalType.setText(contestItem.getGoalType());

        String goalValue = "Goal:  " + contestItem.getGoalValue();
        ((ContestItemViewHolder) holder).textViewGoalValue.setText(goalValue);

        String creatorString = "Creator:  " + contestItem.getCreator().getEmail();
        ((ContestItemViewHolder) holder).textViewCreator.setText(creatorString);

        if (!contestItem.isJoined()) {
            ((ContestItemViewHolder) holder).imageViewJoined.setVisibility(View.GONE);
        }
        if (contestItem.getGoalType().equalsIgnoreCase(ContestItem.GOAL_TYPE_STEPS)) {
            ((ContestItemViewHolder) holder).textViewMeters.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.contestItems.size();
    }
}
