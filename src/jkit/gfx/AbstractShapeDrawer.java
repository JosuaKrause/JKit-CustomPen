package jkit.gfx;

import java.awt.Graphics;
import java.awt.Shape;
import java.util.Calendar;

import jkit.gfx.pen.Pen;
import jkit.gfx.pen.SnowPen;

public abstract class AbstractShapeDrawer {

    public static boolean beSerious = true;

    public abstract void draw(final Graphics gfx, final Shape outline);

    public static final AbstractShapeDrawer getShapeDrawerForPen(final Pen p) {
        return process(p != null ? new PenShapeDrawer(p)
                : new SimpleShapeDrawer());
    }

    private static AbstractShapeDrawer process(final AbstractShapeDrawer asd) {
        if (beSerious) {
            return asd;
        }
        final Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) == Calendar.DECEMBER ? new PenShapeDrawer(
                new SnowPen(asd, 5.0)) : asd;
    }

}