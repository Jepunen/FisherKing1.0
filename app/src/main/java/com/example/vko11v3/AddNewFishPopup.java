package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AddNewFishPopup extends AppCompatDialogFragment {

    EditText newFishName;
    EditText newFishWeight;
    EditText newFishLength;

    // Camera
    ImageView imageView;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File photoFile = null;
    String storageDir;
    @SuppressLint("DefaultLocale")
    String photoFileName = String.format("%d.jpg", System.currentTimeMillis());
    // Camera END

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_new_fish, null);

        newFishName   = (EditText) view.findViewById(R.id.newFishName);
        newFishWeight = (EditText) view.findViewById(R.id.newFishWeight);
        newFishLength = (EditText) view.findViewById(R.id.newFishLength);
        imageView   = (ImageView) view.findViewById(R.id.newFishImageView);

        AlertDialog dialog = (AlertDialog) getDialog();

        // Camera
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    // Image view on popup
                    Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                    imageView.setRotation(90);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        });
        // Camera END

        newFishName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                buttonCanBePressed(!TextUtils.isEmpty(newFishName.getText().toString()));
            }
        });

        builder.setView(view)
                .setTitle("Add new fish")
                .setPositiveButton("Add", (dialogInterface, i) -> {

                    String title = newFishName.getText().toString();
                    double weight = 0.0;
                    double length = 0.0;
                    if ( !TextUtils.isEmpty(newFishWeight.getText().toString()) ) {
                        weight = Double.parseDouble(newFishWeight.getText().toString());
                    }
                    if ( !TextUtils.isEmpty(newFishLength.getText().toString()) ) {
                        length = Double.parseDouble(newFishLength.getText().toString());
                    }
                    Date date = new Date();
                    Fish fish = new Fish(title, weight, length, photoFileName, "latitude", "longitude", date.getTime());

                    // TODO Add to list and Serialized file

                })
                .setNeutralButton("Add picture", null);



        return builder.create();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();

        // Disable the "Add" button if Title text field is empty
        buttonCanBePressed(!TextUtils.isEmpty(newFishName.getText().toString()));

        // Camera
        assert dialog != null;
        Button addPicture = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        addPicture.setOnClickListener(view1 -> {

            // Create a File reference for future access
            photoFile = getPhotoFileUri(photoFileName);

            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            Uri fileProvider = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                // Start the image capture intent to take photo
                activityResultLauncher.launch(intent);
            }
        });
        // Camera END

        if ( imageView.getDrawable() == null ) {
            addPicture.setText("Add picture");
        } else {
            addPicture.setText("Retake picture");
        }
    }

    // Disables / Enables the "Add" button
    public void buttonCanBePressed(boolean active) {
        AlertDialog dialog = (AlertDialog) getDialog();
        assert dialog != null;
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(active);
    }

    // Create a Directory for the photo to be saved at
    // + save the photo there when taken
    public File getPhotoFileUri(String fileName) {

        // Get safe storage directory for photos
        File mediaStorageDir = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FisherKing");
        storageDir = mediaStorageDir.getAbsolutePath();

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("Fisher King", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
    }
}
