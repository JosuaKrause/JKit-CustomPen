package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class CrayonPen extends SimplePen {

    private final Random rnd = new Random();

    private double thickness;

    public CrayonPen(final Color color, final double thickness) {
        super(color);
        this.thickness = thickness;
    }

    public void setThickness(final double thickness) {
        this.thickness = thickness;
    }

    public double getThickness() {
        return thickness;
    }

    @Override
    public void prepare(final Graphics2D g, final Shape s) {
        super.prepare(g, s);
        g.setStroke(new BasicStroke(1f));
        rnd.setSeed(s.getBounds2D().hashCode());
    }

    @Override
    public void draw(final Graphics2D g, final double rotation) {
        final int t = (int) Math.round(thickness) * 2;
        final double ht = thickness * 0.5;
        for (double pos = 0.0; pos <= segmentLength + 2.0; pos += 1.0) {
            for (int i = 0; i < t; ++i) {
                final double h = rnd.nextDouble() * thickness;
                final Shape s = new Rectangle2D.Double(pos - 0.5, h - ht - 0.5,
                        1.0, 1.0);
                g.fill(s);
            }
        }
    }

}
