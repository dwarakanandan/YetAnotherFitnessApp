package team.charlie.yetanotherfitnesstracker;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import java.lang.ref.WeakReference;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;

public class FitnessAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "FitnessAppWidgetProvide";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Log.d(TAG, "onUpdate: appWidgetId " + appWidgetId);
            new DailySummaryAsyncTask(new WeakReference<>(views), new WeakReference<>(context), appWidgetId).execute();
        }
    }

    private static class DailySummaryAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<RemoteViews> root;
        WeakReference<Context> context;
        Float stepProgress, distanceProgress, caloriesProgress, activeTimeProgress;
        int appWidgetId;

        DailySummaryAsyncTask(WeakReference<RemoteViews> root, WeakReference<Context> context, int appWidgetId) {
            this.root = root;
            this.context = context;
            this.appWidgetId = appWidgetId;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            List<FitnessActivity> fitnessActivitiesToday = fitnessDatabase.fitnessActivityDao().
                    getAllActivities(FitnessUtility.getNMinusTodayStartInMilliseconds(0),
                            FitnessUtility.getNMinusTodayEndInMilliseconds(0));
            stepProgress = FitnessUtility.aggregateFitnessActivitiesOnParameter(fitnessActivitiesToday, FitnessUtility.FITNESS_PARAMETER_STEPS);
            distanceProgress = FitnessUtility.aggregateFitnessActivitiesOnParameter(fitnessActivitiesToday, FitnessUtility.FITNESS_PARAMETER_DISTANCE);
            caloriesProgress = FitnessUtility.aggregateFitnessActivitiesOnParameter(fitnessActivitiesToday, FitnessUtility.FITNESS_PARAMETER_CALORIES);
            activeTimeProgress = FitnessUtility.aggregateFitnessActivitiesOnParameter(fitnessActivitiesToday, FitnessUtility.FITNESS_PARAMETER_ACTIVE_MINUTES);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            root.get().setTextViewText(R.id.widget_layout_step_count, String.valueOf(Math.round(stepProgress)));
            root.get().setTextViewText(R.id.widget_layout_distance, String.valueOf(Math.round(distanceProgress)));
            root.get().setTextViewText(R.id.widget_layout_calories, String.valueOf(Math.round(caloriesProgress)));
            root.get().setTextViewText(R.id.widget_layout_active_time, String.valueOf(Math.round(activeTimeProgress)));
            AppWidgetManager manager = AppWidgetManager.getInstance(context.get());
            manager.updateAppWidget(appWidgetId, root.get());
        }
    }
}
