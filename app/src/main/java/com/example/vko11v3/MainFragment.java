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
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

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

    //getFish variables (temp)
    Button btFish;

    //listFish variables (temp)
    Button btListFish;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainInterface) requireActivity()).hideNavToolbar(false);

        // Set as last been on fragment
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        sharedPref.edit().putString("last_page", "Main").apply();

        TextView textView = view.findViewById(R.id.textView);
        textView.setTextSize(Settings.getInstance().fonttikoko);
        textView.setAllCaps(Settings.getInstance().caps);
        textView.getLayoutParams().height = Settings.getInstance().korkeus;
        textView.getLayoutParams().width = Settings.getInstance().leveys;

        EditText editText = view.findViewById(R.id.editText);
        editText.setEnabled(Settings.getInstance().editTextEnable);

        TextView luettavaTeksti = view.findViewById(R.id.luettavaTeksti);
        TextView displayText = view.findViewById(R.id.displayText);
        TextView kielivalinta = view.findViewById(R.id.kielivalinta);

        //get weather
        temperature = view.findViewById(R.id.temperature);

        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Tässä kohtaa muokkaa asetuksia. Tuo parametri s on edittext-kentän tämänhetkinen arvo -> aseta se asetuksiin
                Settings.getInstance().editTextTallennus = s;
                System.out.println("menikö s settingsseihin?: " + Settings.getInstance().editTextTallennus);
            }
        });

        if (Settings.getInstance().editTextEnable == false) {
            luettavaTeksti.setText(Settings.getInstance().editTextTallennus);
        }

        displayText.setText(Settings.getInstance().T4DisplayText);
        kielivalinta.setText("Kieli:" + Settings.getInstance().T4Kieli);

        //get location variables:
        btLocation = view.findViewById(R.id.btLocation);
        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);
        countryName = view.findViewById(R.id.country);
        locality = view.findViewById(R.id.locality);
        address = view.findViewById(R.id.address);


        // get temperature:

        //onko tämä vielä tarpeellinen?:
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        btTemperature = view.findViewById(R.id.btTemperature);


        // Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity()); //ei pelkkä this, koska ollaan fragmentissa

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check permission
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        //mainActivity vai MainFragment -> ei tunnu toimivan kummallakaan
                        //eikä context: vs. activity -> kummallakaan
                        == PackageManager.PERMISSION_GRANTED) {
                    //When permission granted
                    getLocation();
                } else {
                    //When permission denied
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

        FloatingActionButton addCatch = view.findViewById(R.id.floatingAddCatch);
        addCatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainInterface) requireActivity()).showAddCatchPopup(view);
            }
        });

        btTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //readJSON(URLWeather);
                System.out.println("*** test - get temperature button pressed ***");
            }
        });

        //get Fish
        btFish = view.findViewById(R.id.btFish);
        btFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFish();
            }
        });

        //list Fish
        btListFish = view.findViewById(R.id.btListFish);
        btListFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listFish();
            }
        });

    }

    private void getFish() {
        System.out.println("*** New fish (kiinteä testikala) ***");
        Fish fish = new Fish();
        System.out.println(fish.getFish());
        System.out.println("******");

        ArrayList<Fish> fList = new ArrayList<Fish>();
        Fish f1 = new Fish("Särki", 2.2, 3.3, String.valueOf(61.011333), String.valueOf(25.614806));
        Fish f2 = new Fish("Hauki", 5.5, 3.3, String.valueOf(61.011333), String.valueOf(25.614806));
        Fish f3 = new Fish("Ahven", 4.4, 3.3, String.valueOf(61.011333), String.valueOf(25.614806));

        fList.add(f1);
        fList.add(f2);
        fList.add(f3);

        SerializeFish.instance.serializeData(getActivity().getApplicationContext(),"FishList", fList);
    }

    private void listFish() {
        System.out.println("*** List Fish method ***");

        ArrayList<Fish> fisut = new ArrayList<>();
        fisut = SerializeFish.instance.deSerializeData(getActivity().getApplicationContext(),"FishList");

        for (Fish f : fisut) {
            System.out.println(f.title + ": " + f.weight);
        }
    }



    //get location:
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Snackbar.make(getView(), "Give permissions **temp**", 3);

            // TODO: Consider calling
            //  ActivityCompat#requestPermissions
            //  here to request the missing permissions, and then overriding
            //  public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            //  to handle the case where the user grants the permission. See the documentation
            //  for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(getActivity(),
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                        //Set latitude on TextView
                        latitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLatitude())));
                        //Set longitude on TextView
                        longitude.setText(Html.fromHtml(String.valueOf(addresses.get(0).getLongitude())));
                                //Set country name
                                countryName.setText(addresses.get(0).getCountryName());
                        //Set locality
                        locality.setText(addresses.get(0).getLocality());
                        //Set address
                        address.setText(addresses.get(0).getAddressLine(0));

                        //get weather:
                        URLWeather = WEATHER_URL + "?lat=" +addresses.get(0).getLatitude()+"&lon="+addresses.get(0).getLongitude()+"&appid="+APP_ID;
                        System.out.println("*** URLWeather *** :"+URLWeather);
                        readJSON(URLWeather);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void readJSON (String URLWeather) {
        System.out.println("*** readJSON metodi ***");
        String json = getJSON(URLWeather);
        System.out.println("JSON: "+json);

        try {
            JSONObject jsonObject = new JSONObject(json);
            //JSONArray weatherMain = jsonObject.getJSONArray("main");
            double tempKelvin = jsonObject.getJSONObject("main").getDouble("temp");
            System.out.println("*** temperature in Kelvin: *** "+tempKelvin);
            double tempCelcius = tempKelvin - 273.15;
            System.out.println("*** temperature in Celsius: *** "+tempCelcius);
            temperature.setText(Double.toString(tempCelcius));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //vrt. Moodle video:
        /*if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for(int i=0; i<jsonArray.length(); i++)  {
                    JSONObject jobject = jsonArray.getJSONObject(i);
                    System.out.println("*** temp ****"+jobject.getString("temp"));
                    System.out.println("*** temp ****"+jobject.getString("description"));
                    System.out.println("*** temp ****"+jobject.getString("main"));
                    System.out.println("*** temp ****"+jobject.get("temp"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    public String getJSON (String URLWeather) {
        System.out.println("*** getJSON metodi ***");
        String response = null;

        try {
            //URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=60.982927&lon=25.660680&appid=8083d74fdf91756ac7b6cba38cd2b8e9");
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
