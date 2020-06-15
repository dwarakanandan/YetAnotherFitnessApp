package team.charlie.yetanotherfitnesstracker.ui.weightTracking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import team.charlie.yetanotherfitnesstracker.R;

public class WeightTrackingItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "WeightTrackingItemAdapt";
    WeightTrackingItemClickListener activity;
    ArrayList<WeightTrackingItem> weightTrackingItems;

    public WeightTrackingItemAdapter(WeightTrackingItemClickListener activity, ArrayList<WeightTrackingItem> weightTrackingItems) {
        this.activity = activity;
        this.weightTrackingItems = weightTrackingItems;
    }

    public static class WeightTrackingItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        WeightTrackingItemClickListener activity;
        public TextView dayTextView;
        public TextView weightTextView;
        public TextView bmiTextView;

        public WeightTrackingItemViewHolder(@NonNull View itemView, WeightTrackingItemClickListener activity) {
            super(itemView);
            this.activity = activity;
            this.dayTextView = itemView.findViewById(R.id.activity_weight_tracking_list_item_date);
            this.weightTextView = itemView.findViewById(R.id.activity_weight_tracking_list_item_weight);
            this.bmiTextView = itemView.findViewById(R.id.activity_weight_tracking_list_item_bmi);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            activity.onLongClick(getAdapterPosition());
            return false;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_weight_tracking_list_item, parent, false);
        WeightTrackingItemViewHolder weightTrackingItemViewHolder = new WeightTrackingItemViewHolder(v, this.activity);
        return weightTrackingItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WeightTrackingItem weightTrackingItem = this.weightTrackingItems.get(position);
        ((WeightTrackingItemViewHolder)holder).dayTextView.setText(weightTrackingItem.getDateText());
        ((WeightTrackingItemViewHolder)holder).weightTextView.setText(weightTrackingItem.getWeightText());
        ((WeightTrackingItemViewHolder)holder).bmiTextView.setText(weightTrackingItem.getBmiText());
    }

    @Override
    public int getItemCount() {
        return this.weightTrackingItems.size();
    }
}
