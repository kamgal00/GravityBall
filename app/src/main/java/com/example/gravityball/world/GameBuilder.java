package com.example.gravityball.world;

import android.content.res.Resources;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.gravityball.ResourcesUtils;

import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

public class GameBuilder {

    private GameBuilder(){}

    public static GameWorld buildFromJSON(Resources resources, String name) throws JSONException {
        String json = getJSONFromName(resources,name);
        JSONObject obj = new JSONObject(json);

        GameWorld gameWorld = initializeWorld(obj);

        loadObjects(obj, gameWorld);

        return gameWorld;
    }

    @NonNull
    private static GameWorld initializeWorld(JSONObject obj) throws JSONException {
        double worldWidth = obj.getDouble("worldWidth"),
                worldHeight = obj.getDouble("worldHeight"),
                ballRadius = obj.getDouble("ballRadius");
        Vec2 ballPosition = loadVec2(obj.getJSONArray("ballPosition"));
        GameWorld gameWorld = new GameWorld(
                (float) ballRadius,
                ballPosition,
                (float) worldWidth,
                (float) worldHeight
        );
        return gameWorld;
    }

    private static void loadObjects(JSONObject obj, GameWorld gameWorld) throws JSONException {
        JSONArray walls = obj.getJSONArray("walls");
        for(int i=0;i<walls.length(); i++) {
            Pair<Vec2, Vec2> rectangle = loadRectangle(walls.getJSONArray(i));
            gameWorld.addWall(rectangle.first, rectangle.second);
        }


        JSONArray obstacles = obj.getJSONArray("obstacles");
        for(int i=0;i<obstacles.length(); i++) {
            Pair<Vec2, Vec2> rectangle = loadRectangle(obstacles.getJSONArray(i));
            Vec2 newPos = loadVec2(obstacles.getJSONArray(i).getJSONArray(2));
            gameWorld.addObstacle(rectangle.first, rectangle.second, newPos);
        }

        Pair<Vec2, Vec2> treasureRectangle = loadRectangle(obj.getJSONArray("treasure"));
        gameWorld.addTreasure(treasureRectangle.first, treasureRectangle.second);
    }

    private static Vec2 loadVec2(JSONArray obj) throws JSONException {
        return new Vec2((float) obj.getDouble(0), (float) obj.getDouble(1));
    }
    private static Pair<Vec2, Vec2> loadRectangle(JSONArray obj) throws JSONException {
        return new Pair<>(loadVec2(obj.getJSONArray(0)), loadVec2(obj.getJSONArray(1)));
    }

    private static String getJSONFromName(Resources resources, String name){
        return  ResourcesUtils.getStringFromId(resources, getJsonId(resources, name));
    }

    private static int getJsonId(Resources resources, String name) {
        return resources.getIdentifier(name, "raw", "com.example.gravityball");
    }

}
