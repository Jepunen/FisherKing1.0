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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

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

        // Hides the navigation toolbar from the user
        ((MainInterface) requireActivity()).hideNavToolbar(true);

        // -- Get screen elements by ID --
        TextView message  = view.findViewById(R.id.registerMessage);
        EditText username = view.findViewById(R.id.registerUsername);
        EditText password = view.findViewById(R.id.registerPassword);
        EditText email    = view.findViewById(R.id.registerEmail);
        // -- Get screen elements by ID - END --


        // -- Get buttons and add listeners --
        Button register = view.findViewById(R.id.registerButton);
        register.setOnClickListener(view1 -> {

            String user = username.getText().toString();
            String pass = password.getText().toString();
            String emal = email.getText().toString();

            // Check that none of the input fields are empty
            if ( TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(emal) ) {
                message.setText(R.string.empty_field);

            } else {
                // Create a user with it's username as key and password as the value.
                // Create another key as username:email with email as its value
                SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                // Checks if user with the name already exists
                // if   = user doesn't exist -> create user
                // else = user exists -> prompt user of existing account
                if ( !sharedPref.contains(user) ) {
                    editor.putString(user, pass);
                    editor.putString(user + ":email", emal);
                    editor.apply();
                    Snackbar snackMessage = Snackbar.make(requireView(), "Account created, try logging in", BaseTransientBottomBar.LENGTH_LONG);
                    snackMessage.show();

                    goToLoginPage();
                } else {
                    Snackbar snackMessage = Snackbar.make(requireView(), "An account with this username already exists", BaseTransientBottomBar.LENGTH_LONG);
                    snackMessage.show();
                }
            }
        });
        // "Go back" button on top left of register page
        ImageButton goBack = view.findViewById(R.id.registerGoBack);
        goBack.setOnClickListener(view12 -> goToLoginPage());

        Button wipe = view.findViewById(R.id.registerWipeData);
        wipe.setOnClickListener(view1 -> {

            SharedPreferences sharedPrefDefault = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences sharedPref = getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

            sharedPref.edit().clear().apply();
            sharedPrefDefault.edit().clear().apply();
        });

        // -- Get buttons and add listeners - END --
    }

    // Goes to the login fragment
    private void goToLoginPage () {
        Fragment login = new LogInFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container_fragment, login );
        transaction.commit();
    }
}
