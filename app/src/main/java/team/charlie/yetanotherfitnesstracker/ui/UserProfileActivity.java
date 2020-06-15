package team.charlie.yetanotherfitnesstracker.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.FitnessUtility;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.UserProfile;
import team.charlie.yetanotherfitnesstracker.database.FitnessDatabase;
import team.charlie.yetanotherfitnesstracker.database.entities.WeightItem;
import team.charlie.yetanotherfitnesstracker.ui.weightTracking.WeightTrackingActivity;

public class UserProfileActivity extends AppCompatActivity implements ApiClientActivity {

    private static final String TAG = "UserProfileActivity";

    ImageView imageViewProfile;
    Spinner spinnerGender;
    EditText editTextName;
    EditText editTextAge;
    EditText editTextHeight;
    EditText editTextWeight;
    EditText editTextStrideLength;
    EditText editTextStepGoal;
    EditText editTextDistanceGoal;
    EditText editTextCaloriesGoal;
    EditText editTextActiveTimeGoal;
    EditText editTextSleepGoal;
    private SharedPreferences sharedPref;

    String gender, name;
    float height, weight, strideLength;
    int age, stepGoal, distanceGoal, caloriesGoal, activeTimeGoal, sleepGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        sharedPref = Objects.requireNonNull(this.getApplicationContext()).getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        spinnerGender = findViewById(R.id.activity_user_profile_gender);
        editTextName = findViewById(R.id.activity_user_profile_name);
        editTextAge = findViewById(R.id.activity_user_profile_age);
        editTextHeight = findViewById(R.id.activity_user_profile_height);
        editTextWeight = findViewById(R.id.activity_user_profile_weight);
        editTextStrideLength = findViewById(R.id.activity_user_profile_stride_length);
        editTextStepGoal = findViewById(R.id.activity_user_profile_step_goal);
        editTextDistanceGoal = findViewById(R.id.activity_user_profile_distance_goal);
        editTextCaloriesGoal = findViewById(R.id.activity_user_profile_calories_goal);
        editTextActiveTimeGoal = findViewById(R.id.activity_user_profile_active_time_goal);
        editTextSleepGoal = findViewById(R.id.activity_user_profile_sleep_goal);
        imageViewProfile = findViewById(R.id.activity_user_profile_image);

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editTextName.getText().toString().equals("")) {
                    imageViewProfile.setImageBitmap(generateCircleBitmap(getColor(R.color.app_main_secondary), 150, editTextName.getText().toString().substring(0, 1).toUpperCase(), getColor(R.color.app_main_primary)));
                }
            }
        });

        editTextHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editTextHeight.getText().toString().equals("")) {
                    strideLength = FitnessUtility.getStrideLengthInMeters(Float.parseFloat(editTextHeight.getText().toString()));
                    editTextStrideLength.setText(String.valueOf(strideLength));
                }
            }
        });

        Button saveButton = findViewById(R.id.activity_user_profile_button_save);
        saveButton.setOnClickListener(v -> {
            if (isInputValid()) {
                getUserProfileFromEditTexts();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(UserProfile.UPDATED, true);
                editor.putString(UserProfile.GENDER, gender);
                editor.putString(UserProfile.NAME, name);
                editor.putInt(UserProfile.AGE, age);
                editor.putFloat(UserProfile.HEIGHT, height);
                editor.putFloat(UserProfile.WEIGHT, weight);
                editor.putFloat(UserProfile.STRIDE_LENGTH, strideLength);
                editor.putInt(UserProfile.STEP_GOAL, stepGoal);
                editor.putInt(UserProfile.DISTANCE_GOAL, distanceGoal);
                editor.putInt(UserProfile.CALORIES_GOAL, caloriesGoal);
                editor.putInt(UserProfile.ACTIVE_TIME_GOAL, activeTimeGoal);
                editor.putInt(UserProfile.SLEEP_GOAL, sleepGoal);
                editor.apply();

                new SaveWeightItemAsyncTask(new WeakReference<>(UserProfileActivity.this.getApplicationContext()), weight).execute();

                Toast.makeText(UserProfileActivity.this, "Saved User Profile!!",
                        Toast.LENGTH_LONG).show();
            }
        });

        Button weightTrackButton = findViewById(R.id.activity_user_profile_weight_track);
        weightTrackButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, WeightTrackingActivity.class);
            this.startActivity(myIntent);
        });
    }

    @Override
    protected void onResume() {
        getUserProfileFromSharedPrefs();
        setupProfile();
        imageViewProfile.setImageBitmap(generateCircleBitmap(getColor(R.color.app_main_secondary), 150, name.substring(0, 1).toUpperCase(), getColor(R.color.app_main_primary)));
        super.onResume();
    }

    boolean isInputValid() {
        if (editTextName.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Display name cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextAge.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Age cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextHeight.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Height cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextWeight.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Weight cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextStrideLength.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Stride Length cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextStepGoal.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Step goal cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextDistanceGoal.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Distance goal cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextCaloriesGoal.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Calorie goal cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextActiveTimeGoal.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Active time goal cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (editTextSleepGoal.getText().toString().equals("")) {
            Toast.makeText(UserProfileActivity.this, "Sleep goal cannot be empty!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    void getUserProfileFromEditTexts() {
        age = Integer.parseInt(editTextAge.getText().toString());
        height = Float.parseFloat(editTextHeight.getText().toString());
        weight = Float.parseFloat(editTextWeight.getText().toString());
        strideLength = Float.parseFloat(editTextStrideLength.getText().toString());
        stepGoal = Integer.parseInt(editTextStepGoal.getText().toString());
        distanceGoal = Integer.parseInt(editTextDistanceGoal.getText().toString());
        caloriesGoal = Integer.parseInt(editTextCaloriesGoal.getText().toString());
        activeTimeGoal = Integer.parseInt(editTextActiveTimeGoal.getText().toString());
        sleepGoal = Integer.parseInt(editTextSleepGoal.getText().toString());
        name = editTextName.getText().toString().trim();
    }

    void getUserProfileFromSharedPrefs() {
        age = sharedPref.getInt(UserProfile.AGE, 18);
        height = sharedPref.getFloat(UserProfile.HEIGHT, 180);
        weight = sharedPref.getFloat(UserProfile.WEIGHT, 75);
        strideLength = sharedPref.getFloat(UserProfile.STRIDE_LENGTH, 80);
        stepGoal = sharedPref.getInt(UserProfile.STEP_GOAL, 0);
        distanceGoal = sharedPref.getInt(UserProfile.DISTANCE_GOAL, 0);
        caloriesGoal = sharedPref.getInt(UserProfile.CALORIES_GOAL, 0);
        activeTimeGoal = sharedPref.getInt(UserProfile.ACTIVE_TIME_GOAL, 0);
        sleepGoal = sharedPref.getInt(UserProfile.SLEEP_GOAL, 0);

        gender = sharedPref.getString(UserProfile.GENDER, "MALE");
        name = sharedPref.getString(UserProfile.NAME, "Username");
    }

    void setupProfile() {
        setupSpinner();
        editTextName.setText(name);
        editTextAge.setText(String.valueOf(age));
        editTextHeight.setText(String.valueOf(height));
        editTextWeight.setText(String.valueOf(weight));
        editTextStrideLength.setText(String.valueOf(strideLength));
        editTextStepGoal.setText(String.valueOf(stepGoal));
        editTextDistanceGoal.setText(String.valueOf(distanceGoal));
        editTextCaloriesGoal.setText(String.valueOf(caloriesGoal));
        editTextActiveTimeGoal.setText(String.valueOf(activeTimeGoal));
        editTextSleepGoal.setText(String.valueOf(sleepGoal));
    }

    void setupSpinner() {
        ArrayList<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("MALE");
        spinnerItems.add("FEMALE");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.activity_user_profile_spinner, spinnerItems);
        spinnerGender.setAdapter(arrayAdapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    gender = "MALE";
                } else {
                    gender = "FEMALE";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (gender.equals("MALE")) {
            spinnerGender.setSelection(0);
        } else {
            spinnerGender.setSelection(1);
        }

    }

    public static Bitmap generateCircleBitmap(int circleColor, float diameterDP, String text, int textColor) {
        //final int textColor = 0xFFFFFFFF;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float diameterPixels = diameterDP * (metrics.densityDpi / 160f);
        float radiusPixels = diameterPixels / 2;

        // Create the bitmap
        Bitmap output = Bitmap.createBitmap((int) diameterPixels, (int) diameterPixels,
                Bitmap.Config.ARGB_8888);

        // Create the canvas to draw on
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        // Draw the circle
        final Paint paintC = new Paint();
        paintC.setAntiAlias(true);
        paintC.setColor(circleColor);
        canvas.drawCircle(radiusPixels, radiusPixels, radiusPixels, paintC);

        // Draw the text
        if (text != null && text.length() > 0) {
            Log.d(TAG, "generateCircleBitmap: " + text);
            final Paint paintT = new Paint();
            paintT.setColor(textColor);
            paintT.setAntiAlias(true);
            paintT.setTextSize(radiusPixels * 2);
            final Rect textBounds = new Rect();
            paintT.getTextBounds(text, 0, text.length(), textBounds);
            canvas.drawText(text, radiusPixels - textBounds.exactCenterX(), radiusPixels - textBounds.exactCenterY(), paintT);
        }

        return output;
    }

    private static class SaveWeightItemAsyncTask extends AsyncTask<Integer, Void, Void> {

        WeakReference<Context> context;
        float weight;

        SaveWeightItemAsyncTask(WeakReference<Context> context, float weight) {
            this.context = context;
            this.weight = weight;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            FitnessDatabase fitnessDatabase = FitnessDatabase.getInstance(context.get());
            List<WeightItem> weightItems = fitnessDatabase.weightItemDao().getWeightItemByTimeStamp(FitnessUtility.getNMinusTodayStartInMilliseconds(0));
            Log.d(TAG, "doInBackground: " + weightItems.size());
            if (weightItems.size() > 0) {
                WeightItem weightItem = weightItems.get(0);
                weightItem.setWeight(weight);
                weightItem.setUpdated(1);
                fitnessDatabase.weightItemDao().update(weightItem);
            } else {
                WeightItem weightItem = new WeightItem(FitnessUtility.getNMinusTodayStartInMilliseconds(0), weight, 0, 0, 0, "");
                fitnessDatabase.weightItemDao().insert(weightItem);
            }
            return null;
        }
    }
}
