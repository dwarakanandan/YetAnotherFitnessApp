package team.charlie.yetanotherfitnesstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.location.DetectedActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import team.charlie.yetanotherfitnesstracker.api.ActivityInsertRequest;
import team.charlie.yetanotherfitnesstracker.api.ActivityUpdateRequest;
import team.charlie.yetanotherfitnesstracker.api.AddWeightRequest;
import team.charlie.yetanotherfitnesstracker.api.UpdateUserLocationRequest;
import team.charlie.yetanotherfitnesstracker.api.UpdateUserProfileRequest;
import team.charlie.yetanotherfitnesstracker.api.UpdateWeightRequest;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivityBackupStatus;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;
import team.charlie.yetanotherfitnesstracker.database.entities.WeightItem;

public class BackupTask implements ApiClientActivity {

    private static BackupTask mInstance;
    private WeakReference<Context> callingContext;
    private boolean backupInProgress;


    public static BackupTask getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BackupTask(new WeakReference<>(context));
            return mInstance;
        } else {
            mInstance.callingContext = new WeakReference<>(context);
            return mInstance;
        }
    }

    private BackupTask(WeakReference<Context> context) {
        callingContext = context;
    }

    void triggerBackup() {
        if (!backupInProgress) {
            backupInProgress = true;
            new BackupAsyncTask(this, callingContext).execute();
        }
    }

    @Override
    public void onActivityInsertSuccess(int fitnessActivityId, String remoteBackupId) {
        new UpdateFitnessActivityBackupStatusAsyncTask(callingContext, fitnessActivityId, remoteBackupId).execute();
    }

    @Override
    public void onActivityUpdateSuccess(int fitnessActivityId, String remoteBackupId) {
        new UpdateFitnessActivityBackupStatusAsyncTask(callingContext, fitnessActivityId, remoteBackupId).execute();
    }

    @Override
    public void onAddWeightSuccess(int weightEntryId, String remoteId) {
        new UpdateWeightItemBackupStatusAsyncTask(callingContext, weightEntryId, remoteId).execute();
    }

    @Override
    public void onUpdateWeightSuccess(int weightEntryId, String remoteId) {
        new UpdateWeightItemBackupStatusAsyncTask(callingContext, weightEntryId, remoteId).execute();
    }

    @Override
    public void onUpdateWeightFailure(int weightEntryId, String remoteId) {
        new UpdateWeightItemBackupStatusAsyncTask(callingContext, weightEntryId, remoteId).execute();
    }

    private static class BackupAsyncTask extends AsyncTask<Integer, Void, Void> {
        WeakReference<Context> context;
        BackupTask backupTask;

        BackupAsyncTask(BackupTask backupTask, WeakReference<Context> context) {
            this.backupTask = backupTask;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            backupFitnessActivities(fitnessDatabase);
            backupUserWeights(fitnessDatabase);
            backupUserProfile();
            backupUserLocation();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            backupTask.backupInProgress = false;
        }

        void backupUserLocation() {
            SharedPreferences sharedPref = Objects.requireNonNull(context.get()).getSharedPreferences(
                    context.get().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String latitude = sharedPref.getString(UserProfile.CURRENT_LATITUDE, null);
            String longitude = sharedPref.getString(UserProfile.CURRENT_LONGITUDE, null);
            if ((latitude != null) && (longitude != null)) {
                UpdateUserLocationRequest updateUserLocationRequest = new UpdateUserLocationRequest(backupTask, context.get());
                updateUserLocationRequest.updateLocation(latitude, longitude);
            }
        }

        void backupUserWeights(FitnessDatabase fitnessDatabase) {
            List<WeightItem> pendingWeightItems = fitnessDatabase.weightItemDao().getPendingWeightItems();
            List<WeightItem> updatedWeightItems = fitnessDatabase.weightItemDao().getUpdatedWeightItems();
            List<WeightItem> deletedWeightItems = fitnessDatabase.weightItemDao().getDeletedWeightItems();

            AddWeightRequest addWeightRequest = new AddWeightRequest(backupTask, context.get());
            for (WeightItem weightItem : pendingWeightItems) {
                addWeightRequest.addWeightItem(weightItem);
            }

            UpdateWeightRequest updateWeightRequest = new UpdateWeightRequest(backupTask, context.get());
            for (WeightItem weightItem : updatedWeightItems) {
                updateWeightRequest.updateWeightItem(weightItem, false);
            }

            for (WeightItem weightItem : deletedWeightItems) {
                updateWeightRequest.updateWeightItem(weightItem, true);
            }
        }

        void backupUserProfile() {
            UpdateUserProfileRequest updateUserProfileRequest = new UpdateUserProfileRequest(backupTask, context.get());
            UserProfile userProfile = new UserProfile();
            SharedPreferences sharedPref = Objects.requireNonNull(context.get()).getSharedPreferences(
                    context.get().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            if (sharedPref.getBoolean(UserProfile.UPDATED, true)) {
                userProfile.loadUserProfileFromSharedPreferences(sharedPref);
                updateUserProfileRequest.updateProfile(userProfile);
                sharedPref.edit().putBoolean(UserProfile.UPDATED, false).apply();
            }
        }

        void backupFitnessActivities(FitnessDatabase fitnessDatabase) {
            List<FitnessActivityBackupStatus> pendingFitnessActivities = fitnessDatabase.fitnessActivityBackupStatusDao().getPendingFitnessActivities();
            List<FitnessActivityBackupStatus> updatedFitnessActivities = fitnessDatabase.fitnessActivityBackupStatusDao().getUpdatedFitnessActivities();
            List<FitnessActivityBackupStatus> deletedFitnessActivities = fitnessDatabase.fitnessActivityBackupStatusDao().getDeletedFitnessActivities();

            List<FitnessActivity> pendingFitnessActivitiesForBackup = new ArrayList<>();
            List<FitnessActivity> updatedFitnessActivitiesForBackup = new ArrayList<>();

            for (FitnessActivityBackupStatus fitnessActivityBackupStatus : pendingFitnessActivities) {
                List<FitnessActivity> currentActivity = fitnessDatabase.fitnessActivityDao().getActivityById(fitnessActivityBackupStatus.getId());
                pendingFitnessActivitiesForBackup.add(currentActivity.get(0));
            }
            ActivityInsertRequest activityInsertRequest = new ActivityInsertRequest(backupTask, context.get());
            for (FitnessActivity fitnessActivity : pendingFitnessActivitiesForBackup) {
                List<LocationWithStepCount> locationWithStepCounts = fitnessDatabase.locationWithStepCountDao().getAccurateLocationWithStepCount(fitnessActivity.getStartTimeMilliSeconds(), fitnessActivity.getEndTimeMilliSeconds(), 15.0f);
                activityInsertRequest.insertActivity(fitnessActivity, locationWithStepCounts);
            }

            for (FitnessActivityBackupStatus fitnessActivityBackupStatus : updatedFitnessActivities) {
                List<FitnessActivity> currentActivity = fitnessDatabase.fitnessActivityDao().getActivityById(fitnessActivityBackupStatus.getId());
                updatedFitnessActivitiesForBackup.add(currentActivity.get(0));
            }
            ActivityUpdateRequest activityUpdateRequest = new ActivityUpdateRequest(backupTask, context.get());
            for (int i = 0; i < updatedFitnessActivitiesForBackup.size(); i++) {
                List<LocationWithStepCount> locationWithStepCounts = fitnessDatabase.locationWithStepCountDao().getAccurateLocationWithStepCount(updatedFitnessActivitiesForBackup.get(i).getStartTimeMilliSeconds(), updatedFitnessActivitiesForBackup.get(i).getEndTimeMilliSeconds(), 15.0f);
                activityUpdateRequest.updateActivity(updatedFitnessActivitiesForBackup.get(i), locationWithStepCounts, updatedFitnessActivities.get(i), false);
            }

            for (FitnessActivityBackupStatus fitnessActivityBackupStatus : deletedFitnessActivities) {
                FitnessActivity fitnessActivity = new FitnessActivity(DetectedActivity.WALKING,
                        FitnessUtility.getNMinusTodayStartInMilliseconds(0),
                        FitnessUtility.getNMinusTodayStartInMilliseconds(0),
                        0,
                        0,
                        0,
                        0);
                ArrayList<LocationWithStepCount> locationWithStepCounts = new ArrayList<>();
                activityUpdateRequest.updateActivity(fitnessActivity, locationWithStepCounts, fitnessActivityBackupStatus, true);
            }

        }
    }

    private static class UpdateFitnessActivityBackupStatusAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<Context> context;
        int fitnessActivityId;
        String remoteBackupId;

        UpdateFitnessActivityBackupStatusAsyncTask(WeakReference<Context> context, int fitnessActivityId, String remoteBackupId) {
            this.context = context;
            this.fitnessActivityId = fitnessActivityId;
            this.remoteBackupId = remoteBackupId;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            List<FitnessActivityBackupStatus> fitnessActivityBackupStatus = fitnessDatabase.fitnessActivityBackupStatusDao().getBackupStatusById(fitnessActivityId);

            if (fitnessActivityBackupStatus.get(0).getMarkForDelete() == 1) {
                fitnessDatabase.fitnessActivityBackupStatusDao().delete(fitnessActivityBackupStatus.get(0));
            } else if (fitnessActivityBackupStatus.get(0).getUpdated() == 1) {
                fitnessActivityBackupStatus.get(0).setUpdated(0);
                fitnessDatabase.fitnessActivityBackupStatusDao().update(fitnessActivityBackupStatus.get(0));
            } else {
                fitnessActivityBackupStatus.get(0).setBackupDone(1);
                fitnessActivityBackupStatus.get(0).setRemoteBackupId(remoteBackupId);
                fitnessDatabase.fitnessActivityBackupStatusDao().update(fitnessActivityBackupStatus.get(0));
            }

            return null;
        }
    }

    private static class UpdateWeightItemBackupStatusAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<Context> context;
        int weightEntryId;
        String remoteBackupId;

        UpdateWeightItemBackupStatusAsyncTask(WeakReference<Context> context, int weightEntryId, String remoteBackupId) {
            this.context = context;
            this.weightEntryId = weightEntryId;
            this.remoteBackupId = remoteBackupId;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            List<WeightItem> weightItems = fitnessDatabase.weightItemDao().getWeightItemById(weightEntryId);
            if (weightItems.get(0).getMarkForDelete() == 1) {
                fitnessDatabase.weightItemDao().delete(weightItems.get(0));
            } else {
                weightItems.get(0).setBackupDone(1);
                weightItems.get(0).setUpdated(0);
                weightItems.get(0).setRemoteId(remoteBackupId);
                fitnessDatabase.weightItemDao().update(weightItems.get(0));
            }
            return null;
        }
    }
}
