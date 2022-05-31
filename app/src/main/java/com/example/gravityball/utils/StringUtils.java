package com.example.gravityball.utils;

public class StringUtils {
    public static String millisToString(long timeInMillis) {
        long MI = timeInMillis%1000; timeInMillis/=1000;
        long SS = timeInMillis%60; timeInMillis/=60;
        long MM = timeInMillis;

        return String.format("%02d:%02d:%03d", MM, SS, MI);
    }
}
