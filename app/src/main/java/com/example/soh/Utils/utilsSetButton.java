package com.example.soh.Utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.Button;

public class utilsSetButton {
    public static void setButtonStyle(Button button, String colorHex, float alpha) {
        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorHex)));
        button.setAlpha(alpha);
    }
}
