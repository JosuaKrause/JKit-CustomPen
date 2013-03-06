package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import jkit.gfx.AbstractShapeDrawer;

/**
 * A decorator pen for adding a snow effect.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public class SnowPen extends DecoratorPen {

  /** The original shape drawer. */
  private final AbstractShapeDrawer origin;

  /** The snow crayon. */
  private final CrayonPen crayon;

  /** The maximal slope. */
  private final double maxSlope;

  /** The maximal thickness. */
  private double maxThickness;

  /**
   * Creates a snow pen.
   * 
   * @param origin The shape drawer to add the snow effect to.
   * @param thickness The thickness of the snow.
   */
  public SnowPen(final AbstractShapeDrawer origin, final double thickness) {
    this(origin, thickness, 2.0);
  }

  /**
   * Creates a snow pen.
   * 
   * @param origin The shape drawer to add the snow effect to.
   * @param thickness The thickness of the snow.
   * @param pressure The pressure used to draw the snow.
   */
  public SnowPen(final AbstractShapeDrawer origin, final double thickness,
      final double pressure) {
    this(origin, thickness, pressure, Math.PI / 6.0);
  }

  /**
   * Creates a snow pen.
   * 
   * @param origin The shape drawer to add the snow effect to.
   * @param thickness The thickness of the snow.
   * @param pressure The pressure used to draw the snow.
   * @param maxSlope The maximal slope to add snow to.
   * @param degrees Whether the slope is given in degrees.
   */
  public SnowPen(final AbstractShapeDrawer origin, final double thickness,
      final double pressure, final double maxSlope, final boolean degrees) {
    this(origin, thickness, pressure, degrees ? Math.toRadians(maxSlope)
        : maxSlope, 10.0);
  }

  /**
   * Creates a snow pen.
   * 
   * @param origin The shape drawer to add the snow effect to.
   * @param thickness The thickness of the snow.
   * @param pressure The pressure used to draw the snow.
   * @param maxSlope The maximal slope to add snow to in radians.
   */
  public SnowPen(final AbstractShapeDrawer origin, final double thickness,
      final double pressure, final double maxSlope) {
    this(origin, thickness, pressure, maxSlope, 10.0);
  }

  /**
   * Creates a snow pen.
   * 
   * @param origin The shape drawer to add the snow effect to.
   * @param thickness The thickness of the snow.
   * @param pressure The pressure used to draw the snow.
   * @param maxSlope The maximal slope to add snow to in radians.
   * @param segmentLength The segment length of the snow.
   */
  public SnowPen(final AbstractShapeDrawer origin, final double thickness,
      final double pressure, final double maxSlope,
      final double segmentLength) {
    super(new CrayonPen(Color.WHITE, thickness, pressure));
    this.origin = origin;
    crayon = (CrayonPen) pen;
    setSegmentLength(segmentLength);
    setThickness(thickness);
    this.maxSlope = maxSlope;
  }

  /**
   * Setter.
   * 
   * @param segmentLength Sets the segment length of the snow.
   */
  public void setSegmentLength(final double segmentLength) {
    crayon.setSegmentLength(segmentLength);
  }

  /**
   * Setter.
   * 
   * @param thickness Sets the thickness of the snow.
   */
  public void setThickness(final double thickness) {
    maxThickness = thickness;
  }

  /**
   * Getter.
   * 
   * @return The thickness of the snow.
   */
  public double getThickness() {
    return maxThickness;
  }

  @Override
  public void prepare(final Graphics2D g, final Shape s) {
    origin.draw(g, s);
    g.translate(0.0, -getThickness() * 2.0 / 3.0);
    crayon.prepare(g, s);
  }

  @Override
  public Rectangle2D getSpecialBounds(final Shape s) {
    return origin.getBounds(s);
  }

  /** The standard maximal slope. */
  private static final double MAX_SLOPE = Math.PI * 0.5;

  /** The standard slope step. */
  private static final double SLOPE_STEP = MAX_SLOPE * 2;

  /**
   * Computes the slope of a rotation.
   * 
   * @param rot The rotation.
   * @return The slope.
   */
  private static double slope(final double rot) {
    double r = rot;
    while(Math.abs(r) > MAX_SLOPE) {
      r -= SLOPE_STEP;
    }
    return Math.abs(r);
  }

  /**
   * Adjusts the thickness for the given rotation.
   * 
   * @param rot The rotation.
   */
  private void adjustThickness(final double rot) {
    final double factor = slope(rot) / MAX_SLOPE;
    crayon.setThickness(maxThickness * factor);
  }

  /**
   * Whether the rotation should have snow.
   * 
   * @param rot The rotation.
   * @return Whether it should have snow.
   */
  private boolean isCorrectRotation(final double rot) {
    return slope(rot) <= maxSlope;
  }

  @Override
  public void start(final Graphics2D g, final int no, final double rotation) {
    if(isCorrectRotation(rotation)) {
      adjustThickness(rotation);
      super.start(g, no, rotation);
    }
  }

  @Override
  public void end(final Graphics2D g, final int no, final double rotation) {
    if(isCorrectRotation(rotation)) {
      adjustThickness(rotation);
      super.end(g, no, rotation);
    }
  }

  @Override
  public void draw(final Graphics2D g, final int no, final double rotation) {
    if(isCorrectRotation(rotation)) {
      adjustThickness(rotation);
      super.draw(g, no, rotation);
    }
  }

  @Override
  public Rectangle2D getBoundingBox(final int type, final double rotation) {
    if(isCorrectRotation(rotation)) {
      adjustThickness(rotation);
      return super.getBoundingBox(type, rotation);
    }
    return new Rectangle2D.Double();
  }

  @Override
  public void setColor(final Color color) {
    // TODO
  }

}
