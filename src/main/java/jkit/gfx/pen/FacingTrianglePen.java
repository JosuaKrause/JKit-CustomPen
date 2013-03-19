package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * A pen drawing facing triangles.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public class FacingTrianglePen extends SimplePen {

  /** The triangle shape. */
  private Shape triangle;

  /** The width of the triangles. */
  private double width;

  /** The distance of the triangles from each other. */
  private double distance;

  /** Creates a default triangle pen. */
  public FacingTrianglePen() {
    this(Color.BLACK, 30.0);
  }

  /**
   * Creates a facing triangle pen with a color and segment length.
   * 
   * @param color The color.
   * @param segLen The segment length.
   */
  public FacingTrianglePen(final Color color, final double segLen) {
    super(color, segLen);
  }

  @Override
  public void setSegmentLength(final double segLen) {
    super.setSegmentLength(segLen);
    width = segLen * 0.5;
    distance = segLen * 0.5;
    triangle = createTriangleFor(width);
  }

  /**
   * Creates a triangular shape with the given width.
   * 
   * @param width The width.
   * @return The shape.
   */
  private static Shape createTriangleFor(final double width) {
    final Path2D path = new Path2D.Double();
    path.moveTo(0.0, 0.0);
    path.lineTo(width, 0.0);
    path.lineTo(width * 0.5, width * 0.5);
    path.closePath();
    return path;
  }

  @Override
  public void draw(final Graphics2D gfx, final int no, final double rotation) {
    final Graphics2D g = (Graphics2D) gfx.create();
    g.translate(width, -distance);
    g.fill(triangle);
    g.translate(0, distance * 2.0);
    final AffineTransform at = AffineTransform.getQuadrantRotateInstance(2);
    at.translate(-width, 0.0);
    g.transform(at);
    g.fill(triangle);
    g.dispose();
    gfx.setColor(Color.BLACK);
    gfx.translate(width, -distance);
    gfx.draw(triangle);
    gfx.translate(0, distance * 2.0);
    gfx.transform(at);
    gfx.draw(triangle);
  }

  @Override
  public Rectangle2D getBoundingBox(final int type, final double rotation) {
    throw new UnsupportedOperationException();
  }

}
