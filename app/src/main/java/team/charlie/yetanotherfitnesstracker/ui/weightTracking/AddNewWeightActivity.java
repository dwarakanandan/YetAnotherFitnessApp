package team.charlie.yetanotherfitnesstracker.ui.weightTracking;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.UserProfile;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.WeightItem;

public class AddNewWeightActivity extends FragmentActivity {

    private static final String TAG = "AddNewWeightActivity";

    Calendar startCalendar;
    float weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Log weight...");
        setContentView(R.layout.activity_add_new_weight);

        startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        java.text.DateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault());

        EditText editTextWeight = findViewById(R.id.activity_add_new_weight_weight);

        EditText editTextStartDate = findViewById(R.id.activity_add_new_weight_date);
        editTextStartDate.setCursorVisible(false);
        editTextStartDate.setFocusable(false);
        editTextStartDate.setText(dateFormat.format(startCalendar.getTime()));
        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddNewWeightActivity.DatePickerFragment(startCalendar, editTextStartDate);
                newFragment.show(AddNewWeightActivity.this.getSupportFragmentManager(), "datePicker");
            }
        });

        Button saveButton = findViewById(R.id.activity_add_new_weight_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = Float.parseFloat(editTextWeight.getText().toString());
                new AddWeightItemAsyncTask(new WeakReference<>(AddNewWeightActivity.this)).execute();
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private Calendar date;
        private EditText editText;

        DatePickerFragment(Calendar date, EditText editText) {
            this.date = date;
            this.editText = editText;
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
        }
    }

    private static class AddWeightItemAsyncTask extends AsyncTask<Integer, Void, Void> {

        private WeakReference<AddNewWeightActivity> activity;
        private boolean updated = false;

        public AddWeightItemAsyncTask(WeakReference<AddNewWeightActivity> activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(activity.get().getApplicationContext());
            List<WeightItem> weightItems = fitnessDatabase.weightItemDao().getWeightItemByTimeStamp(activity.get().startCalendar.getTimeInMillis());
            if (weightItems.size() > 0) {
                WeightItem weightItem = weightItems.get(0);
                weightItem.setWeight(activity.get().weight);
                weightItem.setUpdated(1);
                fitnessDatabase.weightItemDao().update(weightItem);
                this.updated = true;
            } else {
                WeightItem weightItem = new WeightItem(activity.get().startCalendar.getTimeInMillis(), activity.get().weight, 0, 0, 0,"");
                fitnessDatabase.weightItemDao().insert(weightItem);
            }

            weightItems = fitnessDatabase.weightItemDao().getLatestWeightItem();
            SharedPreferences sharedPref = activity.get().getSharedPreferences(
                    activity.get().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            sharedPref.edit().putFloat(UserProfile.WEIGHT, weightItems.get(0).getWeight()).apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (updated) {
                Toast.makeText(activity.get(), "Weight entry already exists for that day, Updating!",
                        Toast.LENGTH_SHORT).show();
            }
            this.activity.get().onBackPressed();
        }
    }
}
