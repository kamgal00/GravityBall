package com.example.gravityball.world;

import android.util.Pair;

import androidx.annotation.NonNull;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.ArrayList;
import java.util.HashMap;

public class GameWorld {
    public final World world;
    public final JBox2DUtils worldUtils;
    public Body mainBall, treasure;

    public final ArrayList<Pair<Vec2, Vec2>> walls = new ArrayList<>();
    public final ArrayList<Pair<Vec2, Vec2>> obstacles = new ArrayList<>();
    public Pair<Vec2, Vec2> treasurePosition;
    public final HashMap<Body, Vec2> obstaclesToTeleports = new HashMap<>();

    private Vec2 teleport = null;

    public boolean isEnd = false;

    public final float worldWidth, worldHeight;
    public final float ballRadius;
    public final int velocityIterations = 4;
    public final int positionIterations = 1;

    public GameWorld(float ballRadius, Vec2 initialBallPosition, float worldWidth, float worldHeight) {
        this.ballRadius = ballRadius;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        world = initializeWorld();
        worldUtils = new JBox2DUtils(world, ballRadius);

        initializeBorders(worldWidth, worldHeight);

        mainBall = worldUtils.createBall(initialBallPosition);
    }

    @NonNull
    private World initializeWorld() {
        final World world;
        Vec2 gravity = new Vec2(0, 0);
        world = new World(gravity);
        world.setContactListener(new WorldContactListener());
        return world;
    }

    private void initializeBorders(float worldWidth, float worldHeight) {
        addWall(new Vec2(-1, 1+ worldHeight), new Vec2(1+ worldWidth, worldHeight));
        addWall(new Vec2(-1,0), new Vec2(1+ worldWidth,-1));
        addWall(new Vec2(-1, worldHeight), new Vec2(0,0));
        addWall(new Vec2(worldWidth, worldHeight), new Vec2(1+ worldWidth,0));
    }

    public void addTreasure(Vec2 topLeft, Vec2 bottomRight) {
        if(treasure != null) world.destroyBody(treasure);
        treasure = worldUtils.addRectangle(topLeft, bottomRight);
        treasurePosition = new Pair<>(topLeft, bottomRight);
    }

    public void addWall(Vec2 topLeft, Vec2 bottomRight) {
        worldUtils.addRectangle(topLeft, bottomRight);
        walls.add(new Pair<>(topLeft, bottomRight));
    }

    public void addObstacle(Vec2 topLeft, Vec2 bottomRight, Vec2 newPos) {
        Body b = worldUtils.addRectangle(topLeft, bottomRight);
        obstacles.add(new Pair<>(topLeft, bottomRight));
        obstaclesToTeleports.put(b, newPos);
    }

    public void step(Vec2 ballForce, float time) {
        applyTeleportation();
        mainBall.applyForceToCenter(ballForce.mul(mainBall.getMass()));
        world.step(time, velocityIterations, positionIterations);
    }

    private void applyTeleportation() {
        if(teleport != null) {
            world.destroyBody(mainBall);
            mainBall = worldUtils.createBall(teleport);
            teleport=null;
        }
    }

    private class WorldContactListener implements ContactListener {

        @Override
        public void beginContact(Contact contact) {
            if(ballTouchedObstacle(contact)) {
                teleport = obstaclesToTeleports.get(contact.m_fixtureA.m_body);
            }
            if(ballTouchedTreasure(contact)) {
                isEnd = true;
            }
        }

        private boolean ballTouchedTreasure(Contact contact) {
            return contact.m_fixtureB.m_body == mainBall
                            && contact.m_fixtureA.m_body == treasure;
        }

        private boolean ballTouchedObstacle(Contact contact) {
            return contact.m_fixtureB.m_body == mainBall
                            &&  obstaclesToTeleports.containsKey(contact.m_fixtureA.m_body);
        }

        @Override
        public void endContact(Contact contact) {

        }

        @Override
        public void preSolve(Contact contact, Manifold manifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse contactImpulse) {

        }
    }
}
