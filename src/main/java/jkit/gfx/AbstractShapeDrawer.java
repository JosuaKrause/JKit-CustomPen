package jkit.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Calendar;

import jkit.gfx.pen.Pen;
import jkit.gfx.pen.SnowPen;

/**
 * An abstract base class for drawing shapes.
 * 
 * @author Joschi <josua.krause@gmail.com>
 * 
 */
public abstract class AbstractShapeDrawer {

	/** Whether the easter egg may be used. */
	public static boolean beSerious = true;

	/**
	 * Draws the outline of a shape to the given graphics context.
	 * 
	 * @param gfx
	 *            The graphics context.
	 * @param outline
	 *            The shape to draw.
	 */
	public abstract void draw(final Graphics gfx, final Shape outline);

	/**
	 * Computes the bounding box of the given shape for the given shape drawer.
	 * 
	 * @param s
	 *            The shape.
	 * @return The bounding box as if it was drawn by this shape drawer.
	 */
	public abstract Rectangle2D getBounds(Shape s);

	/**
	 * Setter.
	 * 
	 * @param color
	 *            The color for drawing shape outlines.
	 */
	public abstract void setColor(Color color);

	/**
	 * Returns the shape drawer for the given {@link Pen}.
	 * 
	 * @param p
	 *            The pen or <code>null</code> if the standard method of drawing
	 *            shapes should be used.
	 * @return The shape drawer.
	 */
	public static final AbstractShapeDrawer getShapeDrawerForPen(final Pen p) {
		return process(p != null ? new PenShapeDrawer(p)
		: new SimpleShapeDrawer());
	}

	/**
	 * Processes the shape drawer.
	 * 
	 * @param asd
	 *            The shape drawer.
	 * @return The processed shape drawer.
	 */
	private static AbstractShapeDrawer process(final AbstractShapeDrawer asd) {
		if (beSerious) {
			return asd;
		}
		final Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) == Calendar.DECEMBER ? new PenShapeDrawer(
				new SnowPen(asd, 5.0)) : asd;
	}

}