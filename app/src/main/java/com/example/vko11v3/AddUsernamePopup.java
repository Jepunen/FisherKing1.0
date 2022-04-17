package com.example.vko11v3;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddUsernamePopup extends AppCompatDialogFragment {

    private EditText username;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_username_popup, null);

        username = (EditText) view.findViewById(R.id.usernamePopupEditText);

        AlertDialog dialog = (AlertDialog) getDialog();

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ( !TextUtils.isEmpty(username.getText().toString()) ) {
                    buttonCanBePressed(true);
                } else {
                    buttonCanBePressed(false);
                }
            }
        });

        builder.setView(view)
                .setTitle("Add username")
                .setPositiveButton("Add username", (dialogInterface, i) -> {

                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
                    String user = username.getText().toString();
                    sharedPref.edit().putString("biometric_user", user).apply();
                    sharedPref.edit().putString("current_user", user).apply();
                    ((MainInterface) requireActivity()).setNavHeaderText();

                });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        // disable positive button by default
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.setCanceledOnTouchOutside(false);

        buttonCanBePressed(false);
    }

    public void buttonCanBePressed(boolean active) {
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(active);
    }
}
