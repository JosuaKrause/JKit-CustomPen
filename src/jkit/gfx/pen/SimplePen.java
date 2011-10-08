package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

public abstract class SimplePen extends PenAdapter {

    protected double segmentLength;

    protected Color color;

    private boolean initialized;

    public SimplePen(final Color color) {
        this(color, 10.0);
    }

    public SimplePen(final Color color, final double segmentLength) {
        this.color = color;
        this.segmentLength = segmentLength;
        initialized = false;
    }

    @Override
    public void prepare(final Graphics2D g, final Shape s) {
        if (!initialized) {
            setColor(color);
            setSegmentLength(segmentLength);
            initialized = true;
        }
        if (color != null) {
            g.setColor(color);
        }
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    public Color color() {
        return color;
    }

    public void setSegmentLength(final double segmentLength) {
        this.segmentLength = segmentLength;
    }

    @Override
    public double segmentLength() {
        return segmentLength;
    }

}
