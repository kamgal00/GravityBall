package com.example.gravityball.state;

import org.jbox2d.common.Vec2;

import java.util.ArrayList;

public class BallsParameters {
    public static class BallDescription{
        Vec2 position, velocity, force;

    }
    int number;
    ArrayList<BallDescription> balls;

    private static BallsParameters instance;

    public static synchronized BallsParameters getInstance() {
        return instance;
    }

    public static synchronized void setInstance(BallsParameters newInstance) {
        instance=newInstance;
    }

    public synchronized void setParams(Vec2 position, Vec2 velocity, Vec2 force) {
        balls.get(number).position = position;
        balls.get(number).velocity = velocity;
        balls.get(number).force = force;
    }

    public synchronized void getCopy() {

    }

}
