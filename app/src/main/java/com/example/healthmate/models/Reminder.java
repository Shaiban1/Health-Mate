package com.example.healthmate.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int pillCount;
    private String time;
    private String day;
    private String ringtoneResId;
    private boolean taken;// Add this field for storing ringtone URI

    // Default constructor
    public Reminder() {
    }

    // Constructor to initialize all fields
    public Reminder(String name, int pillCount, String time, String day, String ringtoneResId) {
        this.name = name;
        this.pillCount = pillCount;
        this.time = time;
        this.day = day;
        this.ringtoneResId = ringtoneResId;
    }


    // Getter and Setter methods

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPillCount() {
        return pillCount;
    }

    public void setPillCount(int pillCount) {
        this.pillCount = pillCount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getRingtoneResId() {
        return ringtoneResId;
    }

    public void setRingtoneResId(String ringtoneResId) {
        this.ringtoneResId = ringtoneResId;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pillCount=" + pillCount +
                ", time='" + time + '\'' +
                ", day='" + day + '\'' +
                ", ringtoneResId='" + ringtoneResId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Reminder reminder = (Reminder) obj;
        return Objects.equals(getId(), reminder.getId()) &&
                Objects.equals(getPillCount(), reminder.getPillCount()) &&
                Objects.equals(getTime(), reminder.getTime()) &&
                Objects.equals(getDay(), reminder.getDay()) &&
                Objects.equals(getRingtoneResId(), reminder.getRingtoneResId())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),getName(),getPillCount(),getTime(),getDay(),getRingtoneResId());
    }
}
