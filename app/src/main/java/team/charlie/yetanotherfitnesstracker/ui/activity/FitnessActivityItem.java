package team.charlie.yetanotherfitnesstracker.ui.activity;

public class FitnessActivityItem {

    private int mImageResource;
    private String mTitleText;
    private String mTimeText;
    private String mDistanceText;
    private String mDurationText;
    private String mCaloriesText;
    private String mStepCountText;

    public FitnessActivityItem(int mImageResource, String mTitleText, String mTimeText, String mDistanceText, String mDurationText, String mCaloriesText, String mStepCountText) {
        this.mImageResource = mImageResource;
        this.mTitleText = mTitleText;
        this.mTimeText = mTimeText;
        this.mDistanceText = mDistanceText;
        this.mDurationText = mDurationText;
        this.mCaloriesText = mCaloriesText;
        this.mStepCountText = mStepCountText;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public String getmTitleText() {
        return mTitleText;
    }

    public String getmTimeText() {
        return mTimeText;
    }

    public String getmDistanceText() {
        return mDistanceText;
    }

    public String getmDurationText() {
        return mDurationText;
    }

    public String getmCaloriesText() {
        return mCaloriesText;
    }

    public String getmStepCountText() {
        return mStepCountText;
    }
}
