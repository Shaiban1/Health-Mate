package com.example.healthmate.models;

import java.util.List;

public class Reminder {

    private String reminderId;
    private String name;
    private int pillsCount;
    private boolean isDaily;
    private List<String> selectedDays;
    private String timeOfDay;

    public Reminder() {

    }

    public Reminder(String reminderId, String name, int pillsCount, boolean isDaily, List<String> selectedDays, String timeOfDay) {
        this.reminderId = reminderId;
        this.name = name;
        this.pillsCount = pillsCount;
        this.isDaily = isDaily;
        this.selectedDays = selectedDays;
        this.timeOfDay = timeOfDay;
    }

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPillsCount() {
        return pillsCount;
    }

    public void setPillsCount(int pillsCount) {
        this.pillsCount = pillsCount;
    }

    public boolean isDaily() {
        return isDaily;
    }

    public void setDaily(boolean daily) {
        isDaily = daily;
    }

    public List<String> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<String> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
}
