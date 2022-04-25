package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class CatchesRecyclerViewAdapter extends RecyclerView.Adapter<CatchesRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Fish> catches;

    recyclerInterFace rListener;


    public CatchesRecyclerViewAdapter (Activity context, ArrayList<Fish> listOfCatches, recyclerInterFace rListener) {
        this.context = context;
        this.catches = listOfCatches;
        this.rListener = rListener;
    }

    @NonNull
    @Override
    public CatchesRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate layout and give look to rows

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new CatchesRecyclerViewAdapter.MyViewHolder(view, rListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CatchesRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Fish fish = catches.get(position);
        if (fish.getWeight() == 0.0) {
            holder.title.setText(fish.getTitle() + " / weight not set" );
        } else {
            if ( fish.isInGrams() ) {
                holder.title.setText(fish.getTitle() + " / " + (int) Math.round(fish.getWeight()) + "g");
            } else {
                holder.title.setText(fish.getTitle() + " / " + fish.getWeight() + "kg");
            }
        }
        holder.details.setText(fish.getDate());
        if ( fish.getLocality() == null ) {
            holder.timePlace.setText("Location data not saved");
        } else {
            holder.timePlace.setText(fish.getLocality());
        }
        holder.fish = fish;
        holder.position = position;

        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FisherKing");
        String storageDir = mediaStorageDir.getAbsolutePath();

        Bitmap myBitmap = BitmapFactory.decodeFile(storageDir + "/" + catches.get(position).getPicture());
        if (!catches.get(position).getPicture().equals("null")) {
            holder.image.setRotation(90);
            holder.image.setImageBitmap(myBitmap);
        } else {
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        // How many items in total
        return catches.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        // Basically onCreate
        TextView title, details, timePlace;
        ImageView image;
        CardView card;
        Fish fish;
        int position;

        recyclerInterFace rListener;

        public MyViewHolder(@NonNull View itemView, recyclerInterFace rListener) {
            super(itemView);
            this.rListener = rListener;

            title = itemView.findViewById(R.id.recyclerRowTitle);
            details = itemView.findViewById(R.id.recyclerRowDetail);
            timePlace = itemView.findViewById(R.id.recyclerRowTimePlace);

            card = itemView.findViewById(R.id.cardView);
            card.setOnClickListener(view -> {
                rListener.openDetailsPopup(fish, position);
            });


            image = itemView.findViewById(R.id.recyclerImageView);
            image.setOnClickListener(view -> {
                rListener.openFullScreenImage(fish);
            });


        }

    }

    public interface recyclerInterFace {
        void openFullScreenImage(Fish fish);
        void openDetailsPopup(Fish fish, int position);
    }
}
