package team.charlie.yetanotherfitnesstracker.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;


public class FitnessActivityItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FitnessActivityItem> fitnessActivityItems;
    private int FITNESS_VIEW_TYPE = 0;
    private int DAY_SEPARATOR_VIEW_TYPE = 1;
    private ActivityItemClickListener activityItemClickListener;

    public static class FitnessActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imageView;
        public TextView titleTextView;
        public TextView timeTextView;
        public TextView distanceTextView;
        public TextView durationTextView;
        public TextView caloriesTextView;
        public TextView stepCountTextView;
        public ImageView distanceImageView;
        public ImageView stepCountImageView;
        private ActivityItemClickListener activityItemClickListener;

        public FitnessActivityViewHolder(@NonNull View itemView, ActivityItemClickListener activityItemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fitness_activity_list_item_image);
            titleTextView = itemView.findViewById(R.id.fitness_activity_list_item_title);
            timeTextView = itemView.findViewById(R.id.fitness_activity_list_item_time);
            distanceTextView = itemView.findViewById(R.id.fitness_activity_list_item_distance);
            durationTextView = itemView.findViewById(R.id.fitness_activity_list_item_duration);
            caloriesTextView = itemView.findViewById(R.id.fitness_activity_list_item_calories);
            stepCountTextView = itemView.findViewById(R.id.fitness_activity_list_item_steps);
            distanceImageView = itemView.findViewById(R.id.fitness_activity_list_item_image_distance);
            stepCountImageView = itemView.findViewById(R.id.fitness_activity_list_item_image_step);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.activityItemClickListener = activityItemClickListener;
        }

        @Override
        public void onClick(View v) {
            activityItemClickListener.onActivityClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            activityItemClickListener.onActivityLongClick(getAdapterPosition());
            return false;
        }
    }

    public static class DaySeparatorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView dayTitleTextView;
        private ActivityItemClickListener activityItemClickListener;

        public DaySeparatorViewHolder(@NonNull View itemView, ActivityItemClickListener activityItemClickListener) {
            super(itemView);
            dayTitleTextView = itemView.findViewById(R.id.day_separator_list_item_title);
            this.activityItemClickListener = activityItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            activityItemClickListener.onDaySeparatorClick(getAdapterPosition());
        }
    }

    public FitnessActivityItemAdapter(ArrayList<FitnessActivityItem> fitnessActivityItems, ActivityItemClickListener activityItemClickListener) {
        this.fitnessActivityItems = fitnessActivityItems;
        this.activityItemClickListener = activityItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FitnessActivityItem currentFitnessActivityItem = fitnessActivityItems.get(position);
        if (getItemViewType(position) == FITNESS_VIEW_TYPE) {
            ((FitnessActivityViewHolder)holder).imageView.setImageResource(currentFitnessActivityItem.getmImageResource());
            ((FitnessActivityViewHolder)holder).titleTextView.setText(currentFitnessActivityItem.getmTitleText());
            ((FitnessActivityViewHolder)holder).timeTextView.setText(currentFitnessActivityItem.getmTimeText());
            ((FitnessActivityViewHolder)holder).distanceTextView.setText(currentFitnessActivityItem.getmDistanceText());
            ((FitnessActivityViewHolder)holder).durationTextView.setText(currentFitnessActivityItem.getmDurationText());
            ((FitnessActivityViewHolder)holder).caloriesTextView.setText(currentFitnessActivityItem.getmCaloriesText());
            ((FitnessActivityViewHolder)holder).stepCountTextView.setText(currentFitnessActivityItem.getmStepCountText());
            if (hideDistance(currentFitnessActivityItem.getmTitleText())) {
                ((FitnessActivityViewHolder)holder).distanceTextView.setVisibility(View.GONE);
                ((FitnessActivityViewHolder)holder).distanceImageView.setVisibility(View.GONE);
            }
            if (hideStepCount(currentFitnessActivityItem.getmTitleText())) {
                ((FitnessActivityViewHolder)holder).stepCountTextView.setVisibility(View.GONE);
                ((FitnessActivityViewHolder)holder).stepCountImageView.setVisibility(View.GONE);
            }

        } else {
            ((DaySeparatorViewHolder)holder).dayTitleTextView.setText(currentFitnessActivityItem.getmTitleText());
        }

    }

    boolean hideStepCount(String activityType) {
        return !(FitnessUtility.getActivityDisplayName(DetectedActivity.WALKING).equals(activityType) || FitnessUtility.getActivityDisplayName(DetectedActivity.RUNNING).equals(activityType));
    }

    boolean hideDistance(String activityType) {
        return (FitnessUtility.getActivityDisplayName(FitnessUtility.SLEEP_CODE).equals(activityType));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == FITNESS_VIEW_TYPE) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_activity_fitness_activity_list_item, parent, false);
            FitnessActivityViewHolder fitnessActivityViewHolder = new FitnessActivityViewHolder(v, activityItemClickListener);
            return fitnessActivityViewHolder;
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_activity_day_separator_list_item, parent, false);
            DaySeparatorViewHolder daySeparatorViewHolder = new DaySeparatorViewHolder(v, activityItemClickListener);
            return daySeparatorViewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        FitnessActivityItem currentFitnessActivityItem = fitnessActivityItems.get(position);
        if (currentFitnessActivityItem.getmImageResource() == 0) {
            return DAY_SEPARATOR_VIEW_TYPE;
        } else {
            return FITNESS_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return fitnessActivityItems.size();
    }
}
