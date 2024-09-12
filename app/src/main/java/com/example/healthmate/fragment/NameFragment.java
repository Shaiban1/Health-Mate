    package com.example.healthmate.fragment;

    import android.os.Bundle;

    import androidx.fragment.app.Fragment;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;

    import com.example.healthmate.R;
    import com.example.healthmate.activities.SurveyScreen;
    import com.google.android.material.textfield.TextInputEditText;


    public class NameFragment extends Fragment {
        private EditText firstName,MiddleName,SecondName;
        String name;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_name, container, false);

            firstName = view.findViewById(R.id.FirstName);
            MiddleName = view.findViewById(R.id.MiddleName);
            SecondName = view.findViewById(R.id.LastName);


            return view;
        }

        public String getName() {
            String fN = firstName.getText().toString().trim();
            String mN = MiddleName.getText().toString().trim();
            String lN = SecondName.getText().toString().trim();

            // Combine the names while handling optional middle or last names
            StringBuilder fullName = new StringBuilder(fN);

            if (!mN.isEmpty()) {
                fullName.append(" ").append(mN);
            }

            if (!lN.isEmpty()) {
                fullName.append(" ").append(lN);
            }

            return fullName.toString();
        }

    }