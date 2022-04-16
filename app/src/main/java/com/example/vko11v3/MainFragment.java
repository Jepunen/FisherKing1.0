package com.example.vko11v3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    //get location muuttujat (en tiedä onko parempi olla täällä vai esim. onViewCreated:n sisällä
    Button btLocation;
    TextView latitude, longitude, countryName, locality, address;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainInterface) requireActivity()).hideNavToolbar(false);

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

        //get location muuttujat:
        btLocation = view.findViewById(R.id.btLocation);
        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);
        countryName = view.findViewById(R.id.country);
        locality = view.findViewById(R.id.locality);
        address = view.findViewById(R.id.address);


        // get location:
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
                ((MainInterface) requireActivity()).createPopup(view);
            }
        });

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


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
