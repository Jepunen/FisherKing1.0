package com.example.vko11v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddNewFishPopup extends AppCompatDialogFragment {

    EditText newFishName;
    EditText newFishWeight;
    EditText newFishLength;

    // Miten arraylist käyttäytyy, kun kirjautuu eri käyttäjällä?
    // Lisääkö vanhaan listaan toisen käyttäjän saaliit?
    // Muistaako edellisen kirjautumisen kalat vai ylikirjoittaako tiedoston -> historia katoaa?
    // Jos katoaa, niin pitää ensin ladata historia arraylistiin (deserialize) ja sitten vasta append + serialize?

    //Save fishes
    ArrayList<Fish> fList = new ArrayList<Fish>();
    FusedLocationProviderClient fusedLocationProviderClient;
    String latitude;
    String longitude;

    // Camera
    ImageView imageView;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File photoFile = null;
    String storageDir;
    @SuppressLint("DefaultLocale")
    String photoFileName = "null";
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                buttonCanBePressed(!TextUtils.isEmpty(newFishName.getText().toString()));
            }
        });

        // Location: Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity()); //ei pelkkä this, koska ollaan fragmentissa

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

                    //Jeres version:
                    //Fish fish = new Fish(title, weight, length, photoFileName, "latitude", "longitude", date.getTime());

                    //Deserialize existing fish list to be able to append to it
                    fList = SerializeFish.instance.deSerializeData(getActivity().getApplicationContext(),"FishList");

                    //Location: check permission
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //When permission granted
                        getLocation();

                        //START GETLOCATION TEMP
                        //....
                        //END


                        System.out.println("*** latitude inside setview ***: " + latitude);
                        System.out.println("*** longitude inside setview ***: " + longitude);

                        //Serialize fish (REMEMBER filename -> insert username)
                        Fish fish = new Fish(title, weight, length, photoFileName, latitude, longitude); //gets date automatically from Fish - constructor
                        fList.add(fish);
                        SerializeFish.instance.serializeData(getActivity().getApplicationContext(),"FishList", fList);
                    } else {
                        //When permission denied
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    }


                    //Serialize fish (REMEMBER filename -> insert username)
                    //Fish fish = new Fish(title, weight, length, photoFileName, "latitude", "longitude"); //gets date automatically from Fish - constructor
                    //Fish fish = new Fish(title, weight, length, photoFileName, latitude, longitude); //gets date automatically from Fish - constructor
                    //fList.add(fish);
                    //SerializeFish.instance.serializeData(getActivity().getApplicationContext(),"FishList", fList);



                    // TODO Add to list and Serialized file
                    //Jonas -> laita tänne latitude / longitude tiedot
                    //Jonas -> tänne serialisointi -> fileen tallennus



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
    @SuppressLint("DefaultLocale")
    public File getPhotoFileUri(String fileName) {

        photoFileName = String.format("%d.jpg", System.currentTimeMillis());

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


    //get location:
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Snackbar.make(getView(), "Give permissions **temp**", 3);

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        System.out.println("*** getlocation method inside addnewfishpopup ***");

                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(getActivity(),
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);

                        //Set latitude on TextView
                        latitude = String.valueOf(Html.fromHtml(String.valueOf(addresses.get(0).getLatitude())));
                        System.out.println("*** latitude inside addnewfishpopup ***: " + latitude);
                        //latitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLatitude())));

                        //Set longitude on TextView
                        longitude = String.valueOf(Html.fromHtml(String.valueOf(addresses.get(0).getLongitude())));
                        System.out.println("*** longitude inside addnewfishpopup ***: " + longitude);
                        //longitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLongitude())));

                        //Set country name
                        //countryName.setText(addresses.get(0).getCountryName());

                        //Set locality
                        //locality.setText(addresses.get(0).getLocality());

                        //Set address
                        //address.setText(addresses.get(0).getAddressLine(0));

                        //get weather:
                        //URLWeather = WEATHER_URL + "?lat=" +addresses.get(0).getLatitude()+"&lon="+addresses.get(0).getLongitude()+"&appid="+APP_ID;
                        //System.out.println("*** URLWeather *** :"+URLWeather);
                        //readJSON(URLWeather);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
