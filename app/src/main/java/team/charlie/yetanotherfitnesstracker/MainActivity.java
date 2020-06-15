package team.charlie.yetanotherfitnesstracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import team.charlie.yetanotherfitnesstracker.api.ApiBase;
import team.charlie.yetanotherfitnesstracker.api.LoginRequest;

public class MainActivity extends AppCompatActivity implements ApiClientActivity {
    private static final String TAG = "MainActivity";
    EditText email;
    EditText password;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        checkPermissions();

        setupProfileGoals();

        if (sharedPref.contains(ApiBase.SET_COOKIE_CONNECT_SID) && sharedPref.contains(ApiBase.SET_COOKIE_REMEMBER_ME)) {
            transitionToDashboardActivity();
            return;
        }

        init();
    }

    private void setupProfileGoals() {
        if (!sharedPref.contains(UserProfile.STEP_GOAL)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(UserProfile.AGE, 18);
            editor.putFloat(UserProfile.HEIGHT, 180);
            editor.putFloat(UserProfile.WEIGHT, 75);
            editor.putFloat(UserProfile.STRIDE_LENGTH, FitnessUtility.getStrideLengthInMeters(80));
            editor.putString(UserProfile.GENDER, "MALE");
            editor.putInt(UserProfile.STEP_GOAL, 10000);
            editor.putInt(UserProfile.DISTANCE_GOAL, 5000);
            editor.putInt(UserProfile.CALORIES_GOAL, 1200);
            editor.putInt(UserProfile.ACTIVE_TIME_GOAL, 100);
            editor.putInt(UserProfile.SLEEP_GOAL, 7);
            editor.apply();
        }
    }


    private void checkPermissions() {

        int PERMISSION_GRANT = 0;

        PackageManager pm = getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            Log.e(TAG, "checkPermissions : FEATURE_SENSOR_STEP_COUNTER not supported");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
            String[] permissions;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions = new String[]{Manifest.permission.ACTIVITY_RECOGNITION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION};
            } else {
                permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};
            }
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_GRANT);
        }
    }

    void init() {

        setContentView(R.layout.login_activity);
        Button loginButton = findViewById(R.id.login_button_login);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);

        if (sharedPref.contains(UserProfile.EMAIL)) {
            email.setText(sharedPref.getString(UserProfile.EMAIL, ""));
        }

        loginButton.setOnClickListener(v -> {
            if (isInputValid()) {
                String emailString = email.getText().toString().trim().toLowerCase();
                String passwordString = password.getText().toString();
                LoginRequest loginRequest = new LoginRequest(MainActivity.this, MainActivity.this.getApplicationContext(),
                        emailString, passwordString);
                loginRequest.login();
            }
        });

        Button registerButton = findViewById(R.id.login_button_register);
        registerButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, RegisterActivity.class);
            MainActivity.this.startActivity(myIntent);
        });
    }

    private boolean isInputValid() {
        if (email.getText().toString().trim().isEmpty()) {
            Toast.makeText(this,
                    "Email cannot be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            Toast.makeText(this,
                    "Password cannot be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(this,
                "Login successful!", Toast.LENGTH_SHORT).show();
        Switch rememberSwitch = findViewById(R.id.login_remember_me_switch);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (rememberSwitch.isChecked()) {
            editor.putString(UserProfile.EMAIL, email.getText().toString().trim().toLowerCase());
        } else {
            editor.remove(UserProfile.EMAIL);
        }

        editor.putBoolean("ACTIVITY_SYNC_AFTER_FIRST_LOGIN", true);
        editor.putBoolean("PROFILE_SYNC_AFTER_FIRST_LOGIN", true);
        editor.apply();

        transitionToDashboardActivity();
    }

    private void transitionToDashboardActivity() {
        Intent myIntent = new Intent(this, DashboardActivity.class);
        this.startActivity(myIntent);
    }

    @Override
    public void onLoginFailure() {
        Toast.makeText(this,
                "Login failed!", Toast.LENGTH_LONG).show();
        init();
    }
}
