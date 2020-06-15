package team.charlie.yetanotherfitnesstracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivityBackupStatus;

@Dao
public interface FitnessActivityBackupStatusDao {

    @Insert
    void insert(FitnessActivityBackupStatus fitnessActivityBackupStatus);

    @Delete
    void delete(FitnessActivityBackupStatus fitnessActivityBackupStatus);

    @Update
    void update(FitnessActivityBackupStatus fitnessActivityBackupStatus);

    @Query("SELECT * FROM fitness_activity_backup_status_table WHERE backupDone = 0")
    List<FitnessActivityBackupStatus> getPendingFitnessActivities();

    @Query("SELECT * FROM fitness_activity_backup_status_table WHERE updated = 1 AND markForDelete = 0")
    List<FitnessActivityBackupStatus> getUpdatedFitnessActivities();

    @Query("SELECT * FROM fitness_activity_backup_status_table WHERE markForDelete = 1")
    List<FitnessActivityBackupStatus> getDeletedFitnessActivities();

    @Query("SELECT * FROM fitness_activity_backup_status_table WHERE id = :activityId")
    List<FitnessActivityBackupStatus> getBackupStatusById(int activityId);

    @Query("DELETE FROM fitness_activity_backup_status_table")
    void purgeTable();
}
