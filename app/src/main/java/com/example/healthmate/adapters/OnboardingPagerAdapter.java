package com.example.healthmate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnboardingPagerAdapter extends RecyclerView.Adapter<OnboardingPagerAdapter.OnboardingViewHolder> {

    private final Context context;
    private final int[] layouts;

    public OnboardingPagerAdapter(Context context, int[] layouts) {
        this.context = context;
        this.layouts = layouts;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout based on position (viewType is used here)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layouts[viewType], parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        // No specific binding is necessary for static layouts
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        // The view type corresponds to the position, so each page uses a different layout
        return position;
    }

    // ViewHolder class
    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your view components if needed
        }
    }
}
