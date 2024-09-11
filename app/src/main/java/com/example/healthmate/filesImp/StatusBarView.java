package com.example.healthmate.filesImp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.healthmate.R;



public class StatusBarView extends LinearLayout {

    private View indicatorStepOne;
    private View indicatorStepTwo;
    private View indicatorStepThree;
    private View indicatorStepFour;

    public StatusBarView(Context context) {
        super(context);
        init(context);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_status_bar, this, true);

        indicatorStepOne = findViewById(R.id.step_one);
        indicatorStepTwo = findViewById(R.id.step_two);
        indicatorStepThree = findViewById(R.id.step_three);
        indicatorStepFour = findViewById(R.id.step_four);

    }

    public void setCurrentStep(int currentStep) {
        resetIndicators();

        switch (currentStep) {
            case 1:
                indicatorStepOne.setBackgroundColor(getResources().getColor(R.color.logo_color));
                break;
            case 2:
                indicatorStepOne.setBackgroundColor(getResources().getColor(R.color.logo_color));
                indicatorStepTwo.setBackgroundColor(getResources().getColor(R.color.logo_color));
                break;
            case 3:
                indicatorStepOne.setBackgroundColor(getResources().getColor(R.color.logo_color));
                indicatorStepTwo.setBackgroundColor(getResources().getColor(R.color.logo_color));
                indicatorStepThree.setBackgroundColor(getResources().getColor(R.color.logo_color));
                break;

            case 4:
                indicatorStepOne.setBackgroundColor(getResources().getColor(R.color.logo_color));
                indicatorStepTwo.setBackgroundColor(getResources().getColor(R.color.logo_color));
                indicatorStepThree.setBackgroundColor(getResources().getColor(R.color.logo_color));
                indicatorStepFour.setBackgroundColor(getResources().getColor(R.color.logo_color));

            default:
                break;
        }
    }

    private void resetIndicators() {
        indicatorStepOne.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        indicatorStepTwo.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        indicatorStepThree.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        indicatorStepFour.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    }
}
