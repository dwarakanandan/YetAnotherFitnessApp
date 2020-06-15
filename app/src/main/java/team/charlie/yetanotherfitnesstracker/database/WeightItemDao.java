package team.charlie.yetanotherfitnesstracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import team.charlie.yetanotherfitnesstracker.database.entities.WeightItem;


@Dao
public interface WeightItemDao {
    @Insert
    void insert(WeightItem weightItem);

    @Delete
    void delete(WeightItem weightItem);

    @Update
    void update(WeightItem weightItem);

    @Query("SELECT * FROM weight_item_table WHERE markForDelete = 0 ORDER BY timeStamp")
    List<WeightItem> getAllWeightItems();

    @Query("SELECT * FROM weight_item_table WHERE timeStamp = :timeStamp AND markForDelete = 0")
    List<WeightItem> getWeightItemByTimeStamp(long timeStamp);

    @Query("SELECT * FROM weight_item_table WHERE id = :id")
    List<WeightItem> getWeightItemById(int id);

    @Query("SELECT * FROM weight_item_table WHERE markForDelete = 0 ORDER BY timeStamp DESC LIMIT 1")
    List<WeightItem> getLatestWeightItem();

    @Query("SELECT * FROM weight_item_table WHERE backupDone = 0")
    List<WeightItem> getPendingWeightItems();

    @Query("SELECT * FROM weight_item_table WHERE updated = 1 AND markForDelete = 0")
    List<WeightItem> getUpdatedWeightItems();

    @Query("SELECT * FROM weight_item_table WHERE markForDelete = 1")
    List<WeightItem> getDeletedWeightItems();

    @Query("DELETE FROM weight_item_table")
    void purgeTable();
}
