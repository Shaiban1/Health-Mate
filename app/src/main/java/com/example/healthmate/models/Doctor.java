package com.example.healthmate.models;

import java.io.Serializable;

public class Doctor implements Serializable {
    private String name;
    private String specialization;
    private int imageResource;

    public Doctor(String name, String specialization, int imageResource) {
        this.name = name;
        this.specialization = specialization;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public int getImageResource() {
        return imageResource;
    }
}
