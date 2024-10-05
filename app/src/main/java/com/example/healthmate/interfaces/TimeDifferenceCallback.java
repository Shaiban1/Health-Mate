package com.example.healthmate.interfaces;

import android.widget.TextView;

public interface TimeDifferenceCallback {
    void onCalculateTimeDifference(TextView timeRemainingTextView, String reminderTimeString);
}
