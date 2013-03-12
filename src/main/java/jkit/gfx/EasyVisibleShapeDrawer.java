package jkit.gfx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * A shape drawer that makes shapes easily visible.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public class EasyVisibleShapeDrawer extends AbstractShapeDrawer {

  /** The stroke for the inner lines. */
  private final Stroke inner;

  /** The stroke for the outer lines. */
  private final Stroke outer;

  /** The foreground color. */
  private final Color fg;

  /** The background color. */
  private final Color bg;

  /** Creates a black and white shape drawer. */
  public EasyVisibleShapeDrawer() {
    this(Color.WHITE, Color.BLACK, 1, 1);
  }

  /**
   * Creates a shape drawer.
   * 
   * @param fg The front color.
   * @param bg The back color.
   * @param radius The radius of the inner line.
   * @param outerRadius The radius of the outer line.
   */
  public EasyVisibleShapeDrawer(final Color fg, final Color bg,
      final double radius, final double outerRadius) {
    inner = new BasicStroke((float) radius);
    outer = new BasicStroke((float) (radius + outerRadius + 1));
    this.fg = fg;
    this.bg = bg;
  }

  @Override
  public void setColor(final Color color) {
    // FIXME
  }

  @Override
  public Drawable getDrawable(final Shape s) {
    final Color fg = this.fg;
    final Color bg = this.bg;
    final Shape outerShape = outer.createStrokedShape(s);
    final Shape innerShape = inner.createStrokedShape(s);
    final Rectangle2D box = outerShape.getBounds2D();
    return new Drawable() {

      @Override
      public void draw(final Graphics2D gfx) {
        final Graphics2D g = (Graphics2D) gfx.create();
        g.setColor(bg);
        g.fill(outerShape);
        g.setColor(fg);
        g.fill(innerShape);
        g.dispose();
      }

      @Override
      protected Rectangle2D computeBounds() {
        return box;
      }

    };
  }
}
