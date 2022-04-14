package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;
import java.util.Set;

public class LogInFragment extends Fragment {

    TextView message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DrawerLocker) requireActivity()).setDrawerLocked(true);

        // Get screen elements by ID
        message  = view. findViewById(R.id.loginMessage);
        EditText username = view.findViewById(R.id.loginUsername);
        EditText password = view.findViewById(R.id.loginPassword);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch rememberMe = view.findViewById(R.id.loginRememberMe); // TODO do this :)

        // Get screen elements by ID - END


        // Get buttons and add listeners
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view1 -> {

            String user = username.getText().toString();
            String pass = password.getText().toString();

            this.checkUser(user, pass);

        });

        // On button click, open the register fragment
        Button registerButton = view.findViewById(R.id.loginRegisterButton);
        registerButton.setOnClickListener(view12 -> {
            Fragment register = new RegisterFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.container_fragment, register ); // give your fragment container id in first parameter
            transaction.commit();
        });

        // Get buttons and add listeners - END

    }

    // Checks if the username and password combination exists
    // and if that is the case, redirects user to the apps home page
    // Return false in case of user - pass not matching / existing
    @SuppressLint("SetTextI18n")
    public void checkUser (String username, String password) {

        String savedPassword;

        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        savedPassword = sharedPref.getString(username, null);

        if (password.equals(savedPassword)) {
            message.setText("Successfully logged in");
            Fragment main = new MainFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.container_fragment, main ); // give your fragment container id in first parameter
            transaction.commit();
        } else {
            message.setText(R.string.wrong_credentials);
        }
    }
}
