package team.charlie.yetanotherfitnesstracker.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.DetectedActivity;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.UserProfile;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivityBackupStatus;

public class FitnessActivityDetailsActivity extends AppCompatActivity {

    private static final String TAG = "FitnessActivityDetailsA";
    int displayActivityId;

    ArrayList<String> spinnerItems = new ArrayList<>();
    ArrayList<Integer> spinnerItemCodes = new ArrayList<>();

    private FitnessActivity fitnessActivity;
    SharedPreferences sharedPref;

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    EditText editTextStepCount, editTextDistance, editTextCalories;
    ImageView imageViewStepCount, imageViewDistance;
    TextView textViewMeters;
    Spinner spinner;

    EditText editTextStartDate, editTextStartTime, editTextEndDate, editTextEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_details);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        displayActivityId = this.getIntent().getIntExtra("ACTIVITY_ID", -1);

        startCalendar.set(Calendar.MILLISECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);
        startCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.SECOND, 0);

        editTextStepCount = findViewById(R.id.activity_fitness_details_step_count);
        editTextDistance = findViewById(R.id.activity_fitness_details_distance);
        editTextCalories = findViewById(R.id.activity_fitness_details_calories);
        imageViewStepCount = findViewById(R.id.activity_fitness_details_image_step_count);
        imageViewDistance = findViewById(R.id.activity_fitness_details_image_distance);
        editTextStartDate = findViewById(R.id.activity_fitness_details_start_date);
        editTextStartTime = findViewById(R.id.activity_fitness_details_start_time);
        editTextEndDate = findViewById(R.id.activity_fitness_details_end_date);
        editTextEndTime = findViewById(R.id.activity_fitness_details_end_time);
        textViewMeters = findViewById(R.id.activity_fitness_details_text_meters);
        spinner = findViewById(R.id.activity_fitness_details_spinner);

        fitnessActivity = new FitnessActivity(DetectedActivity.WALKING,
                startCalendar.getTimeInMillis(),
                endCalendar.getTimeInMillis(),
                0,
                0,
                0,
                0);

        setupActivitySpinner();
        setupDateTimePicker();

        Button saveButton = findViewById(R.id.activity_fitness_details_button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityDetailsValid()) {
                    Log.d(TAG, "onClick: Activity details valid");
                    if (!hideDistance(fitnessActivity.getActivityType())) {
                        fitnessActivity.setDistanceInMeters(Double.valueOf(editTextDistance.getText().toString()));
                    }
                    if (!hideStepCount(fitnessActivity.getActivityType())) {
                        fitnessActivity.setStepCount(Float.valueOf(editTextStepCount.getText().toString()));
                    }
                    new SaveActivityAsyncTask(new WeakReference<>(FitnessActivityDetailsActivity.this)).execute();
                }
            }
        });

        Button deleteButton = findViewById(R.id.activity_fitness_details_button_delete);
        deleteButton.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        new DeleteActivityAsyncTask(new WeakReference<>(FitnessActivityDetailsActivity.this)).execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(FitnessActivityDetailsActivity.this);
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        if (displayActivityId != -1) {
            deleteButton.setVisibility(View.VISIBLE);
            new GetActivityAsyncTask(new WeakReference<>(this)).execute();
        }
    }

    boolean activityDetailsValid() {
        if (startCalendar.getTimeInMillis() >= endCalendar.getTimeInMillis()) {
            Log.d(TAG, "onClick: startCalendar greater than endCalendar");
            Toast.makeText(this.getApplicationContext(), "Activity cannot end be before it starts!!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if ( !hideDistance(fitnessActivity.getActivityType()) && editTextDistance.getText().toString().equals("")) {
            Log.d(TAG, "onClick: Distance cannot be empty");
            Toast.makeText(this.getApplicationContext(), "Enter a valid distance!!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextCalories.getText().toString().equals("")) {
            Log.d(TAG, "onClick: Calories cannot be empty");
            Toast.makeText(this.getApplicationContext(), "Enter a valid calorie count!!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if ( !hideStepCount(fitnessActivity.getActivityType()) && editTextStepCount.getText().toString().equals("")) {
            Log.d(TAG, "onClick: StepCount cannot be empty");
            Toast.makeText(this.getApplicationContext(), "Enter a valid step count!!",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    boolean hideStepCount(int activityType) {
        return !(DetectedActivity.WALKING == activityType || DetectedActivity.RUNNING == activityType);
    }

    boolean hideDistance(int activityType) {
        return (FitnessUtility.SLEEP_CODE == activityType);
    }

    void hideWidgetsOnSpinnerChange(int position) {
        if (hideStepCount(position)) {
            editTextStepCount.setVisibility(View.GONE);
            imageViewStepCount.setVisibility(View.GONE);
        } else {
            editTextStepCount.setVisibility(View.VISIBLE);
            imageViewStepCount.setVisibility(View.VISIBLE);
        }

        if (hideDistance(position)) {
            editTextDistance.setVisibility(View.GONE);
            imageViewDistance.setVisibility(View.GONE);
            textViewMeters.setVisibility(View.GONE);
        } else {
            editTextDistance.setVisibility(View.VISIBLE);
            imageViewDistance.setVisibility(View.VISIBLE);
            textViewMeters.setVisibility(View.VISIBLE);
        }
    }

    void setupActivitySpinner() {


        spinnerItemCodes.add(DetectedActivity.WALKING);
        spinnerItemCodes.add(DetectedActivity.RUNNING);
        spinnerItemCodes.add(DetectedActivity.ON_BICYCLE);
        spinnerItemCodes.add(DetectedActivity.IN_VEHICLE);
        spinnerItemCodes.add(FitnessUtility.SLEEP_CODE);

        spinnerItems.add(FitnessUtility.WALKING);
        spinnerItems.add(FitnessUtility.RUNNING);
        spinnerItems.add(FitnessUtility.BICYCLE);
        spinnerItems.add(FitnessUtility.VEHICLE);
        spinnerItems.add(FitnessUtility.SLEEP);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fitnessActivity.setActivityType(spinnerItemCodes.get(position));

                hideWidgetsOnSpinnerChange(fitnessActivity.getActivityType());
                recalculateCalories();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    void setupDateTimePicker() {
        java.text.DateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault());
        java.text.DateFormat timeFormat = new SimpleDateFormat("hh : mm  aa", Locale.getDefault());

        editTextStartDate.setCursorVisible(false);
        editTextStartDate.setFocusable(false);
        editTextStartDate.setText(dateFormat.format(startCalendar.getTime()));
        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(startCalendar, editTextStartDate, FitnessActivityDetailsActivity.this);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        editTextStartTime.setCursorVisible(false);
        editTextStartTime.setFocusable(false);
        editTextStartTime.setText(timeFormat.format(startCalendar.getTime()));
        editTextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(startCalendar, editTextStartTime, FitnessActivityDetailsActivity.this);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        editTextEndDate.setCursorVisible(false);
        editTextEndDate.setFocusable(false);
        editTextEndDate.setText(dateFormat.format(endCalendar.getTime()));
        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(endCalendar, editTextEndDate, FitnessActivityDetailsActivity.this);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        editTextEndTime.setCursorVisible(false);
        editTextEndTime.setFocusable(false);
        editTextEndTime.setText(timeFormat.format(endCalendar.getTime()));
        editTextEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(endCalendar, editTextEndTime, FitnessActivityDetailsActivity.this);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

    void recalculateCalories() {
        if (startCalendar.getTimeInMillis() >= endCalendar.getTimeInMillis()) {
            fitnessActivity.setCaloriesBurnt(0);
            editTextCalories.setText("0");
        } else {
            double caloriesBurnt = FitnessUtility.getCaloriesBurnt(Math.round(sharedPref.getFloat(UserProfile.WEIGHT, 75)), fitnessActivity.getActivityType(),
                    fitnessActivity.getTimeInMinutes());
            fitnessActivity.setCaloriesBurnt(caloriesBurnt);
            editTextCalories.setText(String.valueOf(Math.round(caloriesBurnt)));
        }
    }

    void timeChanged() {
        fitnessActivity.setEndTimeMilliSeconds(endCalendar.getTimeInMillis());
        fitnessActivity.setStartTimeMilliSeconds(startCalendar.getTimeInMillis());
        fitnessActivity.setTimeInMinutes(FitnessUtility.getActivityTimeInMinutes(fitnessActivity.getStartTimeMilliSeconds(),
                fitnessActivity.getEndTimeMilliSeconds()));
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private Calendar date;
        private EditText editText;
        private FitnessActivityDetailsActivity parent;

        DatePickerFragment(Calendar date, EditText editText, FitnessActivityDetailsActivity parent) {
            this.date = date;
            this.editText = editText;
            this.parent = parent;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = date.get(Calendar.YEAR);
            int month = date.get(Calendar.MONTH);
            int day = date.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            date.set(year, month, day,
                    date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
            java.text.DateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault());
            editText.setText(dateFormat.format(date.getTime()));

            parent.timeChanged();
            parent.recalculateCalories();
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private Calendar date;
        private EditText editTextTime;
        private FitnessActivityDetailsActivity parent;

        TimePickerFragment(Calendar date, EditText editTextTime, FitnessActivityDetailsActivity parent) {
            this.date = date;
            this.editTextTime = editTextTime;
            this.parent = parent;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = date.get(Calendar.HOUR_OF_DAY);
            int minute = date.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                    hourOfDay, minute, 0);
            java.text.DateFormat timeFormat = new SimpleDateFormat("hh : mm  aa", Locale.getDefault());
            editTextTime.setText(timeFormat.format(date.getTime()));

            parent.timeChanged();
            parent.recalculateCalories();
        }
    }

    private static class GetActivityAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<FitnessActivityDetailsActivity> parent;
        FitnessActivity displayActivity;


        public GetActivityAsyncTask(WeakReference<FitnessActivityDetailsActivity> parent) {
            this.parent = parent;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(parent.get().getApplicationContext());
            List<FitnessActivity> fitnessActivities = fitnessDatabase.fitnessActivityDao().getActivityById(parent.get().displayActivityId);
            displayActivity = fitnessActivities.get(0);
            return null;
        }

        int getSpinnerPositionForActivity(int activityId) {
            switch (activityId) {
                case DetectedActivity.WALKING: return 0;
                case DetectedActivity.RUNNING: return 1;
                case DetectedActivity.ON_BICYCLE: return 2;
                case DetectedActivity.IN_VEHICLE: return 3;
                case FitnessUtility.SLEEP_CODE: return 4;
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            parent.get().fitnessActivity.setId(displayActivity.getId());
            parent.get().startCalendar.setTimeInMillis(displayActivity.getStartTimeMilliSeconds());
            parent.get().endCalendar.setTimeInMillis(displayActivity.getEndTimeMilliSeconds());
            parent.get().fitnessActivity.setDistanceInMeters(displayActivity.getDistanceInMeters());
            parent.get().fitnessActivity.setCaloriesBurnt(displayActivity.getCaloriesBurnt());
            parent.get().fitnessActivity.setStepCount(displayActivity.getStepCount());
            parent.get().timeChanged();


            java.text.DateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault());
            java.text.DateFormat timeFormat = new SimpleDateFormat("hh : mm  aa", Locale.getDefault());

            parent.get().spinner.setSelection(getSpinnerPositionForActivity(displayActivity.getActivityType()));
            parent.get().editTextStartDate.setText(dateFormat.format(displayActivity.getStartTimeMilliSeconds()));
            parent.get().editTextStartTime.setText(timeFormat.format(displayActivity.getStartTimeMilliSeconds()));
            parent.get().editTextEndDate.setText(dateFormat.format(displayActivity.getEndTimeMilliSeconds()));
            parent.get().editTextEndTime.setText(timeFormat.format(displayActivity.getEndTimeMilliSeconds()));

            parent.get().editTextDistance.setText(String.valueOf(Math.round(displayActivity.getDistanceInMeters())));
            parent.get().editTextCalories.setText(String.valueOf(displayActivity.getCaloriesBurnt()));
            parent.get().editTextStepCount.setText(String.valueOf(Math.round(displayActivity.getStepCount())));
        }
    }

    private static class SaveActivityAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<FitnessActivityDetailsActivity> parent;

        public SaveActivityAsyncTask(WeakReference<FitnessActivityDetailsActivity> parent) {
            this.parent = parent;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(parent.get().getApplicationContext());
            if (parent.get().displayActivityId == -1) {
                List<FitnessActivity> currentActivitiesList = fitnessDatabase.fitnessActivityDao().
                        getAllActivities(FitnessUtility.getNMinusTodayStartInMilliseconds(0),
                                FitnessUtility.getNMinusTodayEndInMilliseconds(0));
                if (currentActivitiesList.size() == 0) {
                    FitnessActivity tempStillActivity = new FitnessActivity(DetectedActivity.STILL,
                            FitnessUtility.getNMinusTodayStartInMilliseconds(0),
                            parent.get().fitnessActivity.getStartTimeMilliSeconds(),
                            0,
                            0,
                            0,
                            0);
                    tempStillActivity.setTimeInMinutes(FitnessUtility.getActivityTimeInMinutes(tempStillActivity.getStartTimeMilliSeconds(),tempStillActivity.getEndTimeMilliSeconds()));
                    fitnessDatabase.fitnessActivityDao().insert(tempStillActivity);
                }
                else {
                    FitnessActivity tempStillActivity = currentActivitiesList.get(currentActivitiesList.size() - 1);
                    tempStillActivity.setEndTimeMilliSeconds(parent.get().fitnessActivity.getStartTimeMilliSeconds());
                    tempStillActivity.setTimeInMinutes(FitnessUtility.getActivityTimeInMinutes(tempStillActivity.getStartTimeMilliSeconds(),tempStillActivity.getEndTimeMilliSeconds()));
                    fitnessDatabase.fitnessActivityDao().update(tempStillActivity);
                }

                fitnessDatabase.fitnessActivityDao().insert(parent.get().fitnessActivity);

                List<FitnessActivity> latestActivity = fitnessDatabase.fitnessActivityDao().getLatestActivity();
                fitnessDatabase.fitnessActivityBackupStatusDao().insert(new FitnessActivityBackupStatus(latestActivity.get(0).getId(), 0, 0, 0, ""));


            } else {
                fitnessDatabase.fitnessActivityDao().update(parent.get().fitnessActivity);
                List<FitnessActivityBackupStatus> fitnessActivityBackupStatus = fitnessDatabase.fitnessActivityBackupStatusDao().getBackupStatusById(parent.get().fitnessActivity.getId());
                fitnessActivityBackupStatus.get(0).setUpdated(1);
                fitnessDatabase.fitnessActivityBackupStatusDao().update(fitnessActivityBackupStatus.get(0));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            parent.get().onBackPressed();
        }
    }

    private static class DeleteActivityAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<FitnessActivityDetailsActivity> parent;

        public DeleteActivityAsyncTask(WeakReference<FitnessActivityDetailsActivity> parent) {
            this.parent = parent;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(parent.get().getApplicationContext());
            List<FitnessActivityBackupStatus> fitnessActivityBackupStatus = fitnessDatabase.fitnessActivityBackupStatusDao().getBackupStatusById(parent.get().fitnessActivity.getId());

            fitnessDatabase.fitnessActivityDao().delete(parent.get().fitnessActivity);

            fitnessActivityBackupStatus.get(0).setMarkForDelete(1);
            fitnessDatabase.fitnessActivityBackupStatusDao().update(fitnessActivityBackupStatus.get(0));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            parent.get().onBackPressed();
        }
    }

}
