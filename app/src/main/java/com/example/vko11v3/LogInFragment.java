package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.biometric.BiometricPrompt;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

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

        // Hides the navigation toolbar from the user (since it's login page)
        ((MainInterface) requireActivity()).hideNavToolbar(true);

        // -- Get screen elements by ID --
        message  = view.findViewById(R.id.loginMessage);
        EditText username = view.findViewById(R.id.loginUsername);
        EditText password = view.findViewById(R.id.passwordEditText);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch rememberMe = view.findViewById(R.id.loginRememberMe);
        // -- Get screen elements by ID - END --

        // -- Get buttons and add listeners --
        // "Login" button
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view1 -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();
            try {
                // Valid login = sends user to Home page
                // Invalid login = tells the user mismatching credentials
                this.checkUser(user, pass, rememberMe.isChecked());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });

        // "Register" button, redirects to register page
        TextView registerButton = view.findViewById(R.id.loginRegisterButton);
        registerButton.setOnClickListener(view12 -> ((MainInterface)requireActivity()).goToFragment(new RegisterFragment(), true));

        // -- Biometrics login - START --
        // Initialize executor and prompt for biometrics
        Executor executor;
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;
        executor = ContextCompat.getMainExecutor(requireContext());

        // Create biometric prompt
        biometricPrompt = new BiometricPrompt(LogInFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override // Auth. cancelled or device doesn't have biometric login
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(requireActivity(), "Authentication error", Toast.LENGTH_SHORT).show();
            }

            @Override // Auth. successful
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                // Get user data file
                SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

                // First time login with biometrics, so create username
                if (sharedPref.getString("biometric_user", null) == null) {
                    ((MainInterface) requireActivity()).showAddUsernamePopup(view);
                }
                // Set user as logged in
                String user = sharedPref.getString("biometric_user", "null");
                sharedPref.edit().putString("current_user", user).apply();
                sharedPref.edit().putString("logged_in_as", user).apply();
                // Update nav header
                ((MainInterface) requireActivity()).setNavHeaderText();
                ((MainInterface)requireActivity()).goToFragment(new MainFragment(), true);
            }

            @Override // Necessary but never got this message, so not sure when executed
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(requireActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Build prompt
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // "Login with fingerprint" button
        ImageView fingerprint = view.findViewById(R.id.fingerPrintLogin);
        fingerprint.setOnClickListener(view1 -> {
            // Show biometric prompt
            biometricPrompt.authenticate(promptInfo);
        });
        // -- Biometric login - END --
        // -- Get buttons and add listeners - END --
    }

    // Checks if the username and password combination exists
    // and if that is the case, redirects user to the apps home page
    @SuppressLint("SetTextI18n")
    public void checkUser (String username, String password, boolean stayLoggedIn) throws NoSuchAlgorithmException {

        String savedPassword;

        // Fetch user data file
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        savedPassword = sharedPref.getString(username, null);

        // Hash-512 + salt password
        byte[] salt = PasswordHashSalt.getSalt();
        String hashSaltPassword = PasswordHashSalt.getSecurePassword(password, salt);

        if (hashSaltPassword.equals(savedPassword)) {
            message.setText("Successfully logged in");

            // Checks if stay logged in is enabled.
            if ( stayLoggedIn ) {
                // Adds user as logged in
                sharedPref.edit().putString("logged_in_as", username).apply();
            } else {
                // Either does nothing, or removes user from being logged in
                sharedPref.edit().putString("logged_in_as", null).apply();
            }
            // Adds user as the current user
            sharedPref.edit().putString("current_user", username).apply();
            // Updates the nav header text
            ((MainInterface) requireActivity()).setNavHeaderText();
            // Redirects user to the home page since login was successful
            ((MainInterface)requireActivity()).goToFragment(new MainFragment(), true);
        } else {
            message.setText(R.string.wrong_credentials);
        }
    }
}