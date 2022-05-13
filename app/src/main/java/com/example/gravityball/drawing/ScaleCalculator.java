package com.example.gravityball.drawing;

import static java.lang.Math.min;

import android.graphics.Rect;
import android.util.Pair;

import org.jbox2d.common.Vec2;

public class ScaleCalculator {
    public final float worldWidth, worldHeight;
    public final int deviceScreenX, deviceScreenY;
    public final int gameScreenX, gameScreenY;
    public final int marginX, marginY;

    public ScaleCalculator(float worldWidth, float worldHeight, int deviceScreenX, int deviceScreenY, float screenScale) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.deviceScreenX = deviceScreenX;
        this.deviceScreenY = deviceScreenY;

        float scale = min((float) deviceScreenX /worldWidth, (float) deviceScreenY /worldHeight)*screenScale;
        gameScreenX = (int)(worldWidth*scale);
        gameScreenY = (int)(worldHeight*scale);

        marginX = (deviceScreenX -gameScreenX)/2;
        marginY = (deviceScreenY -gameScreenY)/2;
    }

    public void virtualPositionToPixels(Vec2 vec) {
        vec.x/=worldWidth; vec.x*=gameScreenX; vec.x+= marginX;
        vec.y/=worldHeight; vec.y = 1-vec.y; vec.y*=gameScreenY; vec.y+= marginY;
    }

    public int virtualDistanceToPixels(float dist) {
        return (int) (dist * ((float) gameScreenY/ worldHeight));
    }

    public Rect virtualRectangleToRect(Pair<Vec2, Vec2> point) {
        Vec2 tl = new Vec2(), br = new Vec2();
        tl.set(point.first); br.set(point.second);
        virtualPositionToPixels(tl);
        virtualPositionToPixels(br);
        return new Rect((int)tl.x,(int) tl.y,(int) br.x, (int)br.y);
    }
}
