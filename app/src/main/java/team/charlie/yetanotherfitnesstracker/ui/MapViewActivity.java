package team.charlie.yetanotherfitnesstracker.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapViewActivity";
    private int displayActivityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        displayActivityId = this.getIntent().getIntExtra("ACTIVITY_ID", 0);
        Log.d(TAG, "onCreate: called for activity ID" + displayActivityId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        new DrawPolylineForDisplayActivity(new WeakReference<>(this), this.displayActivityId, googleMap).execute();
    }

    private static class DrawPolylineForDisplayActivity extends AsyncTask<Integer, Void, Void> {

        WeakReference<MapViewActivity> mapViewActivity;
        GoogleMap googleMap;
        int displayActivityId;
        FitnessActivity displayActivity;
        List<LocationWithStepCount> allLocationsForDisplayActivity;

        public DrawPolylineForDisplayActivity(WeakReference<MapViewActivity> mapViewActivity, int displayActivityId, GoogleMap googleMap) {
            this.displayActivityId = displayActivityId;
            this.googleMap = googleMap;
            this.mapViewActivity = mapViewActivity;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(mapViewActivity.get().getApplicationContext());
            List<FitnessActivity> fitnessActivities = fitnessDatabase.fitnessActivityDao().getActivityById(displayActivityId);
            displayActivity = fitnessActivities.get(0);
            allLocationsForDisplayActivity = fitnessDatabase.locationWithStepCountDao()
                    .getAllLocationWithStepCount(
                            displayActivity.getStartTimeMilliSeconds(),
                            displayActivity.getEndTimeMilliSeconds());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setupActivityInfo();
            drawPolyline();
        }

        void setupActivityInfo() {
            TextView textViewActivityType = mapViewActivity.get().findViewById(R.id.map_view_activity_type);
            TextView textViewDistance = mapViewActivity.get().findViewById(R.id.map_view_distance_value);
            TextView textViewTime = mapViewActivity.get().findViewById(R.id.map_view_time_value);
            TextView textViewCalories = mapViewActivity.get().findViewById(R.id.map_view_calories_value);

            textViewActivityType.setText(FitnessUtility.getActivityDisplayName(displayActivity.getActivityType()));
            textViewDistance.setText(Math.round(displayActivity.getDistanceInMeters()) + " meters");
            textViewTime.setText(displayActivity.getTimeInMinutes() + " min");
            textViewCalories.setText(Math.round(displayActivity.getCaloriesBurnt()) + " calories");

        }

        void drawPolyline() {
            List<LatLng> path = new ArrayList<>();

            for (LocationWithStepCount i : allLocationsForDisplayActivity) {
                if (i.getAccuracy() < 100) {
                    path.add(new LatLng(i.getLatitude(), i.getLongitude()));
                    //Log.d(TAG, "drawPolyline: " + i.getLatitude() +", " + i.getLongitude() + "," + i.getAccuracy());
                }
            }
            Log.d(TAG, "drawPolyline: path length:" + path.size());

            if (path.size() > 0) {
                googleMap.addPolyline((new PolylineOptions()).addAll(path));


                googleMap.addMarker(new MarkerOptions()
                        .position(path.get(0))
                        .title("Start")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                googleMap.addMarker(new MarkerOptions()
                        .position(path.get(path.size() - 1))
                        .title("End")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : path) {
                    builder.include(latLng);
                }
                LatLngBounds bounds = builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 1000, null);
            } else {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLng center = new LatLng(49.878708, 8.646927);
                builder.include(center);
                builder.include(new LatLng(center.latitude-0.1f,center.longitude-0.1f));
                builder.include(new LatLng(center.latitude+0.1f,center.longitude+0.1f));
                LatLngBounds bounds = builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                Toast.makeText(mapViewActivity.get().getApplicationContext(), "Valid location data not found!!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}
