package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import jkit.gfx.AbstractShapeDrawer;

public class SnowPen extends DecoratorPen {

    private final AbstractShapeDrawer origin;

    private final CrayonPen crayon;

    private final double maxSlope;

    public SnowPen(final AbstractShapeDrawer origin, final double thickness) {
        this(origin, thickness, 2.0);
    }

    public SnowPen(final AbstractShapeDrawer origin, final double thickness,
            final double pressure) {
        this(origin, thickness, pressure, Math.PI / 6.0);
    }

    public SnowPen(final AbstractShapeDrawer origin, final double thickness,
            final double pressure, final double maxSlope, final boolean degrees) {
        this(origin, thickness, pressure, degrees ? Math.toRadians(maxSlope)
                : maxSlope, 10.0);
    }

    public SnowPen(final AbstractShapeDrawer origin, final double thickness,
            final double pressure, final double maxSlope) {
        this(origin, thickness, pressure, maxSlope, 10.0);
    }

    public SnowPen(final AbstractShapeDrawer origin, final double thickness,
            final double pressure, final double maxSlope,
            final double segmentLength) {
        super(new CrayonPen(Color.WHITE, thickness, pressure));
        this.origin = origin;
        crayon = (CrayonPen) pen;
        setSegmentLength(segmentLength);
        this.maxSlope = maxSlope;
    }

    public void setSegmentLength(final double segmentLength) {
        crayon.setSegmentLength(segmentLength);
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
            super.start(g, rotation);
        }
    }

    @Override
    public void end(final Graphics2D g, final double rotation) {
        if (isCorrectRotation(rotation)) {
            super.end(g, rotation);
        }
    }

    @Override
    public void draw(final Graphics2D g, final double rotation) {
        if (isCorrectRotation(rotation)) {
            super.draw(g, rotation);
        }
    }

}
