package com.example.vko11v3;

import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class Settings {

    int fonttikoko = 30;
    int leveys = 800;
    int korkeus = 300;
    boolean caps; //= false;
    EditText editText;
    String talletettuTeksti;
    Boolean editTextEnable = true;
    TextView luettavaTeksti;
    CharSequence editTextTallennus = "alustus teksti";
    CharSequence T4DisplayText;
    CharSequence T4Kieli;

    //public static final com.example.vko11v3.Settings instance = new com.example.vko11v3.Settings()

    private static Settings instance = null;
    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    private Settings() {

    }

}
