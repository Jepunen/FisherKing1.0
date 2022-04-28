package com.example.vko11v3;

import android.view.View;

// Explained in MainActivity

public interface MainInterface {
    void hideNavToolbar(boolean hidden);
    void setNavHeaderText();
    void showAddCatchPopup(View view);
    void showAddUsernamePopup(View view);
    void showFishDetails(Fish fish, int position);
    void showImageFullscreen(Fish fish);
    void startGoogleMaps(Fish fish);
    void makeToast(String text);
}
