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
import com.example.healthmate.models.Reminder;

import java.util.List;


public class ReminderAdapter extends PagedListAdapter<Reminder, ReminderAdapter.ReminderViewHolder> {
    private ReminderViewModel reminderViewModel;

    public ReminderAdapter(ReminderViewModel reminderViewModel) {
        super(DIFF_CALLBACK);
        this.reminderViewModel = reminderViewModel;
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
            if (holder.reminder_Name != null) { // check if reminderNameEditText is null before accessing it
                holder.reminder_Name.setText(reminder.getName());
            } else {
                Log.e("ReminderAdapter", "reminderNameEditText is null");
            }

            if (holder.pillCount != null) { // check if pillCountEditText is null before accessing it
                holder.pillCount.setText("pills: " + reminder.getPillCount());
            } else {
                Log.e("ReminderAdapter", "pillCountEditText is null");
            }

            if (holder.reminderTime != null) { // check if reminderTimeEditText is null before accessing it
                holder.reminderTime.setText("Time: " + reminder.getTime());
            } else {
                Log.e("ReminderAdapter", "reminderTimeEditText is null");
            }

            if (holder.reminderDay != null) { // check if reminderDayEditText is null before accessing it
                holder.reminderDay.setText("Repeat: " + reminder.getDay());
            } else {
                Log.e("ReminderAdapter", "reminderDayEditText is null");
            }



        } else {
            Log.e("ReminderAdapter", "reminder is null");
        }



        holder.deleteButton.setOnClickListener(view -> {
            // Remove reminder from the database
            reminderViewModel.delete(reminder);
        });
    }

    private String getRingtoneName(int ringtoneResId) {
        if (ringtoneResId == R.raw.ringtone_one) {
            return "Ringtone 1";
        } else if (ringtoneResId == R.raw.ringtone_two) {
            return "Ringtone 2";
        } else if (ringtoneResId == R.raw.ringtone_three) {
            return "Ringtone 3";
        } else if (ringtoneResId == R.raw.ringtone_four) {
            return "Ringtone 4";
        } else if (ringtoneResId == R.raw.ringtone_five) {
            return "Ringtone 5";
        } else {
            return "Default Ringtone";
        }
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
        TextView reminder_Name, pillCount, reminderTime, reminderDay, ringtoneName;
        ImageButton deleteButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            reminder_Name = itemView.findViewById(R.id.reminderName);
            pillCount = itemView.findViewById(R.id.pillCount);
            reminderTime = itemView.findViewById(R.id.reminderTime);
            reminderDay = itemView.findViewById(R.id.reminderDay);
            deleteButton = itemView.findViewById(R.id.delete_reminder);

        }
    }
}
