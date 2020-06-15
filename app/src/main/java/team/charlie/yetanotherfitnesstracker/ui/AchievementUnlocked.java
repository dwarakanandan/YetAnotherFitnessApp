package team.charlie.yetanotherfitnesstracker.ui;

import java.util.ArrayList;
import java.util.List;

public class AchievementUnlocked {
    String type;
    String name;
    String template_description;
    List<Double> levels;
    int value;
    int level = 0;

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public AchievementUnlocked(){

    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate_description() {
        return template_description;
    }

    public void setTemplate_description(String template_description) {
        this.template_description = template_description;
    }

    public List<Double> getLevels() {
        return levels;
    }

    public void setLevels(List<Double> levels) {
        this.levels = levels;
    }

    public AchievementUnlocked(String type, String name, String template_description, List<Double> levels,int value) {
        this.type = type;
        this.name = name;
        this.template_description = template_description;
        this.levels = levels;
        this.value = value;
    }

    public AchievementUnlocked(String name) {
        this.name = name;
    }
}

