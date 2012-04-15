package com.ifmo.optiks.base.physics;

import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;

import java.util.LinkedList;
import java.util.List;

/**
 * User: dududko@gmail.com
 * Date: 29.03.12
 */

public class LaserBeam {

    private List<Line> beam = new LinkedList<Line>();
    private final Scene scene;
    private final Color color;
    private final float WIDTH = 3;
    private final float fromX;
    private final float fromY;


    public LaserBeam(final Scene scene, final Color color, final float fromX, final float fromY) {
        this.scene = scene;
        this.color = color;
        this.fromX = fromX;
        this.fromY = fromY;


    }

    public Line addLine(final float x2, final float y2) {
        final Line line;
        if (beam.isEmpty()) {
            line = new Line(fromX, fromY, x2, y2);
        } else {
            line = new Line(beam.get(beam.size() - 1).getX2(), beam.get(beam.size() - 1).getY2(), x2, y2);
        }
        line.setLineWidth(WIDTH);
        line.setColor(color.RED, color.GREEN, color.BLUE, 0.5f);
        beam.add(line);
        scene.attachChild(line);
        return line;
    }


    private void removeFromScene() {
        for (final Line line : beam) {
            scene.detachChild(line);
        }
    }

    public void resetBeam() {
        removeFromScene();
        beam.clear();
    }

    public static class Color {
        private final float RED;
        private final float GREEN;
        private final float BLUE;
        private final float ALPHA;

        public Color(final float RED, final float GREEN, final float BLUE, final float alpha) {
            this.RED = RED;
            this.GREEN = GREEN;
            this.BLUE = BLUE;
            this.ALPHA = alpha;
        }
    }
}
