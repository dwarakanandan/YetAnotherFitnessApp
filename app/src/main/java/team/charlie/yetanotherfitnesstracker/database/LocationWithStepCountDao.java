package team.charlie.yetanotherfitnesstracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;

@Dao
public interface LocationWithStepCountDao {

    @Insert
    void insert(LocationWithStepCount locationWithStepCount);

    @Delete
    void delete(LocationWithStepCount locationWithStepCount);

    @Update
    void update(LocationWithStepCount locationWithStepCount);

    @Query("SELECT * FROM location_with_step_count_table WHERE timeStampMilliSeconds BETWEEN :start AND :end")
    List<LocationWithStepCount> getAllLocationWithStepCount(long start, long end);

    @Query("SELECT * FROM location_with_step_count_table WHERE (timeStampMilliSeconds BETWEEN :start AND :end) AND accuracy <= :accuracy")
    List<LocationWithStepCount> getAccurateLocationWithStepCount(long start, long end, float accuracy);

    @Query("DELETE FROM location_with_step_count_table")
    void purgeTable();
}
