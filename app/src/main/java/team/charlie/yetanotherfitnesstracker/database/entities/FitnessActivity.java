package team.charlie.yetanotherfitnesstracker.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "fitness_activity_table")
public class FitnessActivity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int activityType;

    private long startTimeMilliSeconds;

    private long endTimeMilliSeconds;

    private float stepCount;

    private long timeInMinutes;

    private double caloriesBurnt;

    private double distanceInMeters;

    public FitnessActivity(int activityType, long startTimeMilliSeconds, long endTimeMilliSeconds, float stepCount, long timeInMinutes, double caloriesBurnt, double distanceInMeters) {
        this.activityType = activityType;
        this.startTimeMilliSeconds = startTimeMilliSeconds;
        this.endTimeMilliSeconds = endTimeMilliSeconds;
        this.stepCount = stepCount;
        this.timeInMinutes = timeInMinutes;
        this.caloriesBurnt = caloriesBurnt;
        this.distanceInMeters = distanceInMeters;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public void setStartTimeMilliSeconds(long startTimeMilliSeconds) {
        this.startTimeMilliSeconds = startTimeMilliSeconds;
    }

    public void setEndTimeMilliSeconds(long endTimeMilliSeconds) {
        this.endTimeMilliSeconds = endTimeMilliSeconds;
    }

    public void setStepCount(float stepCount) {
        this.stepCount = stepCount;
    }

    public void setTimeInMinutes(long timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    public void setCaloriesBurnt(double caloriesBurnt) {
        this.caloriesBurnt = caloriesBurnt;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public int getId() {
        return id;
    }

    public int getActivityType() {
        return activityType;
    }

    public long getStartTimeMilliSeconds() {
        return startTimeMilliSeconds;
    }

    public long getEndTimeMilliSeconds() {
        return endTimeMilliSeconds;
    }

    public float getStepCount() {
        return stepCount;
    }

    public long getTimeInMinutes() {
        return timeInMinutes;
    }

    public double getCaloriesBurnt() {
        return caloriesBurnt;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }
}
