package com.example.vko11v3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainInterface {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    MenuItem nightModeSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch nightMode;
    TextView headerText;


    @SuppressLint("SetTextI18n")
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
        // sharedPref.edit().clear().apply(); // Uncomment to clear USER_DATA file


        // -- Load first fragment --
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        // Check if user has Remember me option checked, and who is logged in
        // depending on that set the default fragment as login fragment or main fragment
        if ( sharedPref.getString("logged_in_as", null) != null ) {
            fragmentTransaction.replace(R.id.container_fragment, new MainFragment());
            setNavHeaderText();
        } else {
            fragmentTransaction.replace(R.id.container_fragment, new LogInFragment());
        }
        fragmentTransaction.commit();
        // -- Load First fragment - END --


        // Nav drawer night mode switch listener
        nightModeSwitch = navigationView.getMenu().findItem(R.id.navigationNightModeSwitch);
        nightMode = (Switch) nightModeSwitch.getActionView().findViewById(R.id.drawerSwitch);
        nightMode.setChecked(true);
        nightMode.setOnCheckedChangeListener((compoundButton, b) -> {
            if ( b ) {
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
        if(menuItem.getItemId() == R.id.navigationHome) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new MainFragment());
            fragmentTransaction.commit();
        }

        // 2nd page
        if(menuItem.getItemId() == R.id.navigationSettings) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new SettingsFragment());
            fragmentTransaction.commit();
        }

        if(menuItem.getItemId() == R.id.navigationCatches) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new Catches());
            fragmentTransaction.commit();
        }

        // Logout
        if(menuItem.getItemId() == R.id.navigationLogout) {

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
        if(shouldLock){
            toolbar.setVisibility(View.GONE);
        }else{
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setNavHeaderText() {
        SharedPreferences sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String user = sharedPref.getString("current_user", null);
        if ( user != null ) {
            headerText.setText("Welcome back " + user);
        } else {
            headerText.setText("Who dis? Anonymous?");
        }
    }
}

/* katsottu apuja:
 * https://www.youtube.com/watch?v=USenYOBJw9Y
 * https://www.studytonight.com/android/get-edittext-set-textview
 *
 * Location:
 * https://www.youtube.com/watch?v=Ak1O9Gip-pg
 *
 */
