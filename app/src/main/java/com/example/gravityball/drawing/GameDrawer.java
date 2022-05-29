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
import android.util.Log;
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

    Bitmap ballBitmap, treasureBitmap, endBitmap, enemyBitmap;

    private Paint backgroundPaint;
    private Paint obstaclePaint;
    private Paint wallPaint;
    private Paint otherPlayersPaint;

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

        otherPlayersPaint = new Paint();
        otherPlayersPaint.setColor(Color.BLUE);
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

        canvas.drawColor(backgroundPaint.getColor());

        if(gameWorld.isEnd) {
            canvas.drawBitmap(endBitmap, null, fullScreenRect, null);
            return;
        }

//        getBallScreenPosition();

        drawGameObjects(canvas);

        disco.makeDisco(canvas, fullScreenRect);
    }

    public void update(){
        getBallScreenPosition();
    }

    private void drawGameObjects(Canvas canvas) {
//        canvas.drawBitmap(ballBitmap, ballTransposition, null);
        for(int i=0;i<ballTranspositions.size();i++) {
            if(i!= mainBallId) {
//                float[] values = new float[9];
//                ballTranspositions.get(i).getValues(values);
//                float x = values[Matrix.MTRANS_X];
//                float y = values[Matrix.MTRANS_Y];
//                canvas.drawCircle(x +ballBitmap.getWidth()/2,y+ballBitmap.getHeight()/2, mainBallRadiusInPixels, otherPlayersPaint );
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
}
