package com.example.healthmate.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.healthmate.R;
import com.example.healthmate.adapters.ReminderAdapter;
import com.example.healthmate.models.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MorningFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_morning, container, false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // Handle the case where user is null
            return view;
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(reminderList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab_morning = view.findViewById(R.id.fab_morning);
        fab_morning.setOnClickListener(view1 -> openReminderDialog());

        loadReminderFromFireBase();

        return view;
    }

    public void loadReminderFromFireBase() {
        DatabaseReference morning_Ref = FirebaseDatabase.getInstance().getReference("Reminders").child(userId);
        morning_Ref.orderByChild("time_of_day").equalTo("morning").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Reminder reminder = dataSnapshot.getValue(Reminder.class);
                    if (reminder != null) {
                        reminderList.add(reminder);
                        Log.d("FirebaseData", "Reminder added: " + reminder.getName());
                    } else {
                        Log.d("FirebaseData", "Reminder is null");
                    }
                }
                if (reminderList != null && !reminderList.isEmpty()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Log.d("ReminderList", "No reminders to display");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log the error to troubleshoot Firebase issues
                Log.e("MorningFragment", "Failed to load reminders", error.toException());
            }
        });
    }

    private void openReminderDialog() {
        ReminderDialogFragment dialogFragment = new ReminderDialogFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        dialogFragment.setArguments(args);
        if (getFragmentManager() != null) {
            dialogFragment.setTargetFragment(MorningFragment.this, 0);
            dialogFragment.show(getFragmentManager(), "ReminderDialogFragment");
        } else {
            Log.e("FragmentError", "FragmentManager is null");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // Reload data from Firebase after the dialog closes
            loadReminderFromFireBase();
        }
    }
}
