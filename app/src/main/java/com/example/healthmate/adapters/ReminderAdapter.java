package com.example.healthmate.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.R;
import com.example.healthmate.filesImp.ReminderViewModel;
import com.example.healthmate.interfaces.TimeDifferenceCallback;
import com.example.healthmate.models.Reminder;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReminderAdapter extends PagedListAdapter<Reminder, ReminderAdapter.ReminderViewHolder> {
    private ReminderViewModel reminderViewModel;
    private TimeDifferenceCallback timeDifferenceCallback;

    public ReminderAdapter(ReminderViewModel reminderViewModel, TimeDifferenceCallback timeDifferenceCallback) {
        super(DIFF_CALLBACK);
        this.reminderViewModel = reminderViewModel;
        this.timeDifferenceCallback = timeDifferenceCallback; // Accept callback
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = getItem(position);
        if (reminder != null) {
            holder.reminder_Name.setText(reminder.getName());
            holder.pillCount.setText("pills: " + reminder.getPillCount());
            holder.reminderTime.setText("Time: " + reminder.getTime());
            holder.reminderDay.setText("Repeat: " + reminder.getDay());

            // Use the callback to calculate time difference and update the UI
            timeDifferenceCallback.onCalculateTimeDifference(holder.timeRemaining, reminder.getTime());

            if (isReminderRunning(reminder)) {
                holder.timeRemaining.setText("Now Running");
                holder.timeRemaining.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorAccent));
            } else {
                holder.timeRemaining.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            }

            holder.deleteButton.setOnClickListener(view -> {
                // Remove reminder from the database
                reminderViewModel.delete(reminder);
            });
        } else {
            Log.e("ReminderAdapter", "reminder is null");
        }
    }

    private boolean isReminderRunning(Reminder reminder) {
        // Assuming the reminder time format is "hh:mm a"
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        LocalTime reminderTime = LocalTime.parse(reminder.getTime(), formatter);

        // Check if the current time matches the reminder time
        return currentTime.equals(reminderTime);
    }

    public static final DiffUtil.ItemCallback<Reminder> DIFF_CALLBACK = new DiffUtil.ItemCallback<Reminder>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return oldItem.equals(newItem);
        }
    };

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView reminder_Name, pillCount, reminderTime, reminderDay, timeRemaining;
        ImageButton deleteButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminder_Name = itemView.findViewById(R.id.reminderName);
            pillCount = itemView.findViewById(R.id.pillCount);
            reminderTime = itemView.findViewById(R.id.reminderTime);
            timeRemaining = itemView.findViewById(R.id.time_remaining);
            reminderDay = itemView.findViewById(R.id.reminderDay);
            deleteButton = itemView.findViewById(R.id.delete_reminder);
        }
    }
}
