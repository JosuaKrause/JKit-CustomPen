package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * A pen with a pencil style.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public class PencilPen extends CachedRandomPen {

  /** The shift in x direction relative to the line orientation. */
  protected double metricShiftX = 0.5;

  /** The shift in y direction relative to the line orientation. */
  protected double metricShiftY = 0;

  /** The spread in x direction relative to the line orientation. */
  protected double metricSpreadX = 0.5;

  /** The spread in y direction relative to the line orientation. */
  protected double metricSpreadY = 0.125;

  /** The maximal length. */
  protected double metricMaxLength = 1.125;

  /** The number of lines. */
  protected int count = 25;

  /** Creates a standard pencil pen. */
  public PencilPen() {
    this(10.0);
  }

  /**
   * Creates a pencil pen.
   * 
   * @param segmentLength The segment length.
   */
  public PencilPen(final double segmentLength) {
    super(new Color(0x40303030, true), segmentLength);
  }

  /**
   * Creates a pencil pen with a given color.
   * 
   * @param color The color.
   * @param segmentLength The segment length.
   */
  public PencilPen(final Color color, final double segmentLength) {
    super(new Color(color.getRGB() | 0x40000000, true), segmentLength);
  }

  @Override
  public void prepare(final Graphics2D g, final Shape s) {
    super.prepare(g, s);
    g.setStroke(new BasicStroke(.5f));
    postPrepare(g);
  }

  /** The horizontal shift relative to the line. */
  private double dx;

  /** The vertical shift relative to the line. */
  private double dy;

  /** The horizontal length relative to the line. */
  private double lx;

  /** The vertical length relative to the line. */
  private double ly;

  /** The maximal length. */
  private double ll;

  @Override
  public void setSegmentLength(final double segmentLength) {
    super.setSegmentLength(segmentLength);
    lx = segmentLength * metricSpreadX;
    ly = segmentLength * metricSpreadY;
    dx = segmentLength * metricShiftX;
    dy = segmentLength * metricShiftY;
    ll = segmentLength * metricMaxLength;
    invalidate();
  }

  /**
   * Getter.
   * 
   * @return Computes the next x position.
   */
  protected final double getNextX() {
    return rndNextGaussian() * lx + dx;
  }

  /**
   * Getter.
   * 
   * @return Computes the next y position.
   */
  protected final double getNextY() {
    return rndNextGaussian() * ly + dy;
  }

  /**
   * Getter.
   * 
   * @return Computes the next length.
   */
  protected final double getNextLine() {
    return rndNextGaussian() * ll;
  }

  /** The gaussian cut off value. */
  private static final double GAUSS_NULL = 4;

  /**
   * Getter.
   * 
   * @return The minimal x value.
   */
  protected final double getMinX() {
    return -GAUSS_NULL * lx + dx;
  }

  /**
   * Getter.
   * 
   * @return The minimal y value.
   */
  protected final double getMinY() {
    return -GAUSS_NULL * ly + dy;
  }

  /**
   * Getter.
   * 
   * @return The minimal line length.
   */
  protected final double getMinLine() {
    return -GAUSS_NULL * ll;
  }

  /**
   * Getter.
   * 
   * @return The maximal x value.
   */
  protected final double getMaxX() {
    return GAUSS_NULL * lx + dx;
  }

  /**
   * Getter.
   * 
   * @return The maximal y value.
   */
  protected final double getMaxY() {
    return GAUSS_NULL * ly + dy;
  }

  /**
   * Getter.
   * 
   * @return The maximal line length.
   */
  protected final double getMaxLine() {
    return GAUSS_NULL * ll;
  }

  @Override
  protected void drawSegment(final Graphics2D g) {
    for(int i = 0; i < count; ++i) {
      final double x = getNextX();
      final double y = getNextY();
      final double len = getNextLine();
      g.draw(new Line2D.Double(x, y, x + len, y));
    }
  }

  /** The bounding box cache. */
  private Rectangle2D bbox;

  @Override
  protected void invalidate() {
    super.invalidate();
    bbox = null;
  }

  @Override
  public Rectangle2D getBoundingBox(final int type, final double rotation) {
    if(bbox == null) {
      final double left = getMinX() + getMinLine();
      final double right = getMaxX() + getMaxLine();
      final double top = getMinY();
      final double bottom = getMaxY();
      final Rectangle2D r = new Rectangle2D.Double(left, top, right - left,
          bottom - top);
      bbox = getBounds(r);
    }
    return bbox;
  }

}
