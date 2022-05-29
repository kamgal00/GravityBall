package com.example.gravityball.world;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.gravityball.networking.Network;

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
import java.util.Arrays;
import java.util.HashMap;

public class GameWorld {
    public final World world;
    public final JBox2DUtils worldUtils;
//    public Body mainBall, treasure;
    public Body treasure;
    public final ArrayList<Body> balls;
    public ArrayList<Vec2> forces, positions, velocities;
    public ArrayList<Float> angles, angularVelocities;

    public final ArrayList<Pair<Vec2, Vec2>> walls = new ArrayList<>();
    public final ArrayList<Pair<Vec2, Vec2>> obstacles = new ArrayList<>();
    public Pair<Vec2, Vec2> treasurePosition;
    public final HashMap<Body, Vec2> obstaclesToTeleports = new HashMap<>();

    private HashMap<Body, Vec2> teleports = new HashMap<>();
    private final boolean triggersEnabled;

    public boolean isEnd = false;
    public int winnerBall=-1;

    public final float worldWidth, worldHeight;
    public final float ballRadius;
    public final int velocityIterations = 4;
    public final int positionIterations = 1;

    public GameWorld(float ballRadius, Vec2 initialBallPosition, float worldWidth, float worldHeight, int ballsNumber, boolean triggers) {
        this.ballRadius = ballRadius;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        triggersEnabled = triggers;

        world = initializeWorld();
        worldUtils = new JBox2DUtils(world, ballRadius);

        initializeBorders(worldWidth, worldHeight);

        balls = new ArrayList<>(Arrays.asList(new Body[ballsNumber]));
        forces = new ArrayList<>(Arrays.asList(new Vec2[ballsNumber]));
        velocities = new ArrayList<>(Arrays.asList(new Vec2[ballsNumber]));
        positions = new ArrayList<>(Arrays.asList(new Vec2[ballsNumber]));
        angles = new ArrayList<>(Arrays.asList(new Float[ballsNumber]));
        angularVelocities = new ArrayList<>(Arrays.asList(new Float[ballsNumber]));

        for(int i=0;i<ballsNumber;i++) {
            balls.set(i, worldUtils.createBall(initialBallPosition));
            forces.set(i, new Vec2(0,0));
            velocities.set(i, new Vec2(0,0));
            positions.set(i, initialBallPosition);
            angles.set(i, 0f);
            angularVelocities.set(i, 0f);

            teleports.put(balls.get(i), null);
        }


//        mainBall = worldUtils.createBall(initialBallPosition);
    }

    @NonNull
    private World initializeWorld() {
        final World world;
        Vec2 gravity = new Vec2(0, 0);
        world = new World(gravity);
        if(triggersEnabled) world.setContactListener(new WorldContactListener());
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

    public void step(float time) {
        applyTeleportation();
        for(int i=0;i<balls.size();i++) {
            balls.get(i).applyForceToCenter(forces.get(i).mul(balls.get(i).getMass()).mul(0.1f));
        }
//        mainBall.applyForceToCenter(ballForce.mul(mainBall.getMass()).mul(0.1f));
        world.step(time, velocityIterations, positionIterations);
        loadParams();
    }

    private void applyTeleportation() {
        for(int i = 0; i<balls.size();i++) {
            if(teleports.get(balls.get(i)) != null) {
                Vec2 teleport = teleports.get(balls.get(i));
                teleports.remove(balls.get(i));
                world.destroyBody(balls.get(i));
                balls.set(i, worldUtils.createBall(teleport));
                teleports.put(balls.get(i), null);
            }
        }

    }

    public void adjust(Network.ServerUpdate u) {
        for(int i=0;i<balls.size();i++) {
            balls.get(i).setTransform(u.positions.get(i), u.angles.get(i));
            balls.get(i).setLinearVelocity(u.velocities.get(i));
            balls.get(i).setAngularVelocity(u.angles.get(i));
            balls.get(i).setAngularVelocity(u.angularVelocities.get(i));
        }
    }

    public void loadParams(){
        for(int i=0;i<balls.size();i++) {
            Body ball = balls.get(i);
            positions.set(i, ball.getPosition());
            velocities.set(i, ball.getLinearVelocity());
            angles.set(i, ball.getAngle());
            angularVelocities.set(i, ball.getAngularVelocity());
        }
    }
    public Network.ServerUpdate createServerUpdate(){
        Network.ServerUpdate out = new Network.ServerUpdate();
        out.angles = angles;
        out.forces = forces;
        out.angularVelocities = angularVelocities;
        out.positions = positions;
        out.velocities = velocities;
        return out;
    }

    private class WorldContactListener implements ContactListener {

        @Override
        public void beginContact(Contact contact) {
            if(ballTouchedObstacle(contact)) {
                teleports.put(
                        contact.m_fixtureB.m_body,
                        obstaclesToTeleports.get(contact.m_fixtureA.m_body)
                );
            }
            if(ballTouchedTreasure(contact)) {
                isEnd = true;
                winnerBall = balls.indexOf(contact.m_fixtureB.m_body);
            }
        }

        private boolean ballTouchedTreasure(Contact contact) {
            return balls.contains(contact.m_fixtureB.m_body)
                            && contact.m_fixtureA.m_body == treasure;
        }

        private boolean ballTouchedObstacle(Contact contact) {
            return  balls.contains(contact.m_fixtureB.m_body)
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
