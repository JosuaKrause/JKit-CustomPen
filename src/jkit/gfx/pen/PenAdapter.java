package jkit.gfx.pen;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public abstract class PenAdapter implements Pen {

	@Override
	public void start(final Graphics2D g, final int no, final double rotation) {
		draw(g, no, rotation);
	}

	@Override
	public void end(final Graphics2D g, final int no, final double rotation) {
		draw(g, no, rotation);
	}

	@Override
	public Rectangle2D getSpecialBounds(final Shape s) {
		return null;
	}

}
