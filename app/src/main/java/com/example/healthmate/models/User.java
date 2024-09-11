package com.example.healthmate.models;

public class User {

    private String name;
    private String ageGroup;
    private String bloodGroup;
    private String lifeStyle;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    // Parameterized constructor
    public User(String name, String ageGroup, String bloodGroup, String lifeStyle) {
        this.name = name;
        this.ageGroup = ageGroup;
        this.bloodGroup = bloodGroup;
        this.lifeStyle = lifeStyle;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for ageGroup
    public String getAgeGroup() {
        return ageGroup;
    }

    // Setter for ageGroup
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    // Getter for bloodGroup
    public String getBloodGroup() {
        return bloodGroup;
    }

    // Setter for bloodGroup
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    // Getter for lifeStyle
    public String getLifeStyle() {
        return lifeStyle;
    }

    // Setter for lifeStyle
    public void setLifeStyle(String lifeStyle) {
        this.lifeStyle = lifeStyle;
    }
}
