package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.Objects;

public class LogInFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get screen elements by ID
        TextView message  = view. findViewById(R.id.loginMessage);
        EditText username = view.findViewById(R.id.loginUsername);
        EditText password = view.findViewById(R.id.loginPassword);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch rememberMe = view.findViewById(R.id.loginRememberMe);

        // Get screen elements by ID - END


        // Get buttons and add listeners
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view1 -> {
            if (!checkUser(username.getText().toString(), password.getText().toString())) {
                message.setText("No matching credentials found for " + username);
            }
        });

        Button registerButton = view.findViewById(R.id.loginRegister);
        registerButton.setOnClickListener(view12 -> {
            // TODO Add open register fragment
        });

        // Get buttons and add listeners - END


    }

    private boolean checkUser (String username, String password) {

        String savedPassword;

        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        savedPassword = sharedPref.getString(username, null);

        if (password.equals(savedPassword)) {
            // TODO Send user to main page

        }

        return false;
    }
}
