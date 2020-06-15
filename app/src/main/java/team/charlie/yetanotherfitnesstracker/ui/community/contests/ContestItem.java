package team.charlie.yetanotherfitnesstracker.ui.community.contests;

import java.io.Serializable;
import java.util.List;

public class ContestItem implements Serializable {

    public static String TYPE_PUBLIC = "PUBLIC";
    public static String TYPE_PRIVATE = "PRIVATE";
    public static String GOAL_TYPE_STEPS = "STEPS";
    public static String GOAL_TYPE_DISTANCE = "DISTANCE";

    private String remoteId;
    private int typeImage;
    private int goalTypeImage;
    private String name;
    private String description;
    private long startDate;
    private long endDate;
    private String type;
    private String goalType;
    private double goalValue;
    private List<ContestItemParticipant> contestItemParticipants;
    private ContestItemParticipant creator;
    private boolean joined;
    private boolean isEnded;

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeImage() {
        return typeImage;
    }

    public List<ContestItemParticipant> getContestItemParticipants() {
        return contestItemParticipants;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }

    public void setContestItemParticipants(List<ContestItemParticipant> contestItemParticipants) {
        this.contestItemParticipants = contestItemParticipants;
    }

    public ContestItemParticipant getCreator() {
        return creator;
    }

    public void setCreator(ContestItemParticipant creator) {
        this.creator = creator;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public void setTypeImage(int typeImage) {
        this.typeImage = typeImage;
    }

    public int getGoalTypeImage() {
        return goalTypeImage;
    }

    public void setGoalTypeImage(int goalTypeImage) {
        this.goalTypeImage = goalTypeImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public double getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(double goalValue) {
        this.goalValue = goalValue;
    }
}
