package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class Catches extends Fragment {

    ArrayList<CatchesData> catches = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catches, container, false);
    }

    @SuppressLint({"SetTextI18n", "CommitPrefEdits"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        sharedPref.edit().putString("last_page", "Catches").apply();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        setUpRecyclerView();

        CatchesRecyclerViewAdapter adapter = new CatchesRecyclerViewAdapter(getActivity(),
                catches);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setUpRecyclerView (  ) {
        String[] titles = {"title1", "title2", "title3", "title4", "title2", "title3", "title4"};
        String[] details = {"details1", "details2", "details3", "details4", "details2", "details3", "details4"};
        String[] timePlace = {"timePlace1", "timePlace2", "timePlace3", "timePlace4", "timePlace2", "timePlace3", "timePlace4"};

        for ( int i=0; i< titles.length; i++ ) {
            catches.add(new CatchesData(
                    titles[i],
                    details[i],
                    timePlace[i]
            ));
        }
    }
}