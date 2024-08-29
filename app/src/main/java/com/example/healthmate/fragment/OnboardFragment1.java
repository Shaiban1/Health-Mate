package com.example.healthmate.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.airbnb.lottie.LottieAnimationView;
import com.example.healthmate.R;

public class OnboardFragment1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_onboard1, container, false);

        LottieAnimationView animationView = view.findViewById(R.id.lottieAnimationView1);


        animationView.setAnimation(R.raw.onboard_ai);
        animationView.playAnimation();

        return view;
    }


}
