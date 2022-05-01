package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ShowFishDetailsPopup extends AppCompatDialogFragment {

    EditText title, weight, length;
    ImageView image;
    ToggleButton toggle;
    Fish fish;
    int position;
    boolean isChecked;
    boolean deleteOldImage = false;
    String user;

    ArrayList<Fish> fList;

    // Camera
    ActivityResultLauncher<Intent> activityResultLauncherCamera;
    ActivityResultLauncher<Intent> activityResultLauncherGallery;
    File photoFile = null;
    String storageDir;
    @SuppressLint("DefaultLocale")
    String imageFileName = "null";
    String oldImageName = "null";
    // Camera END

    // Receive fish as object and it's position in ArrayList
    public ShowFishDetailsPopup(Fish fish, int pos) {
        this.fish = fish;
        this.position = pos;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_fish_details, null);

        // Get current user
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        user = sharedPref.getString("current_user", "");

        // Get image Dir
        File mediaStorageDir = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FisherKing");
        storageDir = mediaStorageDir.getAbsolutePath() + "/" + fish.getPicture();

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("Fisher King", "failed to create directory");
        }

        // Camera
        activityResultLauncherCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Image view on popup
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                image.setImageBitmap(bitmap);
                image.setRotation(90);
                image.setVisibility(View.VISIBLE);
                // New image taken so delete old
                deleteOldImage = true;
            }
        });
        // Camera END
        // Gallery
        activityResultLauncherGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {

                Uri imageUri = result.getData().getData();
                try {
                    imageFileName = String.format("%d-G.jpg", System.currentTimeMillis());
                    FileOutputStream out = new FileOutputStream(mediaStorageDir + "/" + imageFileName);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                    bitmap = RotateBitmap(bitmap, 90);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image.setImageURI(imageUri);
                image.setVisibility(View.VISIBLE);
                image.setRotation(0);
                deleteOldImage = true;
            }
        });

        // Get elements
        title  = view.findViewById(R.id.detailsFishName);
        weight = view.findViewById(R.id.detailsFishWeight);
        length = view.findViewById(R.id.detailsFishLength);
        toggle = view.findViewById(R.id.toggleButtonDetails);
        image  = view.findViewById(R.id.detailsFishImageView);

        // Set values
        title.setText(fish.getTitle());
        if (fish.getWeight() == 0.0) {
            weight.setText("");
            weight.setHint("0.0");
        } else {
            weight.setText(fish.getWeight().toString());
        }
        if (fish.getLength() == 0.0) {
            length.setText("");
            length.setHint("0.0");
        } else {
            length.setText(fish.getLength().toString());
        }
        // KG / GRAMS toggle listener
        toggle.setChecked(fish.inGrams);
        toggle.setOnCheckedChangeListener((compoundButton, b) -> isChecked = b);

        // Fish includes image -> set visible
        if (!fish.getPicture().equals("null")) {
            if (fish.getPicture().contains("-G")) {
                Bitmap bitmap = BitmapFactory.decodeFile(storageDir);
                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(storageDir);
                image.setImageBitmap(bitmap);
                image.setRotation(90);
                image.setVisibility(View.VISIBLE);
            }
        }

        builder.setView(view)
                .setTitle("Edit fish details")
                .setPositiveButton("Apply", (dialogInterface, i) -> {

                    // Get ArrayList from file
                    fList = getfList();
                    Fish temp = fList.get(position);

                    // If new picture taken, delete old one
                    if (deleteOldImage) {
                        File pic = new File(storageDir + "/" + temp.getPicture());
                        try {
                            pic.delete();
                        } catch (Exception ignored) {}
                    }

                    // Apply edits
                    temp.setTitle(title.getText().toString());
                    try {
                        temp.setWeight(Double.valueOf(weight.getText().toString()));
                    } catch (NumberFormatException ignored) {
                        temp.setWeight(0.0);
                    }
                    try {
                        temp.setLength(Double.valueOf(length.getText().toString()));
                    } catch (NumberFormatException ignored) {
                        temp.setLength(0.0);
                    }
                    temp.setInGrams(isChecked);

                    // Set new image name to fish
                    if (deleteOldImage) {
                        temp.setPicture(imageFileName);
                    }

                    // Reverse the list for recyclerView
                    Collections.reverse(fList);
                    SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),user + "_FishList", fList);

                    // Refresh recyclerView
                    ((MainInterface)requireActivity()).goToFragment(new Catches(), false);
                }).setNegativeButton("Add from gallery", null)
                .setNeutralButton("Delete", ((dialog, i) -> dialog.dismiss()));
        return builder.create();
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200));

        // Delete button confirmation for deleting a fish listener
        assert dialog != null;
        Button deleteConfirmation = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        deleteConfirmation.setOnClickListener(view1 -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
            alert.setTitle("Delete?");
            alert.setMessage("Are you sure you want to delete?");
            alert.setPositiveButton("Yes", (dialog1, which) -> {

                // Image that the fish had saved
                File pic = new File(storageDir);
                try {
                    pic.delete();
                } catch (Exception ignored) {
                    Toast.makeText(requireActivity(), "Couldn't delete the picture", Toast.LENGTH_SHORT).show();
                }
                // Get list, remove fish, save list
                fList = getfList();
                fList.remove(position);
                // Reverse list for recycler view
                Collections.reverse(fList);
                SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),user + "_FishList", fList);
                dismiss();

                // Refresh recyclerView
                ((MainInterface)requireActivity()).goToFragment(new Catches(), false);
            });
            alert.setNegativeButton("No", (dialog12, which) -> dialog12.dismiss());

            alert.show();
        });

        // Camera
        Button addPicture = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        addPicture.setOnClickListener(view1 -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

            // Create a File reference for future access
            photoFile = getImageFileUri(imageFileName);

            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            Uri fileProvider = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                // Start the image capture intent to take photo
                activityResultLauncherGallery.launch(intent);
            }
            /*Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            activityResultLauncherGallery.launch(gallery);*/
        });
        if ( image.getDrawable() == null ) {
            addPicture.setText("Add from gallery");
        } else {
            addPicture.setText("Change picture");
        }
        // Camera END
    }

    private ArrayList<Fish> getfList() {
        fList = SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),user + "_FishList");
        // Undo the reversing that was done in Catches
        Collections.reverse(fList);
        return fList;
    }

    // Create a Directory for the photo to be saved at
    @SuppressLint("DefaultLocale")
    public File getImageFileUri(String fileName) {

        oldImageName = imageFileName;
        imageFileName = String.format("%d.jpg", System.currentTimeMillis());

        // Get a safe storage directory for photos
        File mediaStorageDir = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FisherKing");
        storageDir = mediaStorageDir.getAbsolutePath();

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("Fisher King", "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + imageFileName);
    }
}