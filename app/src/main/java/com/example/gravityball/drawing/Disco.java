package com.example.gravityball.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Collections;
import java.util.Vector;

public class Disco {
    int turn = 8, counter = 0;
    Paint blue = new Paint(), red = new Paint(), yellow = new Paint(), green = new Paint();
    Vector<Paint> colors = new Vector<>();
    Paint currPaint = blue;
    Paint prevPaint;

    {
        blue.setColor(Color.BLUE);
        blue.setAlpha(60);
        yellow.setColor(Color.YELLOW);
        yellow.setAlpha(60);
        red.setColor(Color.RED);
        red.setAlpha(60);
        green.setColor(Color.GREEN);
        green.setAlpha(60);

        colors.add(blue); colors.add(red); colors.add(yellow); colors.add(green);
    }

    public void makeDisco(Canvas c, Rect r) {
        c.drawRect(r, currPaint);
        counter++;
        if(counter >= turn) {
            counter = 0;
            prevPaint = currPaint;
            while(prevPaint == currPaint) {
                Collections.shuffle(colors);
                currPaint = colors.get(0);
            }
        }
    }

}
