package com.example.vko11v3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainInterface {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ActionBar actionBar;
    ActionBar homeBtn;
    Toolbar toolbar;
    boolean drawerOpen = false;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    MenuItem nightModeSwitch;
    MenuItem homeStartPage;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch nightMode;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch homeStart;
    TextView headerText;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint({"SetTextI18n", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(20);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_home_24);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        // Navigation drawer header text
        View headerView = navigationView.getHeaderView(0);
        headerText = headerView.findViewById(R.id.drawer_header);
        ImageView logo = headerView.findViewById(R.id.drawerImageView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        logo.setImageBitmap(bitmap);
        logo.setOnClickListener(view -> {
            goToFragment(new MainFragment(), true);
            drawerLayout.close();
            drawerOpen = false;
        });
        // Get user data file
        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

        // -- Load first fragment --
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out
        );

        // Start from home page listener
        homeStartPage = navigationView.getMenu().findItem(R.id.navigationHomeStart);
        homeStart = homeStartPage.getActionView().findViewById(R.id.drawerSwitch);
        // Get saved switch state and set it
        homeStart.setChecked(sharedPref.getBoolean("always_start_from_home", true));
        // Listener to update switch state
        homeStart.setOnCheckedChangeListener((compoundButton, b)
                -> sharedPref.edit().putBoolean("always_start_from_home", b).apply());

        // Check if user has "Remember me" option checked, and who is logged in
        // depending on that set the default fragment as login fragment or main fragment
        if (!sharedPref.getString("logged_in_as", "null").equals("null")) {

            // If the user wants to always start from home page
            if ( !sharedPref.getBoolean("always_start_from_home", false) ) {
                // Get the last page user has been saved on
                String last_page = sharedPref.getString("last_page", null);
                if (last_page.equals("null")) {
                    // User hasn't been on home or catches page so assume they're not logged in
                    fragmentTransaction.replace(R.id.container_fragment, new LogInFragment(), "MY_LOGIN");
                } else {
                    switch (Objects.requireNonNull(last_page)) {
                        case ("Main"):
                            fragmentTransaction.replace(R.id.container_fragment, new MainFragment(), "MY_HOME");
                            break;
                        case ("Catches"):
                            fragmentTransaction.replace(R.id.container_fragment, new Catches(), "MY_CATCHES");
                            break;
                        default:
                            fragmentTransaction.replace(R.id.container_fragment, new LogInFragment(), "MY_LOGIN");
                            break;
                    }
                }
            } else { // User wants to start from home
                fragmentTransaction.replace(R.id.container_fragment, new MainFragment(), "MY_HOME");
            }
            // Update nav header text
            setNavHeaderText();
        } else { // User not logged in -> load login page
            fragmentTransaction.replace(R.id.container_fragment, new LogInFragment());
        }
        fragmentTransaction.commit();
        // -- Load First fragment - END --

        // Check if has permission to use location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // No location permission so request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        }

        // Nav drawer night mode switch listener
        nightModeSwitch = navigationView.getMenu().findItem(R.id.navigationNightModeSwitch);
        nightMode = nightModeSwitch.getActionView().findViewById(R.id.drawerSwitch);
        // Get saved switch state and set it
        nightMode.setChecked(sharedPref.getBoolean("night_mode", true));
        // Listener to update saved switch state
        nightMode.setOnCheckedChangeListener((compoundButton, b) -> {
            sharedPref.edit().putBoolean("night_mode", b).apply();
            if (b) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    @Override
    public void onBackPressed() {
        goToFragment(new MainFragment(), true);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (!drawerOpen) {
                drawerLayout.openDrawer(GravityCompat.START);
                drawerOpen = true;
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
                drawerOpen = false;
            }
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!drawerOpen) {
                    drawerLayout.openDrawer(GravityCompat.START);
                    drawerOpen = true;
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    drawerOpen = false;
                }
                return true;
            case R.id.action_home:
                goToFragment(new MainFragment(), true);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Home page
        if (menuItem.getItemId() == R.id.navigationHome) {
            goToFragment(new MainFragment(), true);
        }
        // 2nd page - Hidden currently, no need atm
        if (menuItem.getItemId() == R.id.navigationSettings) {
            goToFragment(new SettingsFragment(), true);
        }
        // All catches
        if (menuItem.getItemId() == R.id.navigationCatches) {
            goToFragment(new Catches(), true);
        }
        if (menuItem.getItemId() == R.id.action_home) {
            goToFragment(new MainFragment(), true);
        }
        // Logout
        if (menuItem.getItemId() == R.id.navigationLogout) {

            // Get user data file
            SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
            // Set values to null to indicate user not being logged in
            sharedPref.edit().putString("logged_in_as", null).apply();
            sharedPref.edit().putString("current_user", null).apply();

            // Redirect to login page
            goToFragment(new LogInFragment(), true);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        drawerOpen = false;
        return true;
    }

    /* * * * * * * * * * * * * * *
     * BELOW MainInterface METHODS
     * * * * * * * * * * * * * * */

    @Override // Enables / disables the navigation toolbar to prevent access to it
    public void hideNavToolbar(boolean shouldLock) {
        if (shouldLock) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override // Updates the navigation drawer header with the users name
    public void setNavHeaderText() {
        // Get user data file
        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String user = sharedPref.getString("current_user", null);
        if (user != null) {
            headerText.setText("Welcome back " + user);
        } else {
            // If someone manages to login without inputting username
            // resulting in username being null
            headerText.setText("Who dis? Anonymous?");
        }
    }

    @Override // Shows the popup for adding a new fish
    public void showAddCatchPopup(View view) {
        AddNewFishPopup dialog = new AddNewFishPopup();
        dialog.show(getSupportFragmentManager(), "Add new fish");
    }

    @Override // Shows the popup for adding username (biometric user)
    public void showAddUsernamePopup(View view) {
        AddUsernamePopup dialog = new AddUsernamePopup();
        dialog.show(getSupportFragmentManager(), "Add username");
    }

    @Override // Shows fish details / edit popup
    public void showFishDetails(Fish fish, int position) {
        ShowFishDetailsPopup dialog = new ShowFishDetailsPopup(fish, position);
        dialog.show(getSupportFragmentManager(), "Edit fish details");
    }

    @Override // Shows fullscreen image popup
    public void showImageFullscreen(Fish fish) {
        FullScreenImage dialog = new FullScreenImage(fish);
        dialog.show(getSupportFragmentManager(), "Fullscreen image");
    }

    @Override
    public void startGoogleMaps (Fish fish) {
        String geoLocation = "http://maps.google.com/maps?q=loc:" + fish.getLatitude() + "," + fish.getLongitude();
        Intent laucnhMaps = new Intent(Intent.ACTION_VIEW);
        laucnhMaps.setData(Uri.parse(geoLocation));
        Intent chooseMaps = Intent.createChooser(laucnhMaps, "Launch maps");
        startActivity(chooseMaps);
    }

    @Override
    public void makeToast (String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void goToFragment (Fragment fragment, boolean animated) {

        if ( animated ) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out
            );
            fragmentTransaction.replace(R.id.container_fragment, fragment);
            fragmentTransaction.commit();
        } else {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, fragment);
            fragmentTransaction.commit();
        }
    }
}