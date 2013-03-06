package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * A pen drawing the segments of a shape as a line.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public class LinePen extends SimplePen {

  /** The stroke used for the lines. */
  private final Stroke stroke;

  /** Creates a line pen without a stroke. */
  public LinePen() {
    this(null);
  }

  /**
   * Creates a line pen without a color.
   * 
   * @param stroke The stroke or <code>null</code> for no stroke.
   */
  public LinePen(final Stroke stroke) {
    this(stroke, null);
  }

  /**
   * Creates a line pen with the default segment length.
   * 
   * @param stroke The stroke or <code>null</code> for no stroke.
   * @param color The color or <code>null</code> for no color.
   */
  public LinePen(final Stroke stroke, final Color color) {
    this(stroke, color, 10.0);
  }

  /**
   * Creates a line pen.
   * 
   * @param stroke The stroke or <code>null</code> for no stroke.
   * @param color The color or <code>null</code> for no color.
   * @param segmentLength The segment length.
   */
  public LinePen(final Stroke stroke,
      final Color color, final double segmentLength) {
    super(color, segmentLength);
    this.stroke = stroke;
  }

  @Override
  public void prepare(final Graphics2D g, final Shape s) {
    if(stroke != null) {
      g.setStroke(stroke);
    }
    super.prepare(g, s);
  }

  @Override
  public void draw(final Graphics2D g, final int no, final double rotation) {
    g.draw(new Line2D.Double(0, 0, segmentLength, 0));
  }

  @Override
  public Rectangle2D getBoundingBox(final int type, final double rotation) {
    final Shape s = new Line2D.Double(0, 0, segmentLength, 0);
    return getBounds(s);
  }

}
