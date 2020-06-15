package team.charlie.yetanotherfitnesstracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import team.charlie.yetanotherfitnesstracker.api.LoginRequest;
import team.charlie.yetanotherfitnesstracker.api.RegisterRequest;

public class RegisterActivity extends AppCompatActivity implements ApiClientActivity {

    String emailString;
    String passwordString;
    EditText name;
    EditText email;
    EditText password;
    EditText confirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        Button button = findViewById(R.id.registration_activity_register);
        name = findViewById(R.id.registration_activity_name);
        email = findViewById(R.id.registration_activity_email);
        password = findViewById(R.id.registration_activity_password);
        confirmPassword = findViewById(R.id.registration_activity_confirm_password);

        button.setOnClickListener(v -> {
            if (isInputValid()) {
                emailString = email.getText().toString().trim().toLowerCase();
                passwordString = password.getText().toString();
                RegisterRequest registerRequest = new RegisterRequest(
                        RegisterActivity.this, RegisterActivity.this.getApplicationContext(), name.getText().toString().trim(),
                        emailString, passwordString);
                registerRequest.register();
            }
        });
    }

    private boolean isInputValid() {
        if (name.getText().toString().isEmpty()) {
            Toast.makeText(this,
                    "Name cannot be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (email.getText().toString().isEmpty()) {
            Toast.makeText(this,
                    "Enter a valid Email ID", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(this,
                    "Passwords don't match!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onRegisterSuccess() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UserProfile.NAME, name.getText().toString().trim());
        editor.putString(UserProfile.EMAIL, emailString);
        editor.putBoolean("FIRST_LOGIN_AFTER_REGISTER", true);
        editor.apply();
        LoginRequest loginRequest = new LoginRequest(this, this.getApplicationContext(),
                emailString, passwordString);
        loginRequest.login();
    }

    @Override
    public void onRegisterFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginSuccess() {
        Intent myIntent = new Intent(this, DashboardActivity.class);
        this.startActivity(myIntent);
    }

}
