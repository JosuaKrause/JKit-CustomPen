package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * A pen with all basic functionality taken care of.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public abstract class SimplePen extends PenAdapter {

  /** The segment length. */
  protected double segmentLength;

  /** The color. */
  protected Color color;

  /** Whether the pen is already initialized. */
  private boolean initialized;

  /**
   * Creates a simple pen with the given color.
   * 
   * @param color The color.
   */
  public SimplePen(final Color color) {
    this(color, 10.0);
  }

  /**
   * Creates a simple pen with the given color and segment length.
   * 
   * @param color The color.
   * @param segmentLength The segment length.
   */
  public SimplePen(final Color color, final double segmentLength) {
    this.color = color;
    this.segmentLength = segmentLength;
    initialized = false;
  }

  /** The stroke used to draw. */
  protected Stroke usedStroke;

  @Override
  public void prepare(final Graphics2D g, final Shape s) {
    if(!initialized) {
      setColor(color);
      setSegmentLength(segmentLength);
      initialized = true;
    }
    if(color != null) {
      g.setColor(color);
    }
    postPrepare(g);
  }

  /**
   * This method is called after {@link #prepare(Graphics2D, Shape)} has
   * finished.
   * 
   * @param g The graphics configuration.
   */
  protected void postPrepare(final Graphics2D g) {
    usedStroke = g.getStroke();
  }

  /** The standard stroke. */
  private static final BasicStroke BASE = new BasicStroke(1f);

  /**
   * Getter.
   * 
   * @param s The shape.
   * @return The bounds of this shape accounting for strokes.
   */
  protected Rectangle2D getBounds(final Shape s) {
    return (usedStroke != null ? usedStroke.createStrokedShape(s) : BASE
        .createStrokedShape(s)).getBounds2D();
  }

  @Override
  public void setColor(final Color color) {
    this.color = color;
  }

  /**
   * Getter.
   * 
   * @return The color.
   */
  public Color color() {
    return color;
  }

  /**
   * Setter.
   * 
   * @param segmentLength The segment length.
   */
  public void setSegmentLength(final double segmentLength) {
    this.segmentLength = segmentLength;
  }

  @Override
  public double segmentLength() {
    return segmentLength;
  }

}
