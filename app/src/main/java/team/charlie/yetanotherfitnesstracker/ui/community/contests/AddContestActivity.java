package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.api.CreateContestRequest;

public class AddContestActivity extends AppCompatActivity implements ApiClientActivity {

    private static final String TAG = "AddContestActivity";

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    RadioGroup radioGroupContestType, radioGroupGoalType;
    RadioButton radioButtonContestType, radioButtonGoalType;
    EditText editTextName, editTextDescription, editTextGoalValue;
    EditText editTextStartDate, editTextStartTime, editTextEndDate, editTextEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contest);

        startCalendar.set(Calendar.MILLISECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);
        startCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.SECOND, 0);

        radioGroupContestType = findViewById(R.id.activity_add_contest_radio_group_contest_type);
        radioGroupGoalType = findViewById(R.id.activity_add_contest_radio_group_goal_type);
        radioButtonContestType = findViewById(R.id.activity_add_contest_radio_button_public);
        radioButtonGoalType = findViewById(R.id.activity_add_contest_radio_button_steps);

        editTextName = findViewById(R.id.activity_add_contest_name);
        editTextDescription = findViewById(R.id.activity_add_contest_description);
        editTextGoalValue = findViewById(R.id.activity_add_contest_goal_value);

        editTextStartDate = findViewById(R.id.activity_add_contest_start_date);
        editTextStartTime = findViewById(R.id.activity_add_contest_start_time);
        editTextEndDate = findViewById(R.id.activity_add_contest_end_date);
        editTextEndTime = findViewById(R.id.activity_add_contest_end_time);

        setupDateTimePicker();

        Button saveButton = findViewById(R.id.activity_add_contest_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contestDetailsValid()) {
                    Log.d(TAG, "onClick: Contest details valid");
                    ContestItem contestItem = new ContestItem();
                    contestItem.setName(editTextName.getText().toString());
                    contestItem.setDescription(editTextDescription.getText().toString());
                    contestItem.setStartDate(startCalendar.getTimeInMillis());
                    contestItem.setEndDate(endCalendar.getTimeInMillis());
                    contestItem.setType(radioButtonContestType.getText().toString());
                    contestItem.setGoalType(radioButtonGoalType.getText().toString());
                    contestItem.setGoalValue(Double.parseDouble(editTextGoalValue.getText().toString()));

                    CreateContestRequest createContestRequest = new CreateContestRequest(AddContestActivity.this, AddContestActivity.this.getApplicationContext());
                    createContestRequest.createContest(contestItem);
                }
            }
        });
    }

    @Override
    public void onCreateContestSuccess() {
        Toast.makeText(this.getApplicationContext(), "Successfully added Contest!!",
                Toast.LENGTH_LONG).show();
        this.onBackPressed();
    }

    boolean contestDetailsValid() {
        if (startCalendar.getTimeInMillis() >= endCalendar.getTimeInMillis()) {
            Log.d(TAG, "onClick: startCalendar greater than endCalendar");
            Toast.makeText(this.getApplicationContext(), "Contest cannot end be before it starts!!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextName.getText().toString().equals("")) {
            Log.d(TAG, "onClick: Name cannot be empty");
            Toast.makeText(this.getApplicationContext(), "Enter a valid Name!!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextDescription.getText().toString().equals("")) {
            Log.d(TAG, "onClick: Description cannot be empty");
            Toast.makeText(this.getApplicationContext(), "Enter a valid Name!!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextGoalValue.getText().toString().equals("")) {
            Log.d(TAG, "onClick: Goal cannot be empty");
            Toast.makeText(this.getApplicationContext(), "Enter a valid Goal Value!!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (Double.parseDouble(editTextGoalValue.getText().toString()) == 0) {
            Log.d(TAG, "onClick: Goal cannot be 0");
            Toast.makeText(this.getApplicationContext(), "Enter a valid Goal Value!!",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void setContestType(View v) {
        int radioId = radioGroupContestType.getCheckedRadioButtonId();
        radioButtonContestType = findViewById(radioId);
    }

    public void setGoalType(View v) {
        int radioId = radioGroupGoalType.getCheckedRadioButtonId();
        radioButtonGoalType = findViewById(radioId);
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
                DialogFragment newFragment = new DatePickerFragment(startCalendar, editTextStartDate, AddContestActivity.this);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        editTextStartTime.setCursorVisible(false);
        editTextStartTime.setFocusable(false);
        editTextStartTime.setText(timeFormat.format(startCalendar.getTime()));
        editTextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(startCalendar, editTextStartTime, AddContestActivity.this);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        editTextEndDate.setCursorVisible(false);
        editTextEndDate.setFocusable(false);
        editTextEndDate.setText(dateFormat.format(endCalendar.getTime()));
        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(endCalendar, editTextEndDate, AddContestActivity.this);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        editTextEndTime.setCursorVisible(false);
        editTextEndTime.setFocusable(false);
        editTextEndTime.setText(timeFormat.format(endCalendar.getTime()));
        editTextEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(endCalendar, editTextEndTime, AddContestActivity.this);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private Calendar date;
        private EditText editText;
        private AddContestActivity parent;

        DatePickerFragment(Calendar date, EditText editText, AddContestActivity parent) {
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
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private Calendar date;
        private EditText editTextTime;
        private AddContestActivity parent;

        TimePickerFragment(Calendar date, EditText editTextTime, AddContestActivity parent) {
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
        }
    }
}
