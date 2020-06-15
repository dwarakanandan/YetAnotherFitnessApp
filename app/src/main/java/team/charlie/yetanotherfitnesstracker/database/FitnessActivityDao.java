package team.charlie.yetanotherfitnesstracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;

@Dao
public interface FitnessActivityDao {

    @Insert
    void insert(FitnessActivity fitnessActivity);

    @Delete
    void delete(FitnessActivity fitnessActivity);

    @Update
    void update(FitnessActivity fitnessActivity);

    @Query("SELECT * FROM fitness_activity_table WHERE id = :activityId")
    List<FitnessActivity> getActivityById(int activityId);

    @Query("SELECT * FROM fitness_activity_table WHERE startTimeMilliSeconds BETWEEN :start AND :end")
    List<FitnessActivity> getAllActivities(long start, long end);

    @Query("SELECT * FROM fitness_activity_table WHERE (activityType = :activityType) AND (startTimeMilliSeconds BETWEEN :start AND :end) ORDER BY startTimeMilliSeconds")
    List<FitnessActivity> getAllActivitiesForType(int activityType, long start, long end);

    @Query("SELECT * FROM fitness_activity_table ORDER BY id DESC LIMIT 1")
    List<FitnessActivity> getLatestActivity();

    @Query("DELETE FROM fitness_activity_table")
    void purgeTable();

}
