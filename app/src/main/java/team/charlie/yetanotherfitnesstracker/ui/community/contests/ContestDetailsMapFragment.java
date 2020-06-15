package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.R;

public class ContestDetailsMapFragment extends Fragment implements OnMapReadyCallback {

    private View root;

    private ContestItem contestItem;

    ContestDetailsMapFragment(ContestItem contestItem) {
        this.contestItem = contestItem;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_contest_details_map, container,false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_contest_details_map_view_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        drawMap(googleMap);
    }

    private void drawMap(GoogleMap googleMap) {
        List<ContestItemParticipant> participants = this.contestItem.getContestItemParticipants();
        List<LatLng> latLngs = new ArrayList<>();

        for (ContestItemParticipant participant: participants) {
            latLngs.add(new LatLng(participant.getLatitiude(), participant.getLongitude()));
        }

        if (latLngs.size() > 0) {
            for (int i = 0; i < latLngs.size(); i++) {
                googleMap.addMarker(new MarkerOptions()
                        .position(latLngs.get(i))
                        .title(participants.get(i).getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : latLngs) {
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
        }
    }
}
