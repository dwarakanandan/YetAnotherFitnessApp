package team.charlie.yetanotherfitnesstracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class InactiveStatusCheckRegister {

    private static InactiveStatusCheckRegister mInactiveStatusCheckRegister = null;
    private static final int CHECK_INTERVAL = 1800 * 1000;
    private PendingIntent mPendingIntent;
    private AlarmManager alarmManager;

    public static InactiveStatusCheckRegister getInstance() {
        if (mInactiveStatusCheckRegister == null) {
            mInactiveStatusCheckRegister = new InactiveStatusCheckRegister();
        }
        return mInactiveStatusCheckRegister;
    }

    private InactiveStatusCheckRegister() {

    }

    public void registerNextInactiveCheck(Context context) {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, InactiveStatusCheckReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(context, 3, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), CHECK_INTERVAL, mPendingIntent);
    }
}
