package jkit.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * Draws a shape with the standard method.
 * 
 * @author Joschi <josua.krause@gmail.com>
 * 
 */
public class SimpleShapeDrawer extends AbstractShapeDrawer {

	/** The stroke. */
	private final Stroke stroke;

	/** The paint. */
	private final Paint paint;

	/** Creates a simple shape drawer without a stroke or paint. */
	public SimpleShapeDrawer() {
		this(null, null);
	}

	/**
	 * Creates a simple shape drawer.
	 * 
	 * @param stroke
	 *            The stroke or <code>null</code> if none should be used.
	 * @param paint
	 *            The paint or <code>null</code> if none should be used.
	 */
	public SimpleShapeDrawer(final Stroke stroke, final Paint paint) {
		this.stroke = stroke;
		this.paint = paint;
	}

	@Override
	public void setColor(final Color color) {
		// paint = color;
	}

	@Override
	public void draw(final Graphics gfx, final Shape outline) {
		final Graphics2D g = (Graphics2D) gfx.create();
		if (stroke != null) {
			g.setStroke(stroke);
		}
		if (paint != null) {
			g.setPaint(paint);
		}
		g.draw(outline);
		g.dispose();
	}

	@Override
	public Rectangle2D getBounds(final Shape s) {
		return (stroke != null ? stroke.createStrokedShape(s) : s)
				.getBounds2D();
	}

}
