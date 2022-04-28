package com.example.vko11v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainFragment extends Fragment {

    String user;
    int dayTracker = 0;

    // Get weather variables
    final String APP_ID = "8083d74fdf91756ac7b6cba38cd2b8e9";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    String URLWeather;

    // Weather details
    ArrayList<Weather> weatherArrayList = new ArrayList<>();

    // Onscreen elements
    TextView currentTemperature;
    TextView feelsLikeTemperature;
    TextView morningTemperature;
    TextView eveningTemperature;
    TextView nightTemperature;
    TextView locality;
    TextView onScreenDate;
    TextView onScreenDayOfWeek;
    TextView weatherTypeText;
    TextView windSpeedText;
    TextView sunriseText;
    TextView sunsetText;

    ImageView nextDayWeather;
    ImageView previousDayWeather;
    ImageView weatherTypeImage;

    Button goToCatches;
    Button analyzeFish; //temp -> see if needed as button in final version (or display data automatically)

    // Get location variables
    FusedLocationProviderClient fusedLocationProviderClient;
    double location_latitude_value;
    double location_longitude_value;
    String location_locality;

    // Weather forecast
    String URLWeatherForecast;
    final String WEATHER_URL_FORECAST = "https://api.openweathermap.org/data/2.5/onecall";

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

        // Screen elements by ID
        // Temperature
        currentTemperature = view.findViewById(R.id.temperatureTextView);
        feelsLikeTemperature = view.findViewById(R.id.feelsLikeTextView);
        morningTemperature = view.findViewById(R.id.morningTemp);
        eveningTemperature = view.findViewById(R.id.eveningTemp);
        nightTemperature = view.findViewById(R.id.nightTemp);
        // Location / date
        onScreenDayOfWeek = view.findViewById(R.id.dayStringTextView);
        onScreenDate = view.findViewById(R.id.dateTextView);
        locality = view.findViewById(R.id.locality);
        // Weather type
        weatherTypeImage = view.findViewById(R.id.weatherTypeImageview);
        weatherTypeText = view.findViewById(R.id.weatherText);
        // Wind
        windSpeedText = view.findViewById(R.id.windSpeed);
        // Sunset / sunrise
        sunriseText = view.findViewById(R.id.sunriseTimeTextView);
        sunsetText = view.findViewById(R.id.sunsetTimeTextView);
        // Next / previous day buttons
        nextDayWeather = view.findViewById(R.id.nextDayButton);
        previousDayWeather = view.findViewById(R.id.previousDayButton);
        previousDayWeather.setVisibility(View.GONE);
        // Buttons
        goToCatches = view.findViewById(R.id.goToCatchesPageBtn);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Analyze fish button + listener -> TEMP see if needed in final version
        analyzeFish = view.findViewById(R.id.analysisBtn);
        analyzeFish.setOnClickListener(view1 -> analyzeFish());

        // Floating "+" button listener
        FloatingActionButton addCatch = view.findViewById(R.id.floatingAddCatch);
        addCatch.setOnClickListener(view12 -> ((MainInterface) requireActivity()).showAddCatchPopup(view12));

        getLocationForecast();

        //"Pre"Serialize arraylist if empty
        @SuppressLint("SdCardPath") File f = new File("/data/data/com.example.vko11v3/files/last_weather");
        if(!f.exists() && !f.isDirectory()) {
            SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),"last_weather", weatherArrayList);
        }

        weatherArrayList = SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),"last_weather");
        if (weatherArrayList == null) {weatherArrayList = new ArrayList<>();}
        else if (weatherArrayList.size() > 1) {updateInfoFromArrayList(weatherArrayList.get(0));}

        nextDayWeather.setOnClickListener(view1 -> {
            if (weatherArrayList.size() < 1) {
                nextDayWeather.setVisibility(View.GONE);
            } else {
                if (dayTracker >= weatherArrayList.size() - 2) {
                    nextDayWeather.setVisibility(View.GONE);
                } else {
                    previousDayWeather.setVisibility(View.VISIBLE);
                }
                dayTracker++;
                updateInfoFromArrayList(weatherArrayList.get(dayTracker));
            }
        });
        previousDayWeather.setOnClickListener(view1 -> {
            if ( dayTracker == 1 ) {
                previousDayWeather.setVisibility(View.GONE);
            } else {
                nextDayWeather.setVisibility(View.VISIBLE);
            }
            dayTracker--;
            updateInfoFromArrayList(weatherArrayList.get(dayTracker));
        });
        goToCatches.setOnClickListener(view1 -> {
            Fragment catches = new Catches();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.container_fragment, catches);
            transaction.commit();
        });
    }

    // Gets the users LastLocation and if successfully gets location
    // overwrites the saved Fish with one that has location data
    @SuppressLint("SetTextI18n")
    private void getLocationForecast() throws NullPointerException{
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
                        location_latitude_value = addresses.get(0).getLatitude();
                        location_longitude_value = addresses.get(0).getLongitude();
                        location_locality = addresses.get(0).getLocality(); // City

                        // Set current weather and location info
                        URLWeather = WEATHER_URL + "?lat=" +location_latitude_value+"&lon="+location_longitude_value+"&appid="+APP_ID;
                        readJSON(URLWeather);

                        // Get weather forecast based on coordinates
                        URLWeatherForecast = WEATHER_URL_FORECAST + "?lat=" +addresses.get(0).getLatitude()+"&lon="+addresses.get(0).getLongitude()+"&exclude=hourly,alerts,minutely&appid="+APP_ID;
                        System.out.println("*** Getting 8 day weather forecast for location: "+location_locality);
                        readJSONForecast(URLWeatherForecast);

                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (NullPointerException exception) {
            Toast.makeText(requireContext(), "Error while fetching location data", Toast.LENGTH_LONG).show();
            System.out.println("----- Error while getting location -----");
        }
    }

    // Weather forecast
    @SuppressLint("SetTextI18n")
    public void readJSONForecast (String URLWeatherForecast) {

        String json = getJSONForecast(URLWeatherForecast);
        //https://devqa.io/how-to-parse-json-in-java/
        try {
            //prints info to console from fixed URL:
            //http://jsonviewer.stack.hu/#https://api.openweathermap.org/data/2.5/onecall?lat=60.984752099999994&lon=25.657131500000002&exclude=hourly,alerts,minutely&appid=8083d74fdf91756ac7b6cba38cd2b8e9
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("daily");

            weatherArrayList = new ArrayList<>();

            for( int i=0; i < arr.length(); i++) {
                // Forecast date
                long dt = arr.getJSONObject(i).getLong("dt");
                Date date = new Date(dt*1000L);
                Calendar currentDate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat jdf = new SimpleDateFormat("dd/MM/yyyy");
                @SuppressLint("SimpleDateFormat") Format dayName = new SimpleDateFormat("EEEE");
                jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String java_date = jdf.format(date);
                String curr_date = jdf.format(currentDate.getTime());
                String date_name = dayName.format(date);

                if (curr_date.equals(java_date)) {
                    date_name = "Today";
                }

                // Temperature
                double dayTempC = Math.round(
                        (arr.getJSONObject(i).getJSONObject("temp").getDouble("day")
                                - 273.15)*10)/10.0; // converts temp Kelvin to C with 1 decimal point
                double morningTempC = Math.round(
                        (arr.getJSONObject(i).getJSONObject("temp").getDouble("morn")
                                - 273.15)*10)/10.0;
                double eveTempC = Math.round(
                        (arr.getJSONObject(i).getJSONObject("temp").getDouble("eve")
                                - 273.15)*10)/10.0;
                double nightTempC = Math.round(
                        (arr.getJSONObject(i).getJSONObject("temp").getDouble("night")
                                - 273.15)*10)/10.0;

                // Temperature feels like
                double dayTemp_feel = arr.getJSONObject(i).getJSONObject("feels_like").getDouble("day");
                double dayTempC_feel = Math.round((dayTemp_feel  - 273.15)*10)/10.0; // converts temp Kelvin to C with 1 decimal point

                // Weather description
                String description = arr.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");
                String weatherIcon = arr.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");

                // Wind speed
                double wind_speed = arr.getJSONObject(i).getDouble("wind_speed");

                // Sunrise
                long sunRise = arr.getJSONObject(i).getLong("sunrise");
                Date sunRiseDate = new Date(sunRise*1000L);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat jdf_sunRise = new SimpleDateFormat("HH:mm");
                jdf_sunRise.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String java_sunRise = jdf_sunRise.format(sunRiseDate);

                // Sunset
                long sunSet = arr.getJSONObject(i).getLong("sunset");
                Date sunSetDate = new Date(sunSet*1000L);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat jdf_sunSet = new SimpleDateFormat("HH:mm");
                jdf_sunSet.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                String java_sunSet = jdf_sunSet.format(sunSetDate);

                weatherArrayList.add(new Weather(
                        Integer.toString((int)Math.round(dayTempC)),
                        Integer.toString((int)Math.round(dayTempC_feel)),
                        Integer.toString((int)Math.round(morningTempC)),
                        Integer.toString((int)Math.round(eveTempC)),
                        Integer.toString((int)Math.round(nightTempC)),
                        location_locality,
                        java_date,
                        date_name,
                        description,
                        weatherIcon,
                        Integer.toString((int)Math.round(wind_speed)),
                        java_sunRise,
                        java_sunSet));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),"last_weather", weatherArrayList);
    }

    @SuppressLint("SetTextI18n")
    public void readJSON (String URLWeather) {
        String json = getJSONForecast(URLWeather);
        System.out.println("JSON: "+json);
        try {
            JSONObject jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Weather forecast
    //public String getJSONForecast (String URLWeatherForecast) {
    public String getJSONForecast (String URLWeatherForecast) {
        String response = null;

        try {
            //URL url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=60.984752099999994&lon=25.657131500000002&exclude=hourly,alerts,minutely&appid=8083d74fdf91756ac7b6cba38cd2b8e9");
            URL url = new URL(URLWeatherForecast);

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

    // Updates the onscreen element data from array list
    public void updateInfoFromArrayList(Weather weather) {
        currentTemperature.setText(weather.getCurrentTemperature());
        feelsLikeTemperature.setText(weather.getFeelsLikeTemperature());
        morningTemperature.setText(weather.getMorningTemperature());
        eveningTemperature.setText(weather.getEveningTemperature());
        nightTemperature.setText(weather.getNightTemperature());
        locality.setText(weather.getLocality());
        onScreenDate.setText(weather.getOnScreenDate());
        onScreenDayOfWeek.setText(weather.getOnScreenDayOfWeek());
        weatherTypeText.setText(weather.getWeatherTypeText());
        windSpeedText.setText(weather.getWindSpeedText());
        sunriseText.setText(weather.getSunriseText());
        sunsetText.setText(weather.getSunsetText());

        Bitmap bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w01d);
        switch (weather.getWeatherIcon()) {
            case ("01d"):
                bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w01d);
                break;
            case ("02d"):
                bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w02d);
                break;
            case ("03d"):
                bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w03d);
                break;
            case ("04d"):
                bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w04d);
                break;
            case ("09d"):
                bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w09d);
                break;
            case ("10d"):
                bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w10d);
                break;
            case ("11d"):
                bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w11d);
                break;
            case ("13d"):
                bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w13d);
                break;
            case ("50d"):
                bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.w50d);
                break;
            default:
                break;
        }
        weatherTypeImage.setImageBitmap(bitmap);
    }

    //WORK IN PROGRESS -> analyze caught fish
    private void analyzeFish() {
        ArrayList<Fish> fishHistory = new ArrayList<>();
        double total_weight = 0.0;
        double old_Weight = 0.0;
        double new_weight = 0.0;
        String maxCity = null;
        double maxCity_weight = 0.0;
        HashMap<String, Double> fishByCity = new HashMap<String, Double>();

        // Get current user
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String user = sharedPref.getString("current_user", "");
        System.out.println("*** test: current user is: "+user+" ***"); //TEMP

        //"Pre"Serialize arraylist if empty
        @SuppressLint("SdCardPath") File f = new File("/data/data/com.example.vko11v3/files/" + user + "_FishList");
        if(!f.exists() && !f.isDirectory()) {
            SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),user + "_FishList", fishHistory);
        }

        //TEMP - add some fish without locationd data
        /*fishHistory = SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),user + "_FishList");
        Fish f1 = new Fish("Särki", 120.0, true, 0.0, "null");
        Fish f2 = new Fish("Ahven", 300.0, true, 0.0, "null");
        Fish f3 = new Fish("Kuha", 1.2, false, 0.0, "null");
        fishHistory.add(f1);
        fishHistory.add(f2);
        fishHistory.add(f3);

        //TEMP - add some fish without locationd data
        Fish f4 = new Fish("Säynävä", 120.0, true, 0.0, "null", "61.233155", "28.337010", 10.0, "Saimaa");
        Fish f5 = new Fish("Hauki", 300.0, true, 0.0, "null", "61.233155", "28.337010", 10.0, "Päijänne");
        Fish f6 = new Fish("Siika", 1.2, false, 0.0, "null", "61.233155", "28.337010", 10.0, "Inari");
        fishHistory.add(f4);
        fishHistory.add(f5);
        fishHistory.add(f6);
        SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),user + "_FishList", fishHistory);*/

        // Saimaa 61.233155, 28.337010
        // Näsijärvi 61.671543, 23.739232
        // Lake Inari 68.969045, 27.643564

        // Deserialize existing fish ArrayList from file
        fishHistory = SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),user + "_FishList");


        for (Fish fish : fishHistory) {

            //calculate total weight of all caught fish
            if(fish.isInGrams()) {
                total_weight += fish.weight/1000;
            } else {
                total_weight += fish.weight;
            }


            //fill hashmap: locality + caught weight
            //if city already in hashmap
            if(fishByCity.containsKey(fish.locality)){

                //get old weight for city
                old_Weight = fishByCity.get(fish.locality);

                //convert new weight in city to KG´s before saving
                if(fish.isInGrams()) {
                    new_weight = fish.weight / 1000;
                } else {
                    new_weight = fish.weight;
                }

                fishByCity.put(fish.locality, old_Weight + new_weight);

                //if new city
            } else {

                //convert new weight in city to KG´s before saving
                if(fish.isInGrams()) {
                    new_weight = fish.weight/1000;
                } else {
                    new_weight = fish.weight;
                }
                fishByCity.put(fish.locality, new_weight);
            }
        }


        //Find max value city and weight in city
        for (String i : fishByCity.keySet()) {
            if(fishByCity.get(i) > maxCity_weight) {
                maxCity = i;
                maxCity_weight = fishByCity.get(i);
            }
        }


        //TEMP - Print analysis to console
        System.out.println("TOTAL WEIGHT OF YOUR CATCHES: "+ total_weight);
        System.out.println("TOTAL WEIGHT OF CATCHES BY CITY: ");
        for (String i : fishByCity.keySet()) {
            /*if(i != null) {
                System.out.println("City: " + i + ", Fish weight: " + fishByCity.get(i));
            }*/
            System.out.println("City: " + i + ", Fish weight: " + fishByCity.get(i));
        }
        System.out.println("*** Your best city in fish caught: "+maxCity+" : "+maxCity_weight+" ***");

    }

}