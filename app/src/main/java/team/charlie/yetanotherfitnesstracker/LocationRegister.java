package team.charlie.yetanotherfitnesstracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

public class LocationRegister {
    private static LocationRegister mInstance = null;
    private PendingIntent mPendingIntent;
    private FusedLocationProviderClient fusedLocationClient;

    public static LocationRegister getInstance() {
        if (mInstance == null) {
            mInstance = new LocationRegister();
        }
        return mInstance;
    }

    private LocationRegister() {

    }

    public void registerForLocationUpdates(Context mContext) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20 * 1000);
        mLocationRequest.setFastestInterval(20 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Intent mIntent = new Intent(mContext, LocationReceiver.class);
        mIntent.setAction("team.charlie.yetanotherfitnesstracker.LOCATION_UPDATE");
        mIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntent, 0);

        fusedLocationClient = new FusedLocationProviderClient(mContext);
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mPendingIntent);
    }

    public void unRegisterForLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(mPendingIntent);
    }
}
