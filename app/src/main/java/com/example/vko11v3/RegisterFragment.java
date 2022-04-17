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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                // Checks and tells user if their password is a strong password
                if (passwordChecker(pass, "^.{12,20}$")) {
                    message.setText("Password must be between 12-20 characters");
                } else if (passwordChecker(pass, ".*[a-z].*")) {
                    message.setText("Password must include at least one lower case letter");
                } else if (passwordChecker(pass, ".*[A-Z].*")) {
                    message.setText("Password must include at least one upper case letter");
                } else if (passwordChecker(pass, ".*[0-9].*")) {
                    message.setText("Password must include at least one number");
                } else if (passwordChecker(pass, ".*[!\"`'#%&,:;<>=@{}~\\$\\(\\)\\*\\+\\/\\\\\\?\\[\\]\\^\\|].*")) {
                    message.setText("Password must include a special character");
                } else {

                    // Create a user with it's username as key and password as the value.
                    // Create another key as username:email with email as its value
                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    // Checks if user with the name already exists
                    // if   = user doesn't exist -> create user
                    // else = user exists -> prompt user of existing account
                    if ( !sharedPref.contains(user) ) {

                        // Hash-512 + salt password
                        byte[] salt = PasswordHashSalt.getSalt();
                        String hashSaltPassword = PasswordHashSalt.getSecurePassword(pass, salt);

                        editor.putString(user, hashSaltPassword);
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
            }
        });
        // "Go back" button on top left of register page
        ImageButton goBack = view.findViewById(R.id.registerGoBack);
        goBack.setOnClickListener(view12 -> goToLoginPage());

        Button wipe = view.findViewById(R.id.registerWipeData);
        wipe.setOnClickListener(view1 -> {

            SharedPreferences sharedPrefDefault = requireActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

            sharedPref.edit().clear().apply();
            sharedPrefDefault.edit().clear().apply();
        });

        // -- Get buttons and add listeners - END --
    }

    public static boolean passwordChecker(String password,String regex)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return !matcher.matches();
    }

    // Goes to the login fragment
    private void goToLoginPage () {
        Fragment login = new LogInFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container_fragment, login );
        transaction.commit();
    }
}
