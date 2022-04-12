package com.example.vko11v3;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Tässä kohtaa muokkaa asetuksia. Tuo parametri s on edittext-kentän tämänhetkinen arvo -> aseta se asetuksiin
                Settings.getInstance().editTextTallennus = s;
                System.out.println("menikö s settingsseihin?: "+Settings.getInstance().editTextTallennus);

            }
        });

        if(Settings.getInstance().editTextEnable == false) {
            luettavaTeksti.setText(Settings.getInstance().editTextTallennus);
        }

        displayText.setText(Settings.getInstance().T4DisplayText);
        kielivalinta.setText("Kieli:"+Settings.getInstance().T4Kieli);


    }

}
