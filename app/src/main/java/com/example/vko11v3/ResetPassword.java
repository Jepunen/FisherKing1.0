package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Properties;


public class ResetPassword extends Fragment {

    EditText resetEmail;
    EditText verificationCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resetEmail = view.findViewById(R.id.resetEmail);
        verificationCode = view.findViewById(R.id.verificationCode);

        Button sendResetEmail = view.findViewById(R.id.sendEmail);
        sendResetEmail.setOnClickListener(view1 -> {

            resetEmail.setVisibility(View.GONE);
            verificationCode.setVisibility(View.VISIBLE);


        });



    }
}