package com.example.healthmate.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.example.healthmate.R;


public class OnBoardFragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_on_board2, container, false);

        LottieAnimationView animationView = view.findViewById(R.id.lottieAnimationView2);

        animationView.setAnimation(R.raw.onbaord2);
        animationView.playAnimation();
        return view;
    }
}