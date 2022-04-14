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
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DrawerLocker) requireActivity()).setDrawerLocked(true);

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
            String user = username.getText().toString();
            String pass = password.getText().toString();
            String emal = email.getText().toString();

            if ( TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(emal) ) {
                message.setText(R.string.empty_field);
            } else {
                // Create a user with it's username and password as a match
                // Create another match with the username containing it's email
                SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString(user, pass);
                editor.putString(user + ":email", emal);

                editor.apply();

                message.setText("Account successfully created");
            }
        });

        ImageButton goBack = view.findViewById(R.id.registerGoBack);
        goBack.setOnClickListener(view12 -> {
            Fragment login = new LogInFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.container_fragment, login ); // give your fragment container id in first parameter
            transaction.commit();
        });

        // Get buttons and add listeners - END

    }
}
