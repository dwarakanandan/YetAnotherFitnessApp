package team.charlie.yetanotherfitnesstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BackupTaskReceiver extends BroadcastReceiver {

    private static final String TAG = "BackupTaskReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (FitnessUtility.isNetworkAvailable(context)) {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            if (sharedPref.contains("ACTIVITY_SYNC_AFTER_FIRST_LOGIN") || sharedPref.contains("PROFILE_SYNC_AFTER_FIRST_LOGIN")) {
                Log.d(TAG, "onReceive: Skipping backup as SYNC after first login is pending");
                return;
            }
            Log.d(TAG, "onReceive: Backup triggered!");
            BackupTask backupTask = BackupTask.getInstance(context);
            backupTask.triggerBackup();
        }
    }
}
