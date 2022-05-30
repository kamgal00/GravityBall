package com.example.gravityball.drawing;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Pair;

import com.example.gravityball.R;
import com.example.gravityball.world.GameWorld;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;
import java.util.Arrays;

public class GameDrawer {
    public final GameWorld gameWorld;
    public final ScaleCalculator scaleCalculator;
    Disco disco = new Disco();

    private ArrayList<Rect> walls = new ArrayList<>();
    private ArrayList<Rect> obstacles = new ArrayList<>();
    Rect treasurePos;
    Rect fullScreenRect;

//    private Vec2 mainBallPosition;
    private final int mainBallId;
    private int mainBallRadiusInPixels;
//    Matrix ballTransposition = new Matrix();
    private final ArrayList<Matrix> ballTranspositions;
    private int gamePlace = -1;

    Bitmap ballBitmap, treasureBitmap, endBitmap, enemyBitmap;

    private Paint backgroundPaint;
    private Paint obstaclePaint;
    private Paint wallPaint;
    private Paint endScreenPaint;

    public GameDrawer(GameWorld gameWorld, ScaleCalculator scaleCalculator, Resources resources, int players, int playerId) {
        this.gameWorld = gameWorld;
        this.scaleCalculator = scaleCalculator;

        preparePaints();

        getBitmaps(resources);

        fullScreenRect = new Rect(0,0,scaleCalculator.deviceScreenX, scaleCalculator.deviceScreenY);

        prepareStaticObjects();

        mainBallId = playerId;
        ballTranspositions =new ArrayList<>(Arrays.asList(new Matrix[players]));
        for(int i=0;i<ballTranspositions.size();i++ ) ballTranspositions.set(i, new Matrix());
    }

    private void getBitmaps(Resources resources) {
        ballBitmap = BitmapFactory.decodeResource(resources, R.drawable.ball);
        treasureBitmap = BitmapFactory.decodeResource(resources, R.drawable.cheese);
        endBitmap = BitmapFactory.decodeResource(resources, R.drawable.youwin);
        enemyBitmap = BitmapFactory.decodeResource(resources, R.drawable.enemy);
    }

    private void preparePaints() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(102, 0, 51));

        obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.RED);

        wallPaint = new Paint();
        wallPaint.setColor(Color.rgb(255, 255, 153));

        endScreenPaint = new Paint();
        endScreenPaint.setColor(Color.BLUE);
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

        enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, 2* mainBallRadiusInPixels, 2* mainBallRadiusInPixels, false);
        enemyBitmap = ImageUtils.getCroppedBitmap(enemyBitmap);
    }


    public void draw(Canvas canvas) {


        if(playerFinished()) {
            if(gamePlace == -1) {
                calculateGamePlace();
            }

            drawEndScreen(canvas);

            return;
        }

        canvas.drawColor(backgroundPaint.getColor());

        drawGameObjects(canvas);

        disco.makeDisco(canvas, fullScreenRect);

        drawTime(canvas, System.currentTimeMillis() - gameWorld.startTime);
    }

    private boolean playerFinished() {
        return gameWorld.times.get(mainBallId) != -1;
    }

    private void calculateGamePlace() {
        gamePlace = 1;
        for(Long t : gameWorld.times) {
            if(t!=-1 &&  t < gameWorld.times.get(mainBallId)) gamePlace++;
        }
    }

    private void drawEndScreen(Canvas canvas) {

        if(gamePlace == 1) {
            canvas.drawBitmap(endBitmap, null, fullScreenRect, null);
        }
        else {
            canvas.drawColor(endScreenPaint.getColor());
            TextPaint p = new TextPaint();
            p.setTextSize(300);

            p.setTextAlign(Paint.Align.CENTER);

            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((p.descent() + p.ascent()) / 2)) ;

            canvas.drawText("#"+gamePlace, xPos, yPos, p);
        }


        drawTime(canvas, gameWorld.times.get(mainBallId)-gameWorld.serverStartTime);
    }

    public void update(){
        getBallScreenPosition();
    }

    private void drawGameObjects(Canvas canvas) {
        for(int i=0;i<ballTranspositions.size();i++) {
            if(i!= mainBallId) {
                canvas.drawBitmap(enemyBitmap, ballTranspositions.get(i), null);
            }
        }

        canvas.drawBitmap(ballBitmap, ballTranspositions.get(mainBallId), null);

        for(Rect r : obstacles) {
            canvas.drawRect(r, obstaclePaint);
        }

        for(Rect r : walls) {
            canvas.drawRect(r, wallPaint);
        }

        canvas.drawBitmap(treasureBitmap, null, treasurePos, null);
    }


    private void getBallScreenPosition() {
        for(int i=0;i<ballTranspositions.size();i++) {
            Vec2 newPosition = new Vec2(gameWorld.positions.get(i));
            if(newPosition.x== Float.POSITIVE_INFINITY || newPosition.x == Float.NEGATIVE_INFINITY)
                return;
            scaleCalculator.virtualPositionToPixels(newPosition);
            calculateBallTransposition(newPosition, i);
        }
    }

    private void calculateBallTransposition(Vec2 position, int id) {
        ballTranspositions.get(id).setRotate(-gameWorld.angles.get(id)/((float)Math.PI*2)*360, ballBitmap.getWidth()/2, ballBitmap.getHeight()/2);
        ballTranspositions.get(id).postTranslate(position.x- mainBallRadiusInPixels, position.y- mainBallRadiusInPixels);
    }

    public static String millisToString(long timeInMillis) {
        long MI = timeInMillis%1000; timeInMillis/=1000;
        long SS = timeInMillis%60; timeInMillis/=60;
        long MM = timeInMillis;

        return String.format("%02d:%02d:%03d", MM, SS, MI);
    }

    private void drawTime(Canvas canvas, long timeInMillis) {

        String timeInMMSSMI = millisToString(timeInMillis);

        TextPaint p = new TextPaint();
        p.setTextSize(100);

        p.setTextAlign(Paint.Align.CENTER);

        int xPos = (canvas.getWidth() / 2);
        int yPos = 100;

        canvas.drawText(timeInMMSSMI, xPos, yPos, p);

    }
}
