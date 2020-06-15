package team.charlie.yetanotherfitnesstracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BackupTaskRegister {

    private static BackupTaskRegister mBackupTaskReceiver = null;
    private static final int BACKUP_INTERVAL = 120*1000;
    private PendingIntent mPendingIntent;
    private AlarmManager alarmManager;

    public static BackupTaskRegister getInstance() {
        if (mBackupTaskReceiver == null) {
            mBackupTaskReceiver = new BackupTaskRegister();
        }
        return mBackupTaskReceiver;
    }

    private BackupTaskRegister() {

    }

    public void registerNextBackupTask(Context context) {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, BackupTaskReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(context, 3, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), BACKUP_INTERVAL, mPendingIntent);
    }

}
