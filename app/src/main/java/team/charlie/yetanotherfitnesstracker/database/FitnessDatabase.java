package team.charlie.yetanotherfitnesstracker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivityBackupStatus;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;
import team.charlie.yetanotherfitnesstracker.database.entities.WeightItem;

@Database(entities = {FitnessActivity.class, LocationWithStepCount.class, FitnessActivityBackupStatus.class, WeightItem.class}, version = 1)
public abstract class FitnessDatabase extends RoomDatabase {

    private static FitnessDatabase fitnessDatabaseInstance;

    public abstract FitnessActivityDao fitnessActivityDao();

    public abstract LocationWithStepCountDao locationWithStepCountDao();

    public abstract FitnessActivityBackupStatusDao fitnessActivityBackupStatusDao();

    public abstract WeightItemDao weightItemDao();

    public static synchronized FitnessDatabase getInstance(Context context) {
        if (fitnessDatabaseInstance == null) {
            fitnessDatabaseInstance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    FitnessDatabase.class,
                    "fitness_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return fitnessDatabaseInstance;
    }

    public void purgeDatabase() {
        fitnessActivityBackupStatusDao().purgeTable();
        locationWithStepCountDao().purgeTable();
        fitnessActivityDao().purgeTable();
        weightItemDao().purgeTable();
    }

}
