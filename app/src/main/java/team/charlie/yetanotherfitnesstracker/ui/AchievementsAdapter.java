package team.charlie.yetanotherfitnesstracker.ui;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.R;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {
    private List<AchievementUnlocked> listAchievements;
    private List<AchievementUnlocked> listUserAchievements;
    private static final String TAG = "AchievementsAdapter";

    public AchievementsAdapter(List<AchievementUnlocked> listImage) {
        this.listAchievements = new ArrayList<>(listImage);
        this.listUserAchievements = new ArrayList<>();
        setCurrentAchievements();
    }


    public int getCurrentLevel(AchievementUnlocked achievement){
        int value = achievement.getValue();

        if(value!=0){
            for(int i=0;i<achievement.getLevels().size();i++){
                if(value<achievement.getLevels().get(i)){
                    return i;
                }
            }
        }
        return 0;
    }
    public void setCurrentAchievements(){
        for(AchievementUnlocked achivements : this.listAchievements){
            if(getCurrentLevel(achivements)!=0){
                   achivements.setLevel(getCurrentLevel(achivements));
                Log.d(TAG, "setCurrentAchievements: "+achivements.getLevel()+achivements.getType());
                   listUserAchievements.add(achivements);
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.activity_achievements_list_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AchievementUnlocked unlocked = this.listUserAchievements.get(position);
        int duration = Toast.LENGTH_LONG;
        CharSequence text =  unlocked.getTemplate_description().replace(" {value}"," "+String.valueOf(unlocked.getValue()));;

        if(unlocked.getType().equals("monthly_distance")) {
            if (unlocked.getLevel() == 1) {
                holder.imageView.setImageResource(R.drawable.ic_monthly_level1);

            } else  if (unlocked.getLevel() <= 3 &&unlocked.getLevel()>1) {
                holder.imageView.setImageResource(R.drawable.ic_montly_level2);

            }
            else{
                holder.imageView.setImageResource(R.drawable.ic_monthly_level5);
            }
        }
        else if(unlocked.getType().equals("total_distance")){
            if (unlocked.getLevel() == 1) {
                holder.imageView.setImageResource(R.drawable.ic_distance_level1);

            } else {
                holder.imageView.setImageResource(R.drawable.ic_distance_level2);

            }
        }
        else if(unlocked.getType().equals("total_steps")){
            if (unlocked.getLevel() == 1) {
                holder.imageView.setImageResource(R.drawable.ic_steps_level1);

            } else  if (unlocked.getLevel() <= 3 &&unlocked.getLevel()>1) {
                holder.imageView.setImageResource(R.drawable.ic_steps_level2);

            }
            else{
                holder.imageView.setImageResource(R.drawable.ic_steps_level5);
            }
        }

        else if(unlocked.getType().equals("daily_distance")){
            if (unlocked.getLevel() == 1) {
                holder.imageView.setImageResource(R.drawable.ic_daily_level1);

            } else  if (unlocked.getLevel() <= 3 &&unlocked.getLevel()>1) {
                holder.imageView.setImageResource(R.drawable.ic_daily_level3);

            }
            else{
                holder.imageView.setImageResource(R.drawable.ic_daily_level5);
            }
        }

        else if(unlocked.getType().equals("total_weight_loss")){
            if (unlocked.getLevel() == 1) {
                holder.imageView.setImageResource(R.drawable.ic_weight_level1);

            } else  if (unlocked.getLevel() <= 3 &&unlocked.getLevel()>1) {
                holder.imageView.setImageResource(R.drawable.ic_weight_level3);

            }
            else{
                holder.imageView.setImageResource(R.drawable.ic_weight_level2);
            }
        }
        else if(unlocked.getType().equals("total_running")){
            if (unlocked.getLevel() == 1) {
                holder.imageView.setImageResource(R.drawable.ic_running_level1);

            } else  if (unlocked.getLevel() <= 3 &&unlocked.getLevel()>1) {
                holder.imageView.setImageResource(R.drawable.ic_running_level3);

            }
            else{
                holder.imageView.setImageResource(R.drawable.ic_running_level5);
            }
        }
        else if(unlocked.getType().equals("total_walking")){
            if (unlocked.getLevel() == 1) {
                holder.imageView.setImageResource(R.drawable.ic_walking_level1);

            } else  if (unlocked.getLevel() <= 3 &&unlocked.getLevel()>1) {
                holder.imageView.setImageResource(R.drawable.ic_walking_level3);

            }
            else{
                holder.imageView.setImageResource(R.drawable.ic_walking_level5);
            }
        }

        else if(unlocked.getType().equals("total_biking")){
            if (unlocked.getLevel() == 1) {
                holder.imageView.setImageResource(R.drawable.ic_biking_level1);

            } else  if (unlocked.getLevel() <= 3 &&unlocked.getLevel()>1) {
                holder.imageView.setImageResource(R.drawable.ic_biking_level3);

            }
            else{
                holder.imageView.setImageResource(R.drawable.ic_biking_level5);
            }
        }

        else{
            if (unlocked.getLevel() == 1) {
                holder.imageView.setImageResource(R.drawable.ic_biking_level1);

            } else  if (unlocked.getLevel() <= 3 &&unlocked.getLevel()>1) {
                holder.imageView.setImageResource(R.drawable.ic_biking_level3);

            }
            else{
                holder.imageView.setImageResource(R.drawable.ic_biking_level5);
            }
        }

        holder.imageButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(v.getContext(), text, duration);
                    toast.show();
                }
            });


          holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return this.listUserAchievements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        ImageButton imageButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.achievementimageViiew);
            this.imageButton = itemView.findViewById(R.id.achievementInfoButton);
        }
    }

}
