package com.example.vko11v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddNewFishPopup extends AppCompatDialogFragment {

    EditText newFishName;
    EditText newFishWeight;
    EditText newFishLength;
    ToggleButton toggle;

    // Save fish
    ArrayList<Fish> fList = new ArrayList<>();
    FusedLocationProviderClient fusedLocationProviderClient;
    String latitude;
    String longitude;
    String title;
    double weight = 0.0;
    double length = 0.0;
    boolean inGrams = false;
    String locality;
    // Save fish END

    // Weather
    final String APP_ID = "8083d74fdf91756ac7b6cba38cd2b8e9";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    String URLWeather;
    double tempCelcius = 0.0;
    // Weather END

    // Camera
    ImageView imageView;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    File imageFile = null;
    String storageDir;
    @SuppressLint("DefaultLocale")
    String imageFileName = "null";
    // Camera END

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_new_fish, null);

        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Find screen elements by ID
        newFishName   = view.findViewById(R.id.newFishName);
        newFishWeight = view.findViewById(R.id.newFishWeight);
        newFishLength = view.findViewById(R.id.newFishLength);
        imageView     = view.findViewById(R.id.newFishImageView);
        toggle        = view.findViewById(R.id.toggleButton);

        // Camera
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // Executes when a photo is taken and "OK" is pressed
            // and saves the taken image to the allocated file spot
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Image view on popup
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                imageView.setRotation(90);
                imageView.setVisibility(View.VISIBLE);
            }
            if (result.getResultCode() == Activity.RESULT_CANCELED) {
                Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        // Camera END

        // Text change listener for title
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
        // Listener for KG / GRAMS toggle button
        toggle.setOnCheckedChangeListener((compoundButton, b) -> inGrams = b);

        builder.setView(view)
                .setTitle("Add new fish")
                // Creates a new fish object and saves it to an array
                .setPositiveButton("Save", (dialogInterface, i) -> {

                    // Title cannot be empty, so set text
                    // (see buttonCanBePressed())
                    title = newFishName.getText().toString();
                    weight = 0.0;
                    length = 0.0;

                    // If weight / length != 0, set values
                    if ( !TextUtils.isEmpty(newFishWeight.getText().toString()) ) {
                        weight = Double.parseDouble(newFishWeight.getText().toString());
                    }
                    if ( !TextUtils.isEmpty(newFishLength.getText().toString()) ) {
                        length = Double.parseDouble(newFishLength.getText().toString());
                    }

                    // Get ArrayList from file
                    fList = getFishArrayFromFile();

                    // Create a base fish without location data
                    Fish fish = new Fish(title, weight, inGrams, length, imageFileName);
                    addFishToFile(fish);

                    // Go try to get location and in case we get a location
                    // Overwrites the saved fish with a new fish that has location data
                    getLocation();
                    ((MainInterface)requireActivity()).makeToast("Fish saved");
                    ((MainInterface)requireActivity()).goToFragment(new MainFragment(), false);
                })
                // Listener for this is in onResume method
                .setNeutralButton("Add picture", null);

        // Return AlertDialog builder.crete and .show() in MainActivity
        return builder.create();
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200));

        // Disable the "Add" button if Title text field is empty
        buttonCanBePressed(!TextUtils.isEmpty(newFishName.getText().toString()));

        // Camera
        assert dialog != null;
        Button addPicture = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        addPicture.setOnClickListener(view1 -> {

            // Create a File reference for future access
            imageFile = getImageFileUri();

            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            Uri fileProvider = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".provider", imageFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // Launch the camera intent // Start camera app
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                activityResultLauncher.launch(intent);
            } else {
                Toast.makeText(requireActivity(), "Couldn't open the camera", Toast.LENGTH_SHORT).show();
            }
        });
        // Camera END

        // Changes the text of "Add picture"
        // depending if user already taken photo
        if ( imageView.getDrawable() == null ) {
            addPicture.setText("Add picture");
        } else {
            addPicture.setText("Retake picture");
        }
    }

    // Disables / Enables the "Add" button
    public void buttonCanBePressed(boolean b) {
        AlertDialog dialog = (AlertDialog) getDialog();
        assert dialog != null;
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(b);
    }

    // Serializes fish and adds it to fList ArrayList
    // Then serializes the ArrayList to a file
    private void addFishToFile(Fish fish) {
        fList.add(fish);
        // Get current user
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String user = sharedPref.getString("current_user", "");
        SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),user + "_FishList", fList);
    }

    private ArrayList<Fish> getFishArrayFromFile() {

        // Get current user
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String user = sharedPref.getString("current_user", "");

        //"Pre"Serialize arraylist if empty
        @SuppressLint("SdCardPath") File f = new File("/data/data/com.example.vko11v3/files/" + user + "_FishList");
        if(!f.exists() && !f.isDirectory()) {
            ArrayList<Fish> fList = new ArrayList<>();
            SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),user + "_FishList", fList);
        }

        // Deserialize existing fish ArrayList from file and return it
        return SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),user + "_FishList");
    }

    // Create a Directory for the photo to be saved at
    @SuppressLint("DefaultLocale")
    public File getImageFileUri() {

        // Generate a "random" file name
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


    // Gets the users LastLocation and if successfully gets location
    // overwrites the saved Fish with one that has location data
    private void getLocation() throws NullPointerException{

        try {
            // Check if has permission to use location
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // No location permission so request permission
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                return;
            }

            // Add listener for when the location data is received
            // if no location data is received this never executes
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                //Initialize location
                Location location = task.getResult();

                // If successfully retrieved location data
                if (location != null) {
                    try {
                        // Initialize geoCoder
                        Geocoder geocoder = new Geocoder(getActivity(),
                                Locale.getDefault());
                        // Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);

                        // Set variables
                        latitude = String.valueOf(Html.fromHtml(String.valueOf(addresses.get(0).getLatitude())));
                        longitude = String.valueOf(Html.fromHtml(String.valueOf(addresses.get(0).getLongitude())));
                        locality = addresses.get(0).getLocality(); // City

                        // Get weather data from coordinates
                        URLWeather = WEATHER_URL + "?lat=" +addresses.get(0).getLatitude()+"&lon="+addresses.get(0).getLongitude()+"&appid="+APP_ID;
                        tempCelcius = readJSON(URLWeather);

                        // Overwrites the previously saved fish with one that has the location data
                        Fish fish = new Fish(title, weight, inGrams, length, imageFileName, latitude, longitude, tempCelcius, locality);
                        fList = getFishArrayFromFile();

                        // Remove the previously added fish without location data
                        fList.remove(fList.size() - 1);

                        // Add new fish to file
                        addFishToFile(fish);

                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (NullPointerException exception) {
            Toast.makeText(requireContext(), "Error while fetching location data", Toast.LENGTH_LONG).show();
            System.out.println("----- Error while getting location -----");
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(requireContext(), "Error while saving new fish", Toast.LENGTH_LONG).show();
            System.out.println("----- Error with file while getting location -----");
        }
    }

    // Reads the JSON file received from Open Weathers api
    public double readJSON (String URLWeather) {

        // Gets the JSON from URL with another method
        String json = getJSON(URLWeather);

        try {
            JSONObject jsonObject = new JSONObject(json);

            // Get the temp from JSON and convert KELVIN -> C
            double tempKelvin = jsonObject.getJSONObject("main").getDouble("temp");
            tempCelcius = tempKelvin - 273.15;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempCelcius;
    }

    // Gets JSON from URL
    public String getJSON (String URLWeather) {

        // Returns null if fails to get JSON
        String response = null;
        try {
            URL url = new URL(URLWeather);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            response = sb.toString();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}