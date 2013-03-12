package jkit.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Calendar;

import jkit.gfx.pen.Pen;
import jkit.gfx.pen.SnowPen;

/**
 * An abstract base class for drawing shapes.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public abstract class AbstractShapeDrawer {

  /** Whether the easter egg may be used. */
  public static boolean beSerious = true;

  /**
   * Generates a {@link Drawable} using this shape drawer.
   * 
   * @param s The shape.
   * @return The {@link Drawable} for the given shape.
   */
  public abstract Drawable getDrawable(Shape s);

  /**
   * Setter.
   * 
   * @param color The color for drawing shape outlines.
   */
  public abstract void setColor(Color color);

  /**
   * Draws the given shape.
   * 
   * @param gfx The graphics context.
   * @param s The shape.
   * @deprecated Use {@link #getDrawable(Shape)} and
   *             {@link Drawable#drawIfVisible(Graphics2D, java.awt.geom.Rectangle2D)}
   *             instead.
   */
  @Deprecated
  public void draw(final Graphics2D gfx, final Shape s) {
    getDrawable(s).draw(gfx);
  }

  /**
   * Returns the shape drawer for the given {@link Pen}.
   * 
   * @param p The pen or <code>null</code> if the standard method of drawing
   *          shapes should be used.
   * @return The shape drawer.
   */
  public static final AbstractShapeDrawer getShapeDrawerForPen(final Pen p) {
    return process(p != null ? new PenShapeDrawer(p)
        : new SimpleShapeDrawer());
  }

  /**
   * Processes the shape drawer.
   * 
   * @param asd The shape drawer.
   * @return The processed shape drawer.
   */
  private static AbstractShapeDrawer process(final AbstractShapeDrawer asd) {
    if(beSerious) return asd;
    final Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.MONTH) == Calendar.DECEMBER ? new PenShapeDrawer(
        new SnowPen(asd, 5.0)) : asd;
  }

}
