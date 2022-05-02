package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the user data file and set "Catches" as last been on page
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        sharedPref.edit().putString("last_page", "Catches").apply();
        user = sharedPref.getString("current_user", "");

        // Get element by ID
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
    @SuppressLint("SdCardPath")
    private void setUpRecyclerView (  ) {
        // Get ArrayList from file
        catches = SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),user + "_FishList");
        Collections.reverse(catches);
        // Check for leaked images in scoped storage
        File f = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FisherKing");
        if (f.isDirectory()) {
            String[] files = f.list();
            if (files.length != 0) {
                deleteExtraFiles(f);
            }
        }
    }
    // Finds and deletes images that aren't used
    public void deleteExtraFiles(File folder) {
        File[] files = folder.listFiles();
        // Add all pictures in use to ArrayList
        ArrayList<String> usedPictures = new ArrayList<>();
        for ( Fish temp : catches ) {
            usedPictures.add(temp.getPicture());
        }
        if (files!=null) {
            for (File f: files) {
                f.getName();
                if(!usedPictures.contains(f.getName())) {
                    f.delete();
                }
            }
        }
    }

    /* * * * * * * * * * * * * * *
     * BELOW recyclerInterface METHODS
     * * * * * * * * * * * * * * */

    @Override // Opens the image in gallery view
    public void openFullScreenImage(Fish fish) {
        ((MainInterface)requireActivity()).showImageFullscreen(fish);
    }

    @Override // Opens the edit fish details AlertDialog
    public void openDetailsPopup(Fish fish, int position) {
        ((MainInterface)requireActivity()).showFishDetails(fish, position);

    }

    @Override // Opens Google Maps in given coordinates
    public void startGoogleMaps(Fish fish) {
        ((MainInterface)requireActivity()).startGoogleMaps(fish);
    }
}