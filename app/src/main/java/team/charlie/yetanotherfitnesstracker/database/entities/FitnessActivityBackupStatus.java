package team.charlie.yetanotherfitnesstracker.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fitness_activity_backup_status_table")
public class FitnessActivityBackupStatus {

    @PrimaryKey
    private int id;

    private int backupDone;

    private int updated;

    private int markForDelete;

    private String remoteBackupId;

    public FitnessActivityBackupStatus(int id, int backupDone, int updated, int markForDelete, String remoteBackupId) {
        this.id = id;
        this.backupDone = backupDone;
        this.updated = updated;
        this.markForDelete = markForDelete;
        this.remoteBackupId = remoteBackupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBackupDone() {
        return backupDone;
    }

    public void setBackupDone(int backupDone) {
        this.backupDone = backupDone;
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

    public String getRemoteBackupId() {
        return remoteBackupId;
    }

    public void setRemoteBackupId(String remoteBackupId) {
        this.remoteBackupId = remoteBackupId;
    }
}
