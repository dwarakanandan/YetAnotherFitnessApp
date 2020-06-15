package team.charlie.yetanotherfitnesstracker.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "location_with_step_count_table")
public class LocationWithStepCount {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long timeStampMilliSeconds;

    private double latitude;

    private double longitude;

    private float accuracy;

    private float stepCount;

    public LocationWithStepCount(long timeStampMilliSeconds, double latitude, double longitude, float accuracy,float stepCount) {
        this.timeStampMilliSeconds = timeStampMilliSeconds;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.stepCount = stepCount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public long getTimeStampMilliSeconds() {
        return timeStampMilliSeconds;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getStepCount() {
        return stepCount;
    }
}
