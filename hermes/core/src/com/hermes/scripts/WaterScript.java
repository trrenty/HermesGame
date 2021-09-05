package com.hermes.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import games.rednblack.editor.renderer.components.physics.PhysicsBodyComponent;
import games.rednblack.editor.renderer.physics.PhysicsContact;
import games.rednblack.editor.renderer.scripts.IScript;
import games.rednblack.editor.renderer.utils.ComponentRetriever;

public class WaterScript implements IScript, PhysicsContact {

    private final Array<Fixture> fixturesInWater = new Array<>();
    private Fixture waterFixture;
    private final Array<Float> intersectionPoints = new Array<>();
    private final Vector2 centroid = new Vector2();

    private static final Logger log = new Logger(WaterScript.class.getName(), Logger.DEBUG);
    private Array<Float> clipPolygon = new Array<>();
    private Array<Float> inputList = new Array<>();

    @Override
    public void beginContact(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {
        if (contactFixture.getBody().getType() == BodyDef.BodyType.DynamicBody) {
//            log.debug("added");
            fixturesInWater.add(contactFixture);
        }
    }

    @Override
    public void endContact(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {
        if (contactFixture.getBody().getType() == BodyDef.BodyType.DynamicBody) {
//            log.debug("removed");
            fixturesInWater.removeValue(contactFixture, true);
        }
    }

    @Override
    public void preSolve(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {

    }

    @Override
    public void postSolve(Entity contactEntity, Fixture contactFixture, Fixture ownFixture, Contact contact) {

    }

    @Override
    public void init(Entity entity) {
        PhysicsBodyComponent pc = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        waterFixture = pc.body.getFixtureList().first();
    }

    @Override
    public void act(float delta) {
//        log.debug("Ads" );
        for (Fixture fixture : fixturesInWater) {
            float waterDensity = waterFixture.getDensity();
            float bodyDensity = fixture.getDensity();

            intersectionPoints.clear();
            if (findIntersectionOfFixtures(fixture, intersectionPoints)) {
                computeCentroid(intersectionPoints, 0, intersectionPoints.size, centroid);
                float area = computeArea(intersectionPoints, 0, intersectionPoints.size);
                float displacedMass = (waterDensity / bodyDensity) * area;
//                log.debug("area: " + area + " centroid: " + displacedMass);

                fixture.getBody().applyForce(0, 9.81f * displacedMass, centroid.x, centroid.y, true);

                Vector2 velDir = fixture.getBody().getLinearVelocityFromWorldPoint(centroid).sub(waterFixture.getBody().getLinearVelocityFromWorldPoint( centroid ));
                float vel = velDir.nor().len2();

                //apply simple linear drag
                float dragMag = waterDensity * vel * vel;
                Vector2 dragForce = velDir.scl(-dragMag);
                fixture.getBody().applyForce( dragForce, centroid , true);
            }
        }
    }

    @Override
    public void dispose() {

    }

    private boolean findIntersectionOfFixtures(Fixture contactFixture, Array<Float> outputVertices) {
        clipPolygon.clear();
        inputList.clear();

        if (contactFixture.getType() != Shape.Type.Polygon) {
            return false;
        }

        PolygonShape contactPoly = (PolygonShape)contactFixture.getShape();
        PolygonShape waterPoly = (PolygonShape)waterFixture.getShape();


        for (int i = 0; i < waterPoly.getVertexCount(); i++) {
            waterPoly.getVertex(i, centroid);
            Vector2 tmp = waterFixture.getBody().getWorldPoint(centroid);
            outputVertices.add(tmp.x);
            outputVertices.add(tmp.y);
        }

        for (int i = 0; i < contactPoly.getVertexCount(); i++) {
            contactPoly.getVertex(i, centroid);
            Vector2 tmp = contactFixture.getBody().getWorldPoint(centroid);
            clipPolygon.add(tmp.x);
            clipPolygon.add(tmp.y);

        }

        float cp1x = clipPolygon.get(clipPolygon.size - 2);
        float cp1y = clipPolygon.peek();
        for (int i = 0; i < clipPolygon.size; i+=2) {

            float cp2x = clipPolygon.get(i);
            float cp2y = clipPolygon.get(i+1);
            if (outputVertices.isEmpty()) {
                return false;
            }
            inputList.clear();
            inputList.addAll(outputVertices);
            outputVertices.clear();
            float sy = inputList.peek();
            float sx = inputList.get(inputList.size -2);
            for (int j = 0; j < inputList.size; j+=2) {
                float ex = inputList.get(j);
                float ey = inputList.get(j+1);

                if (inside(cp1x, cp1y, cp2x, cp2y, ex, ey)) {
                    if (!inside(cp1x, cp1y, cp2x, cp2y, sx, sy)) {
                        intersection(cp1x, cp1y, cp2x, cp2y, sx, sy, ex, ey, centroid);
                        outputVertices.add(centroid.x);
                        outputVertices.add(centroid.y);
                    }
                    outputVertices.add(ex);
                    outputVertices.add(ey);
                } else if (inside(cp1x, cp1y, cp2x, cp2y, sx, sy)) {
                    intersection(cp1x, cp1y,cp2x, cp2y, sx, sy, ex, ey, centroid);
                    outputVertices.add(centroid.x);
                    outputVertices.add(centroid.y);
                }
                sx = ex;
                sy = ey;
            }
            cp1x = cp2x;
            cp1y = cp2y;
        }

        return !outputVertices.isEmpty();
    }

    private boolean inside(float cp1x, float cp1y, float cp2x, float cp2y, float px, float py) {
        return (cp2x-cp1x)*(py-cp1y) > (cp2y-cp1y)*(px-cp1x);
    }

    private void intersection(float cp1x, float cp1y, float cp2x, float cp2y, float sx, float sy, float ex, float ey, Vector2 result) {
        float dcx =  cp1x - cp2x;
        float dcy = cp1y - cp2y;

        float dpx = sx - ex;
        float dpy = sy - ey;

        float n1 = cp1x * cp2y - cp1y * cp2x;
        float n2 = sx * ey - sy * ex;
        float n3 = 1f / (dcx * dpy - dcy * dpx);
        result.set((n1 * dpx - n2 * dcx) * n3, (n1 * dpy - n2 * dcy) * n3);
    }

    static public Vector2 computeCentroid (Array<Float> polygon, int offset, int count, Vector2 centroid) {
        if (count < 6) throw new IllegalArgumentException("A polygon must have 3 or more coordinate pairs.");

        float area = 0, x = 0, y = 0;
        int last = offset + count - 2;
        float x1 = polygon.get(last), y1 = polygon.get(last + 1);
        for (int i = offset; i <= last; i += 2) {
            float x2 = polygon.get(i), y2 = polygon.get(i + 1);
            float a = x1 * y2 - x2 * y1;
            area += a;
            x += (x1 + x2) * a;
            y += (y1 + y2) * a;
            x1 = x2;
            y1 = y2;
        }
        if (area == 0) {
            centroid.x = 0;
            centroid.y = 0;
        } else {
            area *= 0.5f;
            centroid.x = x / (6 * area);
            centroid.y = y / (6 * area);
        }
        return centroid;
    }

    static public float computeArea (Array<Float> polygon, int offset, int count) {
        float area = 0;
        int last = offset + count - 2;
        float x1 = polygon.get(last), y1 = polygon.get(last + 1);
        for (int i = offset; i <= last; i += 2) {
            float x2 = polygon.get(i), y2 = polygon.get(i + 1);
            area += x1 * y2 - x2 * y1;
            x1 = x2;
            y1 = y2;
        }
        return area * 0.5f;
    }

}
