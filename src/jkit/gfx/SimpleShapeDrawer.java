package jkit.gfx;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

public class SimpleShapeDrawer extends AbstractShapeDrawer {

	@Override
	public void draw(final Graphics gfx, final Shape outline) {
		((Graphics2D) gfx).draw(outline);
	}

}
