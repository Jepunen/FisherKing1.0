package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.itemView.setLongClickable(true);

        // Initialize the values for each card in recyclerView
        // and pass them to ViewHolder
        Fish fish = catches.get(position);
        holder.title.setText(fish.getTitle());

        if ( fish.isInGrams() ) {
            holder.details.setText((int) Math.round(fish.getWeight()) + "g" + " - " + (int) Math.round(fish.getLength()) + "cm");
        } else {
            holder.details.setText(fish.getWeight() + "kg" + " - " + (int) Math.round(fish.getLength()) + "cm");
        }
        holder.timePlace.setText(fish.getDate());
        holder.fish = fish;
        holder.position = position;

        // Convert from JPG -> Bitmap and
        // add the image to the the recyclerView card
        if (catches.get(position).getLocality() == null) {
            holder.mapsPinImage.setVisibility(View.GONE);
            holder.imageText.setVisibility(View.GONE);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pin);
            holder.mapsPinImage.setImageBitmap(bitmap);
            holder.mapsPinImage.setVisibility(View.VISIBLE);
        }
        if (catches.get(position).getPicture().equals("null")) {
            holder.openPicture.setVisibility(View.GONE);
            holder.openPictureText.setVisibility(View.GONE);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.imageview);
            holder.openPicture.setImageBitmap(bitmap);
            holder.openPicture.setVisibility(View.VISIBLE);
        }
    }



    @Override // How many items in total
    public int getItemCount() {
        return catches.size();
    }

    // Inflates the card with given parameters / info
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, details, timePlace;
        ImageView mapsPinImage;
        ImageView openPicture;
        TextView openPictureText;
        TextView imageText;
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
            imageText = itemView.findViewById(R.id.recyclerImageTextView);
            openPictureText = itemView.findViewById(R.id.openPictureText);

            // Opens the view / edit fish details AlertDialog
            card = itemView.findViewById(R.id.cardView);
            card.setOnClickListener(view -> rListener.openDetailsPopup(fish, position));

            openPicture = itemView.findViewById(R.id.openPictureFS);
            openPicture.setOnClickListener(view -> rListener.openFullScreenImage(fish));

            // Opens location in google maps
            mapsPinImage = itemView.findViewById(R.id.recyclerImageView);
            mapsPinImage.setOnClickListener(view -> {
                if (fish.getLocality() != null)
                    rListener.startGoogleMaps(fish);
            });
            imageText.setOnClickListener(view -> {
                if (fish.getLocality() != null)
                    rListener.startGoogleMaps(fish);
            });
        }
    }

    public interface recyclerInterFace {
        void openFullScreenImage(Fish fish);
        void openDetailsPopup(Fish fish, int position);
        void startGoogleMaps(Fish fish);
    }
}