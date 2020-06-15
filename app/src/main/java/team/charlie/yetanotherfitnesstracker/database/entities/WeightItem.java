package team.charlie.yetanotherfitnesstracker.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weight_item_table")
public class WeightItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long timeStamp;

    private float weight;

    private int backupDone;

    private int updated;

    private int markForDelete;

    private String remoteId;

    public WeightItem(long timeStamp, float weight, int backupDone, int updated, int markForDelete, String remoteId) {
        this.timeStamp = timeStamp;
        this.weight = weight;
        this.backupDone = backupDone;
        this.updated = updated;
        this.markForDelete = markForDelete;
        this.remoteId = remoteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getBackupDone() {
        return backupDone;
    }

    public void setBackupDone(int backupDone) {
        this.backupDone = backupDone;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getMarkForDelete() {
        return markForDelete;
    }

    public void setMarkForDelete(int markForDelete) {
        this.markForDelete = markForDelete;
    }
}
