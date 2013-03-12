package jkit.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * Draws a shape with the standard method.
 * 
 * @author Joschi <josua.krause@gmail.com>
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
   * @param stroke The stroke or <code>null</code> if none should be used.
   * @param paint The paint or <code>null</code> if none should be used.
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
  public Drawable getDrawable(final Shape s) {
    final Stroke stroke = this.stroke;
    final Paint paint = this.paint;
    final Rectangle2D box = (stroke != null ? stroke.createStrokedShape(s) : s).getBounds2D();
    return new Drawable() {

      @Override
      public void draw(final Graphics2D gfx) {
        final Graphics2D g = (Graphics2D) gfx.create();
        if(stroke != null) {
          g.setStroke(stroke);
        }
        if(paint != null) {
          g.setPaint(paint);
        }
        g.draw(s);
        g.dispose();
      }

      @Override
      protected Rectangle2D computeBounds() {
        return box;
      }

    };
  }

}
