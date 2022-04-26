package com.example.vko11v3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainInterface {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
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


    @SuppressLint({"SetTextI18n", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        headerText = (TextView) headerView.findViewById(R.id.drawer_header);


        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);

        // -- Load first fragment --
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        // Start from home page listener
        homeStartPage = navigationView.getMenu().findItem(R.id.navigationHomeStart);
        homeStart = (Switch) homeStartPage.getActionView().findViewById(R.id.drawerSwitch);
        homeStart.setChecked(sharedPref.getBoolean("always_start_from_home", true));
        homeStart.setOnCheckedChangeListener((compoundButton, b) -> {
            sharedPref.edit().putBoolean("always_start_from_home", b).apply();
        });

        // Check if user has Remember me option checked, and who is logged in
        // depending on that set the default fragment as login fragment or main fragment
        if (sharedPref.getString("logged_in_as", null) != null) {

            if ( !sharedPref.getBoolean("always_start_from_home", false) ) {
                String last_page = sharedPref.getString("last_page", null);
                if (last_page == "null") {
                    fragmentTransaction.replace(R.id.container_fragment, new LogInFragment(), "MY_FRAGMENT");
                } else {
                    switch (Objects.requireNonNull(last_page)) {
                        case ("Main"):
                            fragmentTransaction.replace(R.id.container_fragment, new MainFragment(), "MY_FRAGMENT");
                            break;
                        case ("Catches"):
                            fragmentTransaction.replace(R.id.container_fragment, new Catches(), "MY_FRAGMENT");
                            break;
                        default:
                            fragmentTransaction.replace(R.id.container_fragment, new LogInFragment(), "MY_FRAGMENT");
                            break;
                    }
                }
            } else {
                fragmentTransaction.replace(R.id.container_fragment, new MainFragment(), "MY_FRAGMENT");
            }
            setNavHeaderText();
        } else {
            fragmentTransaction.replace(R.id.container_fragment, new LogInFragment());
        }
        fragmentTransaction.commit();
        // -- Load First fragment - END --

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        // Nav drawer night mode switch listener
        nightModeSwitch = navigationView.getMenu().findItem(R.id.navigationNightModeSwitch);
        nightMode = (Switch) nightModeSwitch.getActionView().findViewById(R.id.drawerSwitch);
        nightMode.setChecked(sharedPref.getBoolean("night_mode", true));
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        // Home page
        if (menuItem.getItemId() == R.id.navigationHome) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new MainFragment());
            fragmentTransaction.commit();
        }

        // 2nd page
        if (menuItem.getItemId() == R.id.navigationSettings) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new SettingsFragment());
            fragmentTransaction.commit();
        }

        // All catches
        if (menuItem.getItemId() == R.id.navigationCatches) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new Catches());
            fragmentTransaction.commit();
        }

        // Logout
        if (menuItem.getItemId() == R.id.navigationLogout) {

            SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
            sharedPref.edit().putString("logged_in_as", null).apply();
            sharedPref.edit().putString("current_user", null).apply();

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new LogInFragment());
            fragmentTransaction.commit();
        }
        return true;
    }

    @Override
    public void hideNavToolbar(boolean shouldLock) {
        if (shouldLock) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setNavHeaderText() {
        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String user = sharedPref.getString("current_user", null);
        if (user != null) {
            headerText.setText("Welcome back " + user);
        } else {
            headerText.setText("Who dis? Anonymous?");
        }
    }

    @Override
    public void showAddCatchPopup(View view) {
        AddNewFishPopup dialog = new AddNewFishPopup();
        dialog.show(getSupportFragmentManager(), "Add new fish");
    }

    @Override
    public void showAddUsernamePopup(View view) {

        AddUsernamePopup dialog = new AddUsernamePopup();
        dialog.show(getSupportFragmentManager(), "Add username");

    }

    @Override
    public void showFishDetails(Fish fish, int position) {

        ShowFishDetailsPopup dialog = new ShowFishDetailsPopup(fish, position);
        dialog.show(getSupportFragmentManager(), "Edit fish details");

    }

    @Override
    public void showImageFullscreen(Fish fish) {

        FullScreenImage dialog = new FullScreenImage(fish);
        dialog.show(getSupportFragmentManager(), "Fullscreen image");

    }
}

/* Help from sources:
 * https://www.youtube.com/watch?v=USenYOBJw9Y
 * https://www.studytonight.com/android/get-edittext-set-textview
 *
 * Location:
 * https://www.youtube.com/watch?v=Ak1O9Gip-pg
 *
 * Weather:
 * https://www.youtube.com/watch?v=VHgM_MQBQPg&list=PL1tIj6UC0gctBrAVI9GD4G_pgw7tOUQEc&index=2
 * https://openweathermap.org/current
 *
 */
