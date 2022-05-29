package com.example.gravityball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

public class Navigator {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Navigator(){}

    public static void initialize(Context context) {
        Navigator.context = context.getApplicationContext();
    }

    public static void returnTo(Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void runNewActivity(Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
