package com.example.gravityball.drawing;

import static com.example.gravityball.drawing.ImageUtils.getCroppedBitmap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Pair;

import com.example.gravityball.R;
import com.example.gravityball.world.GameWorld;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;

public class GameDrawer {
    public final GameWorld gameWorld;
    public final ScaleCalculator scaleCalculator;
    Disco disco = new Disco();

    private ArrayList<Rect> walls = new ArrayList<>();
    private ArrayList<Rect> obstacles = new ArrayList<>();
    Rect treasurePos;
    Rect fullScreenRect;

    private Vec2 mainBallPosition;
    private int mainBallRadiusInPixels;
    Matrix ballTransposition = new Matrix();

    Bitmap ballBitmap, treasureBitmap, endBitmap;

    private Paint backgroundPaint;
    private Paint obstaclePaint;
    private Paint wallPaint;

    public GameDrawer(GameWorld gameWorld, ScaleCalculator scaleCalculator, Resources resources) {
        this.gameWorld = gameWorld;
        this.scaleCalculator = scaleCalculator;

        preparePaints();

        getBitmaps(resources);

        fullScreenRect = new Rect(0,0,scaleCalculator.deviceScreenX, scaleCalculator.deviceScreenY);

        prepareStaticObjects();
    }

    private void getBitmaps(Resources resources) {
        ballBitmap = BitmapFactory.decodeResource(resources, R.drawable.ball);
        treasureBitmap = BitmapFactory.decodeResource(resources, R.drawable.cheese);
        endBitmap = BitmapFactory.decodeResource(resources, R.drawable.youwin);
    }

    private void preparePaints() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(102, 0, 51));

        obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.RED);

        wallPaint = new Paint();
        wallPaint.setColor(Color.rgb(255, 255, 153));
    }

    private void prepareStaticObjects(){
        walls = new ArrayList<>();
        for(Pair<Vec2, Vec2> wall : gameWorld.walls)
            walls.add(scaleCalculator.virtualRectangleToRect(wall));

        obstacles = new ArrayList<>();
        for(Pair<Vec2, Vec2> obstacle : gameWorld.obstacles)
            obstacles.add(scaleCalculator.virtualRectangleToRect(obstacle));

        treasurePos = scaleCalculator.virtualRectangleToRect(gameWorld.treasurePosition);

        mainBallRadiusInPixels = scaleCalculator.virtualDistanceToPixels(gameWorld.ballRadius);

        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, 2* mainBallRadiusInPixels, 2* mainBallRadiusInPixels, false);
        ballBitmap = ImageUtils.getCroppedBitmap(ballBitmap);
    }


    public void draw(Canvas canvas) {

        canvas.drawColor(backgroundPaint.getColor());

        if(gameWorld.isEnd) {
            canvas.drawBitmap(endBitmap, null, fullScreenRect, null);
            return;
        }

        getBallScreenPosition();

        drawGameObjects(canvas);

        disco.makeDisco(canvas, fullScreenRect);
    }

    private void drawGameObjects(Canvas canvas) {
        canvas.drawBitmap(ballBitmap, ballTransposition, null);

        for(Rect r : obstacles) {
            canvas.drawRect(r, obstaclePaint);
        }

        for(Rect r : walls) {
            canvas.drawRect(r, wallPaint);
        }

        canvas.drawBitmap(treasureBitmap, null, treasurePos, null);
    }


    private void getBallScreenPosition() {
        mainBallPosition = gameWorld.mainBall.getPosition();
        scaleCalculator.virtualPositionToPixels(mainBallPosition);
        calculateBallTransposition();
    }

    private void calculateBallTransposition() {
        ballTransposition.setRotate(-gameWorld.mainBall.getAngle()/((float)Math.PI*2)*360, ballBitmap.getWidth()/2, ballBitmap.getHeight()/2);
        ballTransposition.postTranslate(mainBallPosition.x- mainBallRadiusInPixels, mainBallPosition.y- mainBallRadiusInPixels);
    }
}
