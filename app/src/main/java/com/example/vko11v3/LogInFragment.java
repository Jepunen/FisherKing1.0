package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.biometric.BiometricPrompt;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;
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

        // Hides the navigation toolbar from the user
        ((MainInterface) requireActivity()).hideNavToolbar(true);


        // -- Get screen elements by ID --
        message  = view.findViewById(R.id.loginMessage);
        EditText username = view.findViewById(R.id.loginUsername);
        EditText password = view.findViewById(R.id.resetEmail);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch rememberMe = view.findViewById(R.id.loginRememberMe);

        // -- Get screen elements by ID - END --


        // -- Get buttons and add listeners --
        Button loginButton = view.findViewById(R.id.sendEmail);
        loginButton.setOnClickListener(view1 -> {

            String user = username.getText().toString();
            String pass = password.getText().toString();

            try {
                this.checkUser(user, pass, rememberMe.isChecked());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        });

        // On button click, open the register fragment
        Button registerButton = view.findViewById(R.id.loginRegisterButton);
        registerButton.setOnClickListener(view12 -> {
            Fragment register = new RegisterFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.container_fragment, register ); // give your fragment container id in first parameter
            transaction.commit();
        });

        // Textview "forgot password" click, open reset password fragment
        TextView resetPassword = view.findViewById(R.id.forgotPassword);
        resetPassword.setOnClickListener(view1 -> {
            Fragment reset = new ResetPassword();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.container_fragment, reset);
            transaction.commit();
        });


        // Finger print login START
        Executor executor;
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;

        executor = ContextCompat.getMainExecutor(requireContext());
        biometricPrompt = new BiometricPrompt(LogInFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(requireActivity(), "Authentication error", Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("CommitPrefEdits")
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

                if (sharedPref.getString("biometric_user", null) == null) {
                    ((MainInterface) requireActivity()).showAddUsernamePopup(view);
                }
                sharedPref.edit().putString("current_user", sharedPref.getString("biometric_user", null)).apply();
                sharedPref.edit().putString("logged_in_as", sharedPref.getString("biometric_user", null)).apply();
                ((MainInterface) requireActivity()).setNavHeaderText();
                goToHomeFragment();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(requireActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }) {
        };

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        Button fingerprint = view.findViewById(R.id.fingerPrintLogin);
        fingerprint.setOnClickListener(view1 -> {

            biometricPrompt.authenticate(promptInfo);

        });
        // Finger print login - END


        // -- Get buttons and add listeners - END --

    }

    // Checks if the username and password combination exists
    // and if that is the case, redirects user to the apps home page
    @SuppressLint("SetTextI18n")
    public void checkUser (String username, String password, boolean stayLoggedIn) throws NoSuchAlgorithmException {

        String savedPassword;

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        savedPassword = sharedPref.getString(username, null);

        // Hash-512 + salt password
        byte[] salt = PasswordHashSalt.getSalt();
        String hashSaltPassword = PasswordHashSalt.getSecurePassword(password, salt);

        if (hashSaltPassword.equals(savedPassword)) {

            message.setText("Successfully logged in");

            // Checks if logged in is enabled.
            if ( stayLoggedIn ) {
                // Adds user as logged in
                sharedPref.edit().putString("logged_in_as", username).apply();
            } else {
                // Either does nothing, or removes user from being logged in
                sharedPref.edit().putString("logged_in_as", null).apply();
            }
            sharedPref.edit().putString("current_user", username).apply();
            ((MainInterface) requireActivity()).setNavHeaderText();

            // Redirects user to the home page since login was successful
            goToHomeFragment();

        } else {
            message.setText(R.string.wrong_credentials);
        }
    }

    // Redirects user to home fragment
    private void goToHomeFragment() {
        Fragment main = new MainFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container_fragment, main );
        transaction.commit();
    }

}
