package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import jkit.gfx.AbstractShapeDrawer;

public class SnowPen extends PenAdapter {

    private final AbstractShapeDrawer origin;

    private final CrayonPen crayon;

    private final double maxSlope;

    private final double segmentLength;

    public SnowPen(final AbstractShapeDrawer origin, final double thickness) {
        this(origin, thickness, Math.PI / 6.0);
    }

    public SnowPen(final AbstractShapeDrawer origin, final double thickness,
            final double maxSlope, final boolean degrees) {
        this(origin, thickness, degrees ? Math.toRadians(maxSlope) : maxSlope,
                10.0);
    }

    public SnowPen(final AbstractShapeDrawer origin, final double thickness,
            final double maxSlope) {
        this(origin, thickness, maxSlope, 10.0);
    }

    public SnowPen(final AbstractShapeDrawer origin, final double thickness,
            final double maxSlope, final double segmentLength) {
        this.origin = origin;
        crayon = new CrayonPen(Color.WHITE, thickness);
        this.maxSlope = maxSlope;
        this.segmentLength = segmentLength;
    }

    public void setThickness(final double thickness) {
        crayon.setThickness(thickness);
    }

    public double getThickness() {
        return crayon.getThickness();
    }

    @Override
    public void prepare(final Graphics2D g, final Shape s) {
        origin.draw(g, s);
        g.translate(0.0, -getThickness() * 2.0 / 3.0);
        crayon.prepare(g, s);
    }

    private boolean isCorrectRotation(double rot) {
        while (rot > Math.PI * 0.5) {
            rot -= Math.PI;
        }
        rot = Math.abs(rot);
        return rot <= maxSlope;

    }

    @Override
    public void start(final Graphics2D g, final double rotation) {
        if (isCorrectRotation(rotation)) {
            crayon.start(g, rotation);
        }
    }

    @Override
    public void end(final Graphics2D g, final double rotation) {
        if (isCorrectRotation(rotation)) {
            crayon.end(g, rotation);
        }
    }

    @Override
    public void draw(final Graphics2D g, final double rotation) {
        if (isCorrectRotation(rotation)) {
            crayon.draw(g, rotation);
        }
    }

    @Override
    public double segmentLength() {
        return segmentLength;
    }

}
