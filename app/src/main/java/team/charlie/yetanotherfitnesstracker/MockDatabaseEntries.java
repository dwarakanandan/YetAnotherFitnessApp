package team.charlie.yetanotherfitnesstracker;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;

public class MockDatabaseEntries {

    MockDatabaseEntries(Context context) {
        ArrayList<LatLng> locations1 = new ArrayList<>();
        long time1 = 1583020800000L;
        locations1.add(new LatLng(49.883309, 8.666973));
        locations1.add(new LatLng(49.883297, 8.667858));
        locations1.add(new LatLng(49.883261, 8.669846));
        locations1.add(new LatLng(49.882738, 8.670489));
        locations1.add(new LatLng(49.882710, 8.671224));
        locations1.add(new LatLng(49.882014, 8.670910));
        locations1.add(new LatLng(49.881653, 8.670319));

        ArrayList<LatLng> locations2 = new ArrayList<>();
        long time2 = 1583024400000L;
        locations2.add(new LatLng(49.893309, 8.666973));
        locations2.add(new LatLng(49.893297, 8.667858));
        locations2.add(new LatLng(49.893261, 8.669846));
        locations2.add(new LatLng(49.892738, 8.670489));
        locations2.add(new LatLng(49.892710, 8.671224));
        locations2.add(new LatLng(49.892014, 8.670910));
        locations2.add(new LatLng(49.891653, 8.670319));
        Toast.makeText(context, "Mocked DB", Toast.LENGTH_SHORT).show();
        new Thread() {
            @Override
            public void run() {
                FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context);
                for (LatLng location: locations1) {
                    LocationWithStepCount locationWithStepCount =
                            new LocationWithStepCount(time1,
                                    location.latitude,
                                    location.longitude,
                                    1,
                                    0);
                    fitnessDatabase.locationWithStepCountDao().insert(locationWithStepCount);
                }

                for (LatLng location: locations2) {
                    LocationWithStepCount locationWithStepCount =
                            new LocationWithStepCount(time2,
                                    location.latitude,
                                    location.longitude,
                                    1,
                                    0);
                    fitnessDatabase.locationWithStepCountDao().insert(locationWithStepCount);
                }
            }
        }.start();
    }
}
