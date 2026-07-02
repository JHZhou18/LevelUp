package com.utils.videojuegos;

import com.example.videojuegos.*;
import android.content.Context;
import android.content.Intent;

public class Navegator {
    public static void launchActivity(Context context, Class<?> destination) {
        Intent launcher = new Intent(context, destination);
        context.startActivity(launcher);
    }
}
