package com.example.healthmate.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.models.Reminder;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;

    public ReminderAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.reminderName.setText(reminder.getName());
        holder.pillsCount.setText(String.valueOf(reminder.getPillsCount()));
        holder.timeOfDay.setText(reminder.getTimeOfDay());
        if (reminder.isDaily()) {
            holder.days.setText("Daily");
        } else {
            holder.days.setText(TextUtils.join(", ", reminder.getSelectedDays()));
        }
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView reminderName, pillsCount, timeOfDay, days;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminderName = itemView.findViewById(R.id.reminder_name);
            pillsCount = itemView.findViewById(R.id.pills_count);
            timeOfDay = itemView.findViewById(R.id.time_of_day);
            days = itemView.findViewById(R.id.days);
        }
    }
}
