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
import android.os.Build;
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
import androidx.annotation.RequiresApi;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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
    TextView totalCatches;
    TextView fisherRank;
    TextView bestCity;

    ImageView nextDayWeather;
    ImageView previousDayWeather;
    ImageView weatherTypeImage;

    Button goToCatches;

    // Get location variables
    FusedLocationProviderClient fusedLocationProviderClient;
    double location_latitude_value;
    double location_longitude_value;
    String location_locality;

    // Weather forecast variables
    String URLWeatherForecast;
    final String WEATHER_URL_FORECAST = "https://api.openweathermap.org/data/2.5/onecall";

    // Analyze fish variables
    double total_weight = 0.0;
    double old_Weight = 0.0;
    double new_weight = 0.0;
    String maxCity = "No data";
    double maxCity_weight = 0.0;
    HashMap<String, Double> fishByCity = new HashMap<>();

    // Ranking
    String rank = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"CommitPrefEdits", "SetTextI18n"})
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
        // Button
        goToCatches = view.findViewById(R.id.goToCatchesPageBtn);
        // Fish stats
        totalCatches = view.findViewById(R.id.totalCatchesTextView);
        fisherRank = view.findViewById(R.id.rankTextView);
        bestCity =  view.findViewById(R.id.bestCityTextView);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Floating "+" button listener
        FloatingActionButton addCatch = view.findViewById(R.id.floatingAddCatch);
        addCatch.setOnClickListener(view12 -> ((MainInterface) requireActivity()).showAddCatchPopup(view12));

        // Updates the weather if location data available and last update over an hour ago
        String lastWeatherUpdate = sharedPref.getString("last_weather_update", null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm", Locale.ENGLISH);
        if (lastWeatherUpdate != null) {
            String currentTime = getCurrentDateAndTime();
            LocalDateTime lastUpdate = LocalDateTime.parse(lastWeatherUpdate, formatter);
            LocalDateTime currTime   = LocalDateTime.parse(currentTime, formatter);
            if (currTime.isAfter(lastUpdate.plusHours(1))) {
                getLocationForecast();
            }
        } else {
            getLocationForecast();
        }

        //"Pre"Serialize arraylist if empty
        @SuppressLint("SdCardPath") File f = new File("/data/data/com.example.vko11v3/files/last_weather");
        if(!f.exists() && !f.isDirectory()) {
            SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),"last_weather", weatherArrayList);
        }

        // Get weather data from file
        weatherArrayList = SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),"last_weather");
        if (weatherArrayList == null) {weatherArrayList = new ArrayList<>();}
        else if (weatherArrayList.size() > 1) {updateInfoFromArrayList(weatherArrayList.get(0));}

        // Next day arrow
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
        // Previous day arrow
        previousDayWeather.setOnClickListener(view1 -> {
            if ( dayTracker == 1 ) {
                previousDayWeather.setVisibility(View.GONE);
            } else {
                nextDayWeather.setVisibility(View.VISIBLE);
            }
            dayTracker--;
            updateInfoFromArrayList(weatherArrayList.get(dayTracker));
        });
        goToCatches.setOnClickListener(view1 -> ((MainInterface)requireActivity()).goToFragment(new Catches(), true));

        // Set users data for caught fish
        analyzeFish();
        rank = fishingRank(total_weight);
        totalCatches.setText("Your fish total: " + Math.round(total_weight*10.0)/10.0 + " kg");
        fisherRank.setText("Your rank: " + rank);
        bestCity.setText("Best location: " + maxCity + ", " + maxCity_weight + " kg");

        onScreenDayOfWeek.setOnClickListener(view1 -> {
            getLocationForecast();
            Toast.makeText(requireContext(), "Trying to refresh location data...", Toast.LENGTH_SHORT).show();
        });
    }

    // Gets the users LastLocation and if successfully gets location
    // updates the weather information
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
        SharedPreferences sharedPref = requireContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        sharedPref.edit().putString("last_weather_update", getCurrentDateAndTime()).apply();
        updateInfoFromArrayList(weatherArrayList.get(0));
        dayTracker = 0;
        refreshPage();
        ((MainInterface)requireActivity()).makeToast("Weather data updated!");
    }

    @SuppressLint("SetTextI18n")
    public void readJSON (String URLWeather) {
        String json = getJSONForecast(URLWeather);
        System.out.println("JSON: "+json);
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Weather forecast
    //public String getJSONForecast (String URLWeatherForecast) {
    public String getJSONForecast (String URLWeatherForecast) {
        String response = null;

        try {
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

    // Analyze caught fish
    private void analyzeFish() {
        ArrayList<Fish> fishHistory = new ArrayList<>();

        // Get current user
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String user = sharedPref.getString("current_user", "");
        System.out.println("*** test: current user is: "+user+" ***"); //TEMP

        //"Pre"Serialize arraylist if empty
        @SuppressLint("SdCardPath") File f = new File("/data/data/com.example.vko11v3/files/" + user + "_FishList");
        if(!f.exists() && !f.isDirectory()) {
            SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),user + "_FishList", fishHistory);
        }

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
            if(fishByCity.containsKey(fish.locality)) {

                //get old weight for city
                try {
                    old_Weight = fishByCity.get(fish.locality);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

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
    }

    private String fishingRank(double total_weight) {
        if (total_weight < 10) {
            rank = "Beginner";
        } else if (total_weight < 20) {
            rank = "Amateur";
        } else if (total_weight < 50) {
            rank = "Novice";
        } else if (total_weight < 100) {
            rank = "Oarsman";
        } else if (total_weight < 200) {
            rank = "First mate";
        } else if (total_weight < 300) {
            rank = "Fisher";
        } else if (total_weight < 400) {
            rank = "Badass";
        } else if (total_weight < 500) {
            rank = "Captain";
        } else if (total_weight < 600) {
            rank = "Fleet Commander";
        } else if (total_weight < 700) {
            rank = "Admiral";
        } else if (total_weight < 800) {
            rank = "Supermaster";
        } else if (total_weight < 900) {
            rank = "Fishing demon";
        } else if (total_weight < 1000) {
            rank = "Fishing god";
        } else {
            rank = "* Fisher King *";
        }
        return rank;
    }

    public String getCurrentDateAndTime () {
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
        return date.format(cal.getTime());
    }

    public void refreshPage() {
        ((MainInterface)requireActivity()).goToFragment(new MainFragment(), true);
    }
}