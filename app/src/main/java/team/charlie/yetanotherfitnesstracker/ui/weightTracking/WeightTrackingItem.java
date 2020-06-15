package team.charlie.yetanotherfitnesstracker.ui.weightTracking;

public class WeightTrackingItem {

    private String dateText;
    private String weightText;
    private String bmiText;

    public WeightTrackingItem(String dateText, String weightText, String bmiText) {
        this.dateText = dateText;
        this.weightText = weightText;
        this.bmiText = bmiText;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }

    public String getWeightText() {
        return weightText;
    }

    public void setWeightText(String weightText) {
        this.weightText = weightText;
    }

    public String getBmiText() {
        return bmiText;
    }

    public void setBmiText(String bmiText) {
        this.bmiText = bmiText;
    }
}
