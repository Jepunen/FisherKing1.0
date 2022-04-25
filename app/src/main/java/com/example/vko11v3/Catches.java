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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class Catches extends Fragment implements CatchesRecyclerViewAdapter.recyclerInterFace {

    ArrayList<Fish> catches = new ArrayList<>();

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

        @SuppressLint("SdCardPath") File f = new File("/data/data/com.example.vko11v3/files/FishList");
        if(!f.exists() && !f.isDirectory()) {
            ArrayList<Fish> fList = new ArrayList<Fish>();
            SerializeFish.instance.serializeData(requireActivity().getApplicationContext(),"FishList", fList);
        }

        setUpRecyclerView();

        CatchesRecyclerViewAdapter adapter = new CatchesRecyclerViewAdapter(requireActivity(),
                catches, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setUpRecyclerView (  ) {
        catches = SerializeFish.instance.deSerializeData(requireActivity().getApplicationContext(),"FishList");
        Collections.reverse(catches);
    }

    @Override
    public void openFullScreenImage(Fish fish) {
        ((MainInterface)requireActivity()).showImageFullscreen(fish);
    }

    @Override
    public void openDetailsPopup(Fish fish, int position) {
        ((MainInterface)requireActivity()).showFishDetails(fish, position);

    }
}