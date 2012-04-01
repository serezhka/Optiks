package com.ifmo.optiks.base.physics;

import com.badlogic.gdx.math.Vector2;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;

import java.util.ArrayList;

/**
 * User: dududko@gmail.com
 * Date: 29.03.12
 */

public class LaserBeam {

    private ArrayList<Line> beam;
    private final Scene scene;
    private final float RED;
    private final float GREEN;
    private final float BLUE;
    private final float WIDTH;

    public LaserBeam(final Scene scene, final float red, final float green, final float blue, final float width) {
        this.scene = scene;
        RED = red;
        GREEN = green;
        BLUE = blue;
        WIDTH = width;
        beam = new ArrayList<Line>();
    }

    public Line addLine(final float x1, final float y1, final float x2, final float y2) {
        final Line line;
        if (isEmpty()) {
            line = new Line(x1, y1, x2, y2);
        } else {
            line = new Line(beam.get(beam.size() - 1).getX2(), beam.get(beam.size() - 1).getY2(), x2, y2);
        }
        line.setLineWidth(WIDTH);
        line.setColor(RED, GREEN, BLUE, 0.5f);
        beam.add(line);
        scene.attachChild(line);
        return line;
    }

    public Line addLine(final float x2, final float y2) {
        return addLine(0, 0, x2, y2);
    }

    public Line addLine(final Vector2 vec2) {
        return addLine(0, 0, vec2.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, vec2.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
    }

    public ArrayList<Line> getBeam() {
        return beam;
    }

    public void removeFromScene() {
        for (final Line line : beam) {
            scene.detachChild(line);
        }
    }

    public boolean isEmpty() {
        return beam.isEmpty();
    }

    public void resetBeam() {
        removeFromScene();
        beam = new ArrayList<Line>();
    }
}
