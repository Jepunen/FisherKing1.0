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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Catches extends Fragment implements CatchesRecyclerViewAdapter.recyclerInterFace {

    ArrayList<Fish> catches = new ArrayList<>();
    String user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catches, container, false);
    }

    @SuppressLint({"SetTextI18n", "CommitPrefEdits"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the user data file and set "Catches" as last been on page
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        sharedPref.edit().putString("last_page", "Catches").apply();
        user = sharedPref.getString("current_user", "");

        // Find get element by ID
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        // Check if fish list file exists, and if not, create it
        @SuppressLint("SdCardPath") File f = new File("/data/data/com.example.vko11v3/files/" + user + "_FishList");
        if(!f.exists() && !f.isDirectory()) {
            SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),user + "_FishList", catches);
        }
        setUpRecyclerView();

        // Pass ArrayList and Activity to Adapter
        CatchesRecyclerViewAdapter adapter = new CatchesRecyclerViewAdapter(requireActivity(),
                catches, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // Get fish from file and reverse file to show last added fish first
    private void setUpRecyclerView (  ) {
        catches = SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),user + "_FishList");
        Collections.reverse(catches);
    }

    @Override // Opens and AlertDialog that shows the picture in a bigger imageview
    public void openFullScreenImage(Fish fish) {
        ((MainInterface)requireActivity()).showImageFullscreen(fish);
    }

    @Override // Opens the edit / show more fish details AlertDialog
    public void openDetailsPopup(Fish fish, int position) {
        ((MainInterface)requireActivity()).showFishDetails(fish, position);

    }
}