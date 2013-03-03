package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class PencilPen extends CachedRandomPen {

    protected double metricShiftX = 0.5;

    protected double metricShiftY = 0;

    protected double metricSpreadX = 0.5;

    protected double metricSpreadY = 0.125;

    protected double metricMaxLength = 1.125;

    protected int count = 25;

    public PencilPen() {
        this(10.0);
    }

    public PencilPen(final double segmentLength) {
        super(new Color(0x40303030, true), segmentLength);
    }

    public PencilPen(final Color color, final double segmentLength) {
        super(new Color(color.getRGB() | 0x40000000, true), segmentLength);
    }

    @Override
    public void prepare(final Graphics2D g, final Shape s) {
        super.prepare(g, s);
        g.setStroke(new BasicStroke(.5f));
        postPrepare(g);
    }

    private double dx;

    private double dy;

    private double lx;

    private double ly;

    private double ll;

    @Override
    public void setSegmentLength(final double segmentLength) {
        super.setSegmentLength(segmentLength);
        lx = segmentLength * metricSpreadX;
        ly = segmentLength * metricSpreadY;
        dx = segmentLength * metricShiftX;
        dy = segmentLength * metricShiftY;
        ll = segmentLength * metricMaxLength;
        invalidate();
    }

    protected final double getNextX() {
        return rndNextGaussian() * lx + dx;
    }

    protected final double getNextY() {
        return rndNextGaussian() * ly + dy;
    }

    protected final double getNextLine() {
        return rndNextGaussian() * ll;
    }

    private static final double GAUSS_NULL = 4;

    protected final double getMinX() {
        return -GAUSS_NULL * lx + dx;
    }

    protected final double getMinY() {
        return -GAUSS_NULL * ly + dy;
    }

    protected final double getMinLine() {
        return -GAUSS_NULL * ll;
    }

    protected final double getMaxX() {
        return GAUSS_NULL * lx + dx;
    }

    protected final double getMaxY() {
        return GAUSS_NULL * ly + dy;
    }

    protected final double getMaxLine() {
        return GAUSS_NULL * ll;
    }

    @Override
    protected void drawSegment(final Graphics2D g) {
        for (int i = 0; i < count; ++i) {
            final double x = getNextX();
            final double y = getNextY();
            final double len = getNextLine();
            g.draw(new Line2D.Double(x, y, x + len, y));
        }
    }

    private Rectangle2D bbox;

    @Override
    protected void invalidate() {
        super.invalidate();
        bbox = null;
    }

    @Override
    public Rectangle2D getBoundingBox(final int type, final double rotation) {
        if (bbox == null) {
            final double left = getMinX() + getMinLine();
            final double right = getMaxX() + getMaxLine();
            final double top = getMinY();
            final double bottom = getMaxY();
            final Rectangle2D r = new Rectangle2D.Double(left, top, right - left,
                    bottom - top);
            bbox = getBounds(r);
        }
        return bbox;
    }

}
