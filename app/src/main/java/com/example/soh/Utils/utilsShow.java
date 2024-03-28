package com.example.soh.Utils;
import android.content.Context;
import android.widget.Toast;

public class utilsShow {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
