package com.example.vko11v3;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.File;


public class FullScreenImage extends AppCompatDialogFragment {

    ImageView image;
    Fish fish;

    public FullScreenImage(Fish fish) {
        this.fish = fish;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_fullscreen_image, null);

        image = (ImageView) view.findViewById(R.id.fullScreenImageView);

        File mediaStorageDir = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FisherKing");
        String storageDir = mediaStorageDir.getAbsolutePath() + "/" + fish.getPicture();

        Bitmap myBitmap = BitmapFactory.decodeFile(storageDir);
        image.setRotation(90);
        image.setImageBitmap(myBitmap);

        builder.setView(view)
                .setTitle("")
                .setPositiveButton("Close", (dialogInterface, i) -> {});
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
