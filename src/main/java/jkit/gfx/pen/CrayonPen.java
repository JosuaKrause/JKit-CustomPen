package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A pen with a crayon pen style.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public class CrayonPen extends CachedRandomPen {

  /** The thickness of the crayon line. */
  private double thickness;

  /** The pressure of the crayon line. */
  private double pressure;

  /**
   * Creates a crayon pen with the given color and thickness.
   * 
   * @param color The color.
   * @param thickness The thickness.
   */
  public CrayonPen(final Color color, final double thickness) {
    this(color, thickness, 2.0);
  }

  /**
   * Creates a crayon pen with the given color, thickness, and pressure.
   * 
   * @param color The color.
   * @param thickness The thickness.
   * @param pressure The pressure.
   */
  public CrayonPen(final Color color, final double thickness,
      final double pressure) {
    super(color);
    this.thickness = thickness;
    this.pressure = pressure;
  }

  /**
   * Setter.
   * 
   * @param pressure The pressure.
   */
  public void setPressure(final double pressure) {
    this.pressure = pressure;
    invalidate();
  }

  /**
   * Getter.
   * 
   * @return The pressure.
   */
  public double getPressure() {
    return pressure;
  }

  /**
   * Setter.
   * 
   * @param thickness The thickness.
   */
  public void setThickness(final double thickness) {
    this.thickness = thickness;
    invalidate();
  }

  /**
   * Getter.
   * 
   * @return The thickness.
   */
  public double getThickness() {
    return thickness;
  }

  @Override
  public void prepare(final Graphics2D g, final Shape s) {
    super.prepare(g, s);
    g.setStroke(new BasicStroke(1f));
    postPrepare(g);
  }

  /** The cached bounding-box. */
  private Rectangle2D bbox;

  @Override
  protected void invalidate() {
    super.invalidate();
    bbox = null;
  }

  @Override
  protected void drawSegment(final Graphics2D g) {
    final int t = (int) Math.round(thickness * pressure);
    final double ht = thickness * 0.5;
    for(double pos = 0.0; pos <= segmentLength + 2.0; pos += 1.0) {
      for(int i = 0; i < t; ++i) {
        final double h = rndNextDouble() * thickness;
        final Shape s = new Rectangle2D.Double(pos - 0.5, h - ht - 0.5,
            1.0, 1.0);
        g.fill(s);
      }
    }
  }

  @Override
  public Rectangle2D getBoundingBox(final int type, final double rotation) {
    if(bbox == null) {
      final double posMin = 0.0;
      final double posMax = segmentLength + 2.0;
      final double ht = thickness * 0.5;
      final double minH = 0;
      final double maxH = thickness;
      final double left = posMin - 0.5;
      final double right = posMax + 0.5;
      final double top = minH - ht - 0.5;
      final double bottom = maxH - ht + 0.5;
      final Shape s = new Rectangle2D.Double(left, top, right - left,
          bottom - top);
      bbox = getBounds(s);
    }
    return bbox;
  }

}
