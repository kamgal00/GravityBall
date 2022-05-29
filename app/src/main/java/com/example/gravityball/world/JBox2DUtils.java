package com.example.gravityball.world;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class JBox2DUtils {
    public final World world;
    public final float ballRadius;

    public JBox2DUtils(World world, float ballRadius) {
        this.world = world;
        this.ballRadius = ballRadius;
    }

    public Body addRectangle(Vec2 topLeft, Vec2 bottomRight) {
        BodyDef def = new BodyDef();
        def.position.set((topLeft.x+bottomRight.x)/2, (topLeft.y+bottomRight.y)/2);
        Body body = world.createBody(def);

        PolygonShape s = new PolygonShape();
        s.setAsBox((bottomRight.x-topLeft.x)/2, (topLeft.y-bottomRight.y)/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = s;
        fixtureDef.density = 0.0f;
        fixtureDef.filter.categoryBits = 1;
        fixtureDef.filter.maskBits = 2;
        body.createFixture(fixtureDef);

        return body;
    }

    public Body createBall(Vec2 pos) {
        BodyDef def = new BodyDef();
        def.type = BodyType.DYNAMIC;
        def.position.set(pos);
        Body body = world.createBody(def);

        CircleShape s = new CircleShape();
        s.m_p.set(0,0);
        s.m_radius = ballRadius;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = s;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.9f;
        fixtureDef.restitution = 0.3f;
        fixtureDef.filter.categoryBits = 2;
        fixtureDef.filter.maskBits=1;

        body.createFixture(fixtureDef);
        return body;
    }

}
