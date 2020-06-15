package team.charlie.yetanotherfitnesstracker;

import android.content.SharedPreferences;

public class UserProfile {
    public static String UPDATED = "UPDATED";
    public static String NAME = "NAME";
    public static String EMAIL = "EMAIL";
    public static String AGE = "AGE";
    public static String GENDER = "GENDER";
    public static String HEIGHT = "HEIGHT";
    public static String WEIGHT = "WEIGHT";
    public static String STRIDE_LENGTH = "STRIDE_LENGTH";
    public static String STEP_GOAL = "STEP_GOAL";
    public static String DISTANCE_GOAL = "DISTANCE_GOAL";
    public static String CALORIES_GOAL = "CALORIES_GOAL";
    public static String ACTIVE_TIME_GOAL ="ACTIVE_TIME_GOAL";
    public static String SLEEP_GOAL = "SLEEP_GOAL";
    public static String CURRENT_LATITUDE = "CURRENT_LATITUDE";
    public static String CURRENT_LONGITUDE = "CURRENT_LONGITUDE";

    private String name;
    private int age;
    private String email;
    private String gender;
    private float height;
    private float strideLength;
    private int stepGoal;
    private int distanceGoal;
    private int caloriesGoal;
    private int activeTimeGoal;
    private int sleepGoal;

    public void loadUserProfileFromSharedPreferences(SharedPreferences sharedPref) {
        age = sharedPref.getInt(UserProfile.AGE, 18);
        height = sharedPref.getFloat(UserProfile.HEIGHT, 180);
        strideLength = sharedPref.getFloat(UserProfile.STRIDE_LENGTH, 80);
        stepGoal = sharedPref.getInt(UserProfile.STEP_GOAL, 0);
        distanceGoal = sharedPref.getInt(UserProfile.DISTANCE_GOAL, 0);
        caloriesGoal = sharedPref.getInt(UserProfile.CALORIES_GOAL, 0);
        activeTimeGoal = sharedPref.getInt(UserProfile.ACTIVE_TIME_GOAL, 0);
        sleepGoal = sharedPref.getInt(UserProfile.SLEEP_GOAL, 0);
        gender = sharedPref.getString(UserProfile.GENDER, "MALE");
        name = sharedPref.getString(UserProfile.NAME, "Username");
        email = sharedPref.getString(UserProfile.EMAIL, "example@example.com");
    }

    public void saveUserProfileToSharedPreferences(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UserProfile.GENDER, gender);
        editor.putString(UserProfile.NAME, name);
        editor.putInt(UserProfile.AGE, age);
        editor.putFloat(UserProfile.HEIGHT, height);
        editor.putFloat(UserProfile.STRIDE_LENGTH, strideLength);
        editor.putInt(UserProfile.STEP_GOAL, stepGoal);
        editor.putInt(UserProfile.DISTANCE_GOAL, distanceGoal);
        editor.putInt(UserProfile.CALORIES_GOAL, caloriesGoal);
        editor.putInt(UserProfile.ACTIVE_TIME_GOAL, activeTimeGoal);
        editor.putInt(UserProfile.SLEEP_GOAL, sleepGoal);
        editor.apply();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getStrideLength() {
        return strideLength;
    }

    public void setStrideLength(float strideLength) {
        this.strideLength = strideLength;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int getDistanceGoal() {
        return distanceGoal;
    }

    public void setDistanceGoal(int distanceGoal) {
        this.distanceGoal = distanceGoal;
    }

    public int getCaloriesGoal() {
        return caloriesGoal;
    }

    public void setCaloriesGoal(int caloriesGoal) {
        this.caloriesGoal = caloriesGoal;
    }

    public int getActiveTimeGoal() {
        return activeTimeGoal;
    }

    public void setActiveTimeGoal(int activeTimeGoal) {
        this.activeTimeGoal = activeTimeGoal;
    }

    public int getSleepGoal() {
        return sleepGoal;
    }

    public void setSleepGoal(int sleepGoal) {
        this.sleepGoal = sleepGoal;
    }
}
