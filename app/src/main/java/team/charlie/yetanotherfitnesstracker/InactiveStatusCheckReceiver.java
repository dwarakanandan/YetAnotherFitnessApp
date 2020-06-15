package team.charlie.yetanotherfitnesstracker;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.DetectedActivity;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;

import static team.charlie.yetanotherfitnesstracker.DashboardActivity.CHANNEL_ID;

public class InactiveStatusCheckReceiver extends BroadcastReceiver {

    private static final String TAG = "InactiveStatusCheckRece";

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar currentTime = Calendar.getInstance();
        if (currentTime.get(Calendar.HOUR_OF_DAY) <= 6 || currentTime.get(Calendar.HOUR_OF_DAY) >= 20) {
            Log.d(TAG, "onReceive: Silent hours! skipping inactive status check");
        } else {
            new StatusCheckAsyncTask(new WeakReference<>(context)).execute();
        }
    }

    private static class StatusCheckAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<Context> context;
        private boolean inactive = false;

        StatusCheckAsyncTask(WeakReference<Context> context) {
            this.context = context;
        }


        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            List<FitnessActivity> latestActivity = fitnessDatabase.fitnessActivityDao().getLatestActivity();
            if ( latestActivity !=null && latestActivity.size()>0 && (latestActivity.get(latestActivity.size() -1).getActivityType() == DetectedActivity.STILL)) {
                FitnessActivity latestStillActivity = latestActivity.get(latestActivity.size() - 1);
                long stillTimeInMilliSeconds = latestStillActivity.getEndTimeMilliSeconds() - latestStillActivity.getStartTimeMilliSeconds();
                long stillTimeInHours = stillTimeInMilliSeconds / (3600 * 1000);
                Log.d(TAG, "doInBackground: stillTimeInHours" + stillTimeInHours);
                if (stillTimeInHours >= 2) {
                    this.inactive = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (this.inactive) {
                Notification notification = new NotificationCompat.Builder(context.get(), CHANNEL_ID)
                        .setContentTitle("You have been inactive for over 2 Hours...")
                        .setContentText("Time to move some muscles!!")
                        .setSmallIcon(R.drawable.ic_inactive)
                        .setAutoCancel(true)
                        .build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.get());
                notificationManager.notify(6, notification);
            }
        }
    }
}
