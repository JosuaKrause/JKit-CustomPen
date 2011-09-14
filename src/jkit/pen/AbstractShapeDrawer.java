package jkit.pen;

import java.awt.Graphics;
import java.awt.Shape;

public abstract class AbstractShapeDrawer {

	public abstract void draw(final Graphics gfx, final Shape outline);

	public static final AbstractShapeDrawer getShapeDrawerForPen(final Pen p) {
		return p != null ? new PenShapeDrawer(p) : new SimpleShapeDrawer();
	}

}