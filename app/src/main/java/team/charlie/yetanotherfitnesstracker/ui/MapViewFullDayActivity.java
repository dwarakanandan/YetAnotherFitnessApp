package team.charlie.yetanotherfitnesstracker.ui;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;


public class MapViewFullDayActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapViewFullDayActivity";

    private ArrayList<Integer> displayActivityIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_full_day);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view_full_day_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        displayActivityIds = this.getIntent().getIntegerArrayListExtra("ACTIVITY_IDS");
        Log.d(TAG, "onCreate: " + displayActivityIds.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        new MapViewFullDayActivity.DrawPolylineForDisplayActivities(new WeakReference<>(this), this.displayActivityIds, googleMap).execute();
    }

    private static class DrawPolylineForDisplayActivities extends AsyncTask<Integer, Void, Void> {

        WeakReference<MapViewFullDayActivity> mapViewActivity;
        GoogleMap googleMap;
        ArrayList<Integer> displayActivityIds;
        ArrayList<FitnessActivity> displayActivities;
        ArrayList<List<LocationWithStepCount>> allLocationsForAllDisplayActivities;

        public DrawPolylineForDisplayActivities(WeakReference<MapViewFullDayActivity> mapViewActivity, ArrayList<Integer> displayActivityIds, GoogleMap googleMap) {
            this.displayActivityIds = displayActivityIds;
            this.googleMap = googleMap;
            this.mapViewActivity = mapViewActivity;
            this.displayActivities = new ArrayList<>();
            this.allLocationsForAllDisplayActivities = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(mapViewActivity.get().getApplicationContext());
            for (Integer displayActivityId : displayActivityIds) {
                List<FitnessActivity> fitnessActivities = fitnessDatabase.fitnessActivityDao().getActivityById(displayActivityId);
                FitnessActivity displayActivity = fitnessActivities.get(0);
                List<LocationWithStepCount> allLocationsForDisplayActivity = fitnessDatabase.locationWithStepCountDao()
                        .getAllLocationWithStepCount(
                                displayActivity.getStartTimeMilliSeconds(),
                                displayActivity.getEndTimeMilliSeconds());
                displayActivities.add(displayActivity);
                allLocationsForAllDisplayActivities.add(allLocationsForDisplayActivity);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            drawPolylines();
        }

        private static final int POLYGON_STROKE_WIDTH_PX = 8;
        private static final int PATTERN_DASH_LENGTH_PX = 20;
        private static final int PATTERN_GAP_LENGTH_PX = 20;

        private static final PatternItem DOT = new Dot();
        private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
        private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

        private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);
        private static final List<PatternItem> PATTERN_POLYGON_BETA = Arrays.asList(DOT, GAP, DASH, GAP);
        private static final List<PatternItem> PATTERN_POLYGON_DEFAULT = Arrays.asList(DASH, DASH);

        int getPolylineColor(int activityType) {
            if (activityType == DetectedActivity.WALKING) {
                return Color.rgb(118,43,226);
            } else if (activityType == DetectedActivity.RUNNING) {
                return Color.rgb(139,69,19);
            } else {
                return Color.rgb(34,50,150);
            }
        }

        List<PatternItem> getPolylinePattern(int activityType) {
            if (activityType == DetectedActivity.WALKING) {
                return PATTERN_POLYGON_ALPHA;
            } else if (activityType == DetectedActivity.RUNNING) {
                return PATTERN_POLYGON_BETA;
            } else {
                return PATTERN_POLYGON_DEFAULT;
            }
        }


        void drawPolylines() {

            List<LatLng> allPaths = new ArrayList<>();

            for (int i = 0; i < allLocationsForAllDisplayActivities.size(); i++) {
                List<LocationWithStepCount> allLocationsForDisplayActivity = allLocationsForAllDisplayActivities.get(i);
                List<LatLng> path = new ArrayList<>();

                for (LocationWithStepCount locationWithStepCount : allLocationsForDisplayActivity) {
                    if (locationWithStepCount.getAccuracy() < 100) {
                        path.add(new LatLng(locationWithStepCount.getLatitude(), locationWithStepCount.getLongitude()));
                        allPaths.add(new LatLng(locationWithStepCount.getLatitude(), locationWithStepCount.getLongitude()));
                    }
                }
                Log.d(TAG, "drawPolyline: path length:" + path.size());

                if (path.size() > 0) {
                    googleMap.addPolyline((new PolylineOptions()).addAll(path).color(getPolylineColor(displayActivities.get(i).getActivityType())).pattern(getPolylinePattern(displayActivities.get(i).getActivityType())));

                    FitnessActivity fitnessActivity = displayActivities.get(i);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(fitnessActivity.getStartTimeMilliSeconds());
                    String startCal = calendar.get(Calendar.HOUR_OF_DAY) +":" + calendar.get(Calendar.MINUTE);
                    calendar.setTimeInMillis(fitnessActivity.getEndTimeMilliSeconds());
                    String endCal = calendar.get(Calendar.HOUR_OF_DAY) +":" + calendar.get(Calendar.MINUTE);
                    String startMarker = "START: " + FitnessUtility.getActivityDisplayName(fitnessActivity.getActivityType()) +
                            "  "+startCal+" - "+endCal;
                    String endMarker = "END: " + FitnessUtility.getActivityDisplayName(fitnessActivity.getActivityType()) +
                            "  "+startCal+" - "+endCal;
                    googleMap.addMarker(new MarkerOptions()
                            .position(path.get(0))
                            .title(startMarker)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    googleMap.addMarker(new MarkerOptions()
                            .position(path.get(path.size() - 1))
                            .title(endMarker)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
            }


            if (allPaths.size() > 0) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : allPaths) {
                    builder.include(latLng);
                }
                LatLngBounds bounds = builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100), 1000, null);
            } else {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLng center = new LatLng(49.878708, 8.646927);
                builder.include(center);
                builder.include(new LatLng(center.latitude - 0.1f, center.longitude - 0.1f));
                builder.include(new LatLng(center.latitude + 0.1f, center.longitude + 0.1f));
                LatLngBounds bounds = builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                Toast.makeText(mapViewActivity.get().getApplicationContext(), "Valid location data not found!!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
