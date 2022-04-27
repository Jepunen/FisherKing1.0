package com.example.vko11v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainFragment extends Fragment {

    String user;

    //get weather variables
    final String APP_ID = "8083d74fdf91756ac7b6cba38cd2b8e9";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    TextView temperature;
    Button btTemperature;
    String URLWeather;

    //get location variables
    Button btLocation;
    TextView latitude, longitude, countryName, locality, address;
    FusedLocationProviderClient fusedLocationProviderClient;

    //getFish variables TEMP
    Button btFish;
    Button btListFish;

    //weather forecast
    String URLWeatherForecast;
    final String WEATHER_URL_FORECAST = "https://api.openweathermap.org/data/2.5/onecall";
    boolean isLocation = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Show navigation toolbar
        ((MainInterface) requireActivity()).hideNavToolbar(false);



        // Set as last been on fragment
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        sharedPref.edit().putString("last_page", "Main").apply();
        user = sharedPref.getString("current_user", "");

        //get weather
        temperature = view.findViewById(R.id.temperature);
        btTemperature = view.findViewById(R.id.btTemperature);

        //get location elements:
        btLocation = view.findViewById(R.id.btLocation);
        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);
        countryName = view.findViewById(R.id.country);
        locality = view.findViewById(R.id.locality);
        address = view.findViewById(R.id.address);


        // get temperature:

        //Is this still necessary?: COMMENTED OUT IF STATEMENT TO TEST
        /*if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        btLocation.setOnClickListener(view1 -> {
            // Check permission
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // If permission to get location
                getLocation();
            } else {
                // Not permission to get location
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        });

        // Floating "+" button listener
        FloatingActionButton addCatch = view.findViewById(R.id.floatingAddCatch);
        addCatch.setOnClickListener(view12 -> ((MainInterface) requireActivity()).showAddCatchPopup(view12));

        // "Get temperature" button listener
        btTemperature.setOnClickListener(view13 -> {
            //readJSON(URLWeather);
            System.out.println("*** test - Weather forecast button pressed ***");
            readJSONForecast();

        });

        // Get Fish button listener
        btFish = view.findViewById(R.id.btFish);
        btFish.setOnClickListener(view14 -> createFish());

        // System out print fish list button listener
        btListFish = view.findViewById(R.id.btListFish);
        btListFish.setOnClickListener(view15 -> listFish());

    }

    // Creates 3 fish and adds? them to the file - TEMP
    private void createFish() {

        ArrayList<Fish> fList = new ArrayList<>();
        Fish f1 = new Fish("Särki", 120.0, true, 0.0, "null");
        Fish f2 = new Fish("Ahven", 300.0, true, 0.0, "null");
        Fish f3 = new Fish("Kuha", 1.2, false, 0.0, "null");

        fList.add(f1);
        fList.add(f2);
        fList.add(f3);

        SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),user + "_FishList", fList);
    }

    // Prints fish out to console - TEMP
    private void listFish() {
        ArrayList<Fish> fishHistory;
        fishHistory = SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),user + "_FishList");

        System.out.println();
        System.out.println("*** ALl YOUR CATCHES: ***");
        for (Fish f : fishHistory) {
            System.out.println(f.title + ": " + f.weight + "," + f.latitude + "," + f.longitude + "," + f.tempCelcius + "," + f.locality +"," + f.date);
        }
    }

    // Get location
    private void getLocation() {
        // Check if permission to get location
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
            if (location != null) {
                try {
                    //Initialize geoCoder
                    Geocoder geocoder = new Geocoder(getActivity(),
                            Locale.getDefault());
                    //Initialize address list
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    //List<Address> addresses = geocoder.getFromLocation(61.008106, 25.618022, 1);

                    //Set latitude on TextView
                    latitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLatitude())));
                    //latitude.setText(Html.fromHtml(String.valueOf(61.008106)));
                    //Set longitude on TextView
                    longitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLongitude())));
                    //longitude.setText(Html.fromHtml(String.valueOf(25.618022)));
                    //Set country name
                    countryName.setText(addresses.get(0).getCountryName());
                    //Set locality
                    locality.setText(addresses.get(0).getLocality());
                    //Set address
                    address.setText(addresses.get(0).getAddressLine(0));
                    // -> Check "lake" if you have the time

                    //get weather:
                    URLWeather = WEATHER_URL + "?lat=" +addresses.get(0).getLatitude()+"&lon="+addresses.get(0).getLongitude()+"&appid="+APP_ID;
                    System.out.println("*** URLWeather *** :"+URLWeather);
                    readJSON(URLWeather);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void readJSON (String URLWeather) {
        String json = getJSON(URLWeather);
        System.out.println("JSON: "+json);

        try {
            JSONObject jsonObject = new JSONObject(json);
            double tempKelvin = jsonObject.getJSONObject("main").getDouble("temp");
            double tempCelcius = tempKelvin - 273.15;
            System.out.println("*** temperature in Celsius: *** "+tempCelcius);
            temperature.setText(Double.toString(tempCelcius));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getJSON (String URLWeather) {
        String response = null;

        try {
            //URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=60.982927&lon=25.660680&appid=8083d74fdf91756ac7b6cba38cd2b8e9");
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

    //Weather forecast
    @SuppressLint("SetTextI18n")
    //public void readJSONForecast (String URLWeatherForecast) {
    public void readJSONForecast () {
        //
        String json = getJSONForecast();
        //System.out.println("JSON: "+json);
        //https://devqa.io/how-to-parse-json-in-java/
        try {

            //prints info to console from fixed URL:
            //http://jsonviewer.stack.hu/#https://api.openweathermap.org/data/2.5/onecall?lat=60.984752099999994&lon=25.657131500000002&exclude=hourly,alerts,minutely&appid=8083d74fdf91756ac7b6cba38cd2b8e9
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("daily");
            for( int i = 0; i < arr.length(); i++) {

                //forecast date
                long dt = arr.getJSONObject(i).getLong("dt");
                Date date = new Date(dt*1000L);
                SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd");
                jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String java_date = jdf.format(date);
                //https://www.w3resource.com/java-exercises/datetime/java-datetime-exercise-36.php

                //temperature
                Double dayTemp = arr.getJSONObject(i).getJSONObject("temp").getDouble("day");
                Double dayTempC = Math.round((dayTemp - 273.15)*10)/10.0; // converts temp Kelvin to C with 1 decimal point
                Double morningTemp = arr.getJSONObject(i).getJSONObject("temp").getDouble("morn");
                Double morningTempC = Math.round((morningTemp - 273.15)*10)/10.0;
                Double eveTemp = arr.getJSONObject(i).getJSONObject("temp").getDouble("eve");
                Double eveTempC = Math.round((eveTemp - 273.15)*10)/10.0;
                Double nightTemp = arr.getJSONObject(i).getJSONObject("temp").getDouble("night");
                Double nightTempC = Math.round((nightTemp - 273.15)*10)/10.0;

                //temperature feels like
                Double dayTemp_feel = arr.getJSONObject(i).getJSONObject("feels_like").getDouble("day");
                Double dayTempC_feel = Math.round((dayTemp_feel  - 273.15)*10)/10.0; // converts temp Kelvin to C with 1 decimal point

                //weather description
                String description = arr.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");

                //wind speed
                Double wind_speed = arr.getJSONObject(i).getDouble("wind_speed");

                //sunrise
                long sunRise = arr.getJSONObject(i).getLong("sunrise");
                Date sunRiseDate = new Date(sunRise*1000L);
                SimpleDateFormat jdf_sunRise = new SimpleDateFormat("HH:mm");
                jdf_sunRise.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String java_sunRise = jdf_sunRise.format(sunRiseDate);

                //sunset
                long sunSet = arr.getJSONObject(i).getLong("sunset");
                Date sunSetDate = new Date(sunSet*1000L);
                SimpleDateFormat jdf_sunSet = new SimpleDateFormat("HH:mm");
                jdf_sunSet.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String java_sunSet = jdf_sunSet.format(sunSetDate);

                //prints to console - TEMP
                System.out.println("\n*** forecast for date: "+java_date+" ***");
                System.out.println("*** morning temperature: "+morningTempC+" ***");
                System.out.println("*** day temperature: "+dayTempC+" ***");
                System.out.println("*** evening temperature: "+eveTempC+" ***");
                System.out.println("*** night temperature: "+nightTempC+" ***");
                System.out.println("*** day temperature feels like:"+dayTempC_feel+" ***");
                System.out.println("*** description: "+description+" ***");
                System.out.println("*** wind speed: "+wind_speed+" ***");
                System.out.println("*** sun rise: "+java_sunRise+" ***");
                System.out.println("*** sun set: "+java_sunSet+" ***");
            }

            //tuulen nopeus
            //temp. how it feels
            //koko päivän temp (morning noon night)
            //sunset / sunrise

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Weather forecast
    //public String getJSONForecast (String URLWeatherForecast) {
    public String getJSONForecast () {
        String response = null;

        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=60.984752099999994&lon=25.657131500000002&exclude=hourly,alerts,minutely&appid=8083d74fdf91756ac7b6cba38cd2b8e9");
            //URL url = new URL(URLWeather);

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