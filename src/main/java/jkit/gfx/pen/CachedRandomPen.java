package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * A pen caching segments to speed up drawing.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public abstract class CachedRandomPen extends SimplePen {

  /** Whether the caching is active. */
  public static boolean doCaching = true;

  /** The scaling of the cache. */
  public static double CACHE_SCALE = 8;

  /** Whether the cache should be scaled. */
  public static boolean doScale = false;

  /** The default cache size. */
  public static final int DEFAULT_CACHE_SIZE = 20;

  /** The random number generator for segments. */
  private final Random rndSegement = new Random();

  /** The random number generator for buckets. */
  private final Random rndBucket = new Random();

  /** The cache size. */
  private final int cacheSize = DEFAULT_CACHE_SIZE;

  /**
   * Creates a cached pen with the given color.
   * 
   * @param color The color.
   */
  public CachedRandomPen(final Color color) {
    super(color);
  }

  /**
   * Creates a cached pen with the given color and segment length.
   * 
   * @param color The color.
   * @param segmentLength The segment length.
   */
  public CachedRandomPen(final Color color, final double segmentLength) {
    super(color, segmentLength);
  }

  /** The cache. */
  private final Image[] cache = new Image[cacheSize];

  /** Empties the cache. */
  protected void invalidate() {
    int i = cache.length;
    while(--i >= 0) {
      cache[i] = null;
    }
  }

  /**
   * Starts the next bucket.
   * 
   * @param no The number of the bucket.
   * @return The value for the given bucket.
   */
  private int getNextBucket(final int no) {
    rndBucket.setSeed(seed + no);
    return rndBucket.nextInt(cache.length);
  }

  /** The current seed. */
  private int seed;

  @Override
  public void prepare(final Graphics2D g, final Shape s) {
    super.prepare(g, s);
    seed = s.getBounds2D().hashCode();
    rndBucket.setSeed(seed);
  }

  @Override
  public void setColor(final Color color) {
    super.setColor(color);
    invalidate();
  }

  @Override
  public void setSegmentLength(final double segmentLength) {
    super.setSegmentLength(segmentLength);
    invalidate();
  }

  /**
   * Setter.
   * 
   * @param no The bucket number.
   */
  protected void setSeed(final int no) {
    rndSegement.setSeed(seed + no);
  }

  /** The previous bounding box. */
  private Rectangle2D oldBBox;

  @Override
  public final void draw(final Graphics2D g, final int no,
      final double rotation) {
    if(!doCaching) {
      setSeed(no);
      drawSegment(g);
      return;
    }
    final Rectangle2D bbox = getBoundingBox(SEG_NORM, rotation);
    if(oldBBox == null || !oldBBox.equals(bbox)) {
      invalidate();
      oldBBox = bbox;
    }
    final double dx = bbox.getMinX();
    final double dy = bbox.getMinY();
    final int width = doScale ? (int) Math.ceil(bbox.getWidth()
        * CACHE_SCALE) : (int) Math.ceil(bbox.getWidth());
    final int height = doScale ? (int) Math.ceil(bbox.getHeight()
        * CACHE_SCALE) : (int) Math.ceil(bbox.getHeight());
    final int bucket = getNextBucket(no);
    if(cache[bucket] == null) {
      setSeed(bucket);
      final BufferedImage img = new BufferedImage(width, height,
          BufferedImage.TYPE_INT_ARGB);
      final Graphics2D gfx = img.createGraphics();
      gfx.setColor(g.getColor());
      gfx.setStroke(g.getStroke());
      gfx.setRenderingHints(g.getRenderingHints());
      gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      if(doScale) {
        gfx.scale(CACHE_SCALE, CACHE_SCALE);
      }
      gfx.translate(-dx, -dy);
      drawSegment(gfx);
      gfx.dispose();
      cache[bucket] = doScale ? img.getScaledInstance(
          (int) Math.ceil(bbox.getWidth()),
          (int) Math.ceil(bbox.getHeight()), Image.SCALE_SMOOTH)
          : img;
    }
    g.translate(dx, dy);
    g.drawImage(cache[bucket], 0, 0, null);
    // g.setColor(new Color(0x10ff00ff, true));
    // g.fill(new Rectangle2D.Double(0, 0, width, height));
  }

  /**
   * Renders the current segment.
   * 
   * @param g The graphics context.
   */
  protected abstract void drawSegment(Graphics2D g);

  /**
   * Getter.
   * 
   * @return The next random value.
   */
  protected double rndNextDouble() {
    return rndSegement.nextDouble();
  }

  /**
   * Getter.
   * 
   * @return The next gaussian random value.
   */
  protected double rndNextGaussian() {
    return rndSegement.nextGaussian();
  }

}
