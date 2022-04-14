package com.example.vko11v3;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance){
        //käytä view muuttujaa findViewbyIDn sijaan view.findViewById
        ((DrawerLocker) requireActivity()).setDrawerLocked(false);

        setupSeekbar(view);
        leveysSeekbar(view);
        korkeusSeekbar(view);
        aSwitch (view);
        muokkausSwitch (view);
        T4DisplayText (view);
        T4Kielivalinta (view);
    }

    private void setupSeekbar(View view) {
        SeekBar seekbar = view.findViewById(R.id.fonttiKoko);
        seekbar.setProgress(Settings.getInstance().fonttikoko);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Settings.getInstance().fonttikoko = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void leveysSeekbar(View view) {
        SeekBar seekbar = view.findViewById(R.id.leveysSeekBar);
        seekbar.setProgress(Settings.getInstance().leveys/10);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Settings.getInstance().leveys = i*10;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void korkeusSeekbar(View view) {
        SeekBar seekbar = view.findViewById(R.id.korkeusSeekBar);
        seekbar.setProgress(Settings.getInstance().korkeus/5);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Settings.getInstance().korkeus = i*5;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    private void aSwitch(View view) {


        Switch aSwitch = view.findViewById(R.id.switch1);

        aSwitch.setChecked(Settings.getInstance().caps);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (aSwitch.isChecked()) {
                    //switchText.setText("Switch is on");
                    System.out.println("Switch is on");
                    Settings.getInstance().caps = true;
                } else {
                    //switchText.setText("Switch is off");
                    System.out.println("Switch is off");
                    Settings.getInstance().caps = false;
                }
            }
        });
    }

    private void muokkausSwitch(View view) {


        Switch muokkausSwitch = view.findViewById(R.id.switch2);

        muokkausSwitch.setChecked(Settings.getInstance().editTextEnable);
        muokkausSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (muokkausSwitch.isChecked()) {
                    System.out.println("Muokkaus Switch is on");
                    Settings.getInstance().editTextEnable = true;

                } else {
                    System.out.println("Muokkaus Switch is off");
                    Settings.getInstance().editTextEnable = false;
                }
            }
        });
    }


    //T4

    private void T4DisplayText(View view) {

        EditText T4DisplayText = view.findViewById(R.id.T4DisplayText);
        T4DisplayText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Tässä kohtaa muokkaa asetuksia. Tuo parametri s on edittext-kentän tämänhetkinen arvo -> aseta se asetuksiin
                Settings.getInstance().T4DisplayText = s;
                System.out.println("menikö T4 s settingsseihin?: " + Settings.getInstance().T4DisplayText);

            }
        });

    }


    private void T4Kielivalinta(View view) {

        EditText T4Kielivalinta = view.findViewById(R.id.T4Kielivalinta);
        T4Kielivalinta.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Tässä kohtaa muokkaa asetuksia. Tuo parametri s on edittext-kentän tämänhetkinen arvo -> aseta se asetuksiin
                Settings.getInstance().T4Kieli = s;
                System.out.println("menikö T4 s settingsseihin?: " + Settings.getInstance().T4Kieli);

            }
        });

    }

}
