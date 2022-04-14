package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get screen elements by ID
        TextView message  = view.findViewById(R.id.registerMessage);
        EditText username = view.findViewById(R.id.registerUsername);
        EditText password = view.findViewById(R.id.registerPassword);
        EditText email    = view.findViewById(R.id.registerEmail);

        // Get screen elements by ID - END


        // Get buttons and add listeners
        Button register = view.findViewById(R.id.registerButton);
        register.setOnClickListener(view1 -> {
            // Check that none of the input fields is empty
            if ( !TextUtils.isEmpty(username.getText().toString())
                    || !TextUtils.isEmpty(password.getText().toString())
                    || !TextUtils.isEmpty(email.getText().toString())
            ) {
                // Create a user with it's username as a key,
                // password and email behind the key as a HashSet
                SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                Set<String> tempList = new HashSet<>();

                tempList.add(password.getText().toString());
                tempList.add(email.getText().toString());

                editor.putStringSet(username.getText().toString(), tempList).apply();

            } else {
                message.setText(R.string.empty_fields);
            }
        });

        // Get buttons and add listeners - END

    }
}
