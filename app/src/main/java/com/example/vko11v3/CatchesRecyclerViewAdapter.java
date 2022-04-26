package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class CatchesRecyclerViewAdapter extends RecyclerView.Adapter<CatchesRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Fish> catches;
    recyclerInterFace rListener;

    // Get Context, Arraylist and interface as parameters
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
        // Create a new ViewHolder with view and interface
        return new CatchesRecyclerViewAdapter.MyViewHolder(view, rListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CatchesRecyclerViewAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // Initialize the values for each card in recyclerView
        // and pass them to ViewHolder
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

        // Get image folder Directory
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FisherKing");
        String storageDir = mediaStorageDir.getAbsolutePath();

        // Convert from JPG -> Bitmap and
        // add the image to the the recyclerView card
        Bitmap myBitmap = BitmapFactory.decodeFile(storageDir + "/" + catches.get(position).getPicture());
        if (!catches.get(position).getPicture().equals("null")) {
            holder.image.setRotation(90);
            holder.image.setImageBitmap(myBitmap);
        } else { // no image == no ImageView
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override // How many items in total
    public int getItemCount() {
        return catches.size();
    }

    // Inflates the card with given parameters / info
    public static class MyViewHolder extends RecyclerView.ViewHolder {

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

            // Opens the view / edit fish details AlertDialog
            card = itemView.findViewById(R.id.cardView);
            card.setOnClickListener(view -> rListener.openDetailsPopup(fish, position));

            // Opens the image in AlertDialog with bigger ImageView
            image = itemView.findViewById(R.id.recyclerImageView);
            image.setOnClickListener(view -> rListener.openFullScreenImage(fish));
        }
    }

    public interface recyclerInterFace {
        void openFullScreenImage(Fish fish);
        void openDetailsPopup(Fish fish, int position);
    }
}