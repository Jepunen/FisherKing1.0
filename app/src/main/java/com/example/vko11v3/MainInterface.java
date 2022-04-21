package com.example.vko11v3;

import android.view.View;

import androidx.camera.lifecycle.ProcessCameraProvider;

public interface MainInterface {
    void hideNavToolbar(boolean hidden);
    void setNavHeaderText();
    void showAddCatchPopup(View view);
    void showAddUsernamePopup(View view);
}
