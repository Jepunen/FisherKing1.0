package com.example.vko11v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CatchesRecyclerViewAdapter extends RecyclerView.Adapter<CatchesRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Fish> catches;

    public CatchesRecyclerViewAdapter (Context context, ArrayList<Fish> listOfCatches) {
        this.context = context;
        this.catches = listOfCatches;
    }

    @NonNull
    @Override
    public CatchesRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate layout and give look to rows

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new CatchesRecyclerViewAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CatchesRecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.title.setText(catches.get(position).getTitle());
        holder.details.setText(catches.get(position).getLatitude());
        holder.timePlace.setText(catches.get(position).getPicture());

    }

    @Override
    public int getItemCount() {
        // How many items in total
        return catches.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        // Basically onCreate
        TextView title, details, timePlace;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.recyclerRowTitle);
            details = itemView.findViewById(R.id.recyclerRowDetail);
            timePlace = itemView.findViewById(R.id.recyclerRowTimePlace);

        }

    }
}
