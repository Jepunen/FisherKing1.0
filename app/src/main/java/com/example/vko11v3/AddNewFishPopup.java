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
import android.widget.Toast;
import android.widget.ToggleButton;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddNewFishPopup extends AppCompatDialogFragment {

    EditText newFishName;
    EditText newFishWeight;
    EditText newFishLength;
    ToggleButton toggle;

    // Save fish
    ArrayList<Fish> fList = new ArrayList<Fish>();
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
    File photoFile = null;
    String storageDir;
    @SuppressLint("DefaultLocale")
    String photoFileName = "null";
    // Camera END

    // Location
    boolean isLocation = false;
    // Location END

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_new_fish, null);

        // Find screen elements by ID
        newFishName   = (EditText) view.findViewById(R.id.newFishName);
        newFishWeight = (EditText) view.findViewById(R.id.newFishWeight);
        newFishLength = (EditText) view.findViewById(R.id.newFishLength);
        imageView     = (ImageView) view.findViewById(R.id.newFishImageView);
        toggle        = (ToggleButton) view.findViewById(R.id.toggleButton);

        // Camera
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // Executes when a photo is taken and "OK" is pressed
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Photo saved here to the location that was allocated
                // Image view on popup
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                imageView.setRotation(90);
                imageView.setVisibility(View.VISIBLE);
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
        toggle.setOnCheckedChangeListener((compoundButton, b) -> {
            inGrams = b;
        });

        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity()); //ei pelkkä this, koska ollaan fragmentissa

        builder.setView(view)
                .setTitle("Add new fish")
                // Creates a new fish object and saves it to an array
                .setPositiveButton("Add", (dialogInterface, i) -> {

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

                    //"Pre"Serialize arraylist if empty
                    @SuppressLint("SdCardPath") File f = new File("/data/data/com.example.vko11v3/files/FishList");
                    if(!f.exists() && !f.isDirectory()) {
                        ArrayList<Fish> fList = new ArrayList<Fish>();
                        SerializeFish.instance.serializeData(getActivity().getApplicationContext(),"FishList", fList);
                    }

                    //Deserialize existing fish list to be able to append to it
                    fList = SerializeFish.instance.deSerializeData(getActivity().getApplicationContext(),"FishList");

                    //Location: check permission
/*                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //When permission granted
                        getLocation();

                    } else {
                        //When permission denied
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                        System.out.println("*** no permission for location -> save fish without coordinates or weather data ***");

                        //save fish to list (REMEMBER FILENAME CHANGE)
                        Fish fish = new Fish(title, weight, inGrams,length, photoFileName);
                        fList.add(fish);
                        SerializeFish.instance.serializeData(getActivity().getApplicationContext(),"FishList", fList);

                    }*/


                    getLocation();
                    if(!isLocation) {
                        Fish fish = new Fish(title, weight, inGrams, length, photoFileName);
                        fList.add(fish);
                        SerializeFish.instance.serializeData(getActivity().getApplicationContext(),"FishList", fList);
                    }

                    /*Fish fish = new Fish(title, weight, inGrams, length, photoFileName);
                    fList.add(fish);
                    SerializeFish.instance.serializeData(getActivity().getApplicationContext(),"FishList", fList);*/


                })
                // Listener for this is onResume method
                .setNeutralButton("Add picture", null);

        // Return AlertDialog builder.crete and .show() in MainActivity
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

            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                // Start the image capture intent to take photo
                activityResultLauncher.launch(intent);
            } else {
                Toast.makeText(requireActivity(), "Couldn't open the camera", Toast.LENGTH_LONG);
                System.out.println("Couldn't open the camera");
            }

        });
        // Camera END

        // Changes the text depending if user already taken photo
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

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            latitude = null;
            longitude = null;
            return;
        }


        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            //Initialize location
            Location location = task.getResult();

            //Is able to get location
            if (location != null) {
                try {

                    //Initialize geoCoder
                    Geocoder geocoder = new Geocoder(getActivity(),
                            Locale.getDefault());
                    //Initialize address list
                    List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);

                    //Set latitude on TextView
                    latitude = String.valueOf(Html.fromHtml(String.valueOf(addresses.get(0).getLatitude())));
                    //System.out.println("*** latitude inside addnewfishpopup ***: " + latitude);
                    //latitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLatitude())));

                    //Set longitude on TextView
                    longitude = String.valueOf(Html.fromHtml(String.valueOf(addresses.get(0).getLongitude())));
                    //System.out.println("*** longitude inside addnewfishpopup ***: " + longitude);
                    //longitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLongitude())));

                    //Set country name
                    //countryName.setText(addresses.get(0).getCountryName());

                    //Set locality
                    //locality.setText(addresses.get(0).getLocality());
                    locality = addresses.get(0).getLocality();

                    //Set address
                    //address.setText(addresses.get(0).getAddressLine(0));

                    //get weather:
                    URLWeather = WEATHER_URL + "?lat=" +addresses.get(0).getLatitude()+"&lon="+addresses.get(0).getLongitude()+"&appid="+APP_ID;
                    System.out.println("*** URLWeather *** :"+URLWeather);
                    tempCelcius = readJSON(URLWeather);

                    //save fish to list (REMEMBER FILENAME CHANGE)
                    Fish fish = new Fish(title, weight, inGrams, length, photoFileName, latitude, longitude, tempCelcius, locality); //gets date automatically from Fish - constructor
                    fList.add(fish);
                    SerializeFish.instance.serializeData(getActivity().getApplicationContext(),"FishList", fList);
                    isLocation = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public double readJSON (String URLWeather) {
        System.out.println("*** readJSON metodi ***");
        String json = getJSON(URLWeather);
        System.out.println("JSON: "+json);

        try {
            JSONObject jsonObject = new JSONObject(json);
            double tempKelvin = jsonObject.getJSONObject("main").getDouble("temp");
            System.out.println("*** temperature in Kelvin: *** "+tempKelvin);
            tempCelcius = tempKelvin - 273.15;
            System.out.println("*** temperature in Celsius: *** "+tempCelcius);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    return tempCelcius;
    }

    public String getJSON (String URLWeather) {
        System.out.println("*** getJSON metodi ***");
        String response = null;

        try {
            URL url = new URL(URLWeather);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            response = sb.toString();
            in.close();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

}
