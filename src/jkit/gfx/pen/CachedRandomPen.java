package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public abstract class CachedRandomPen extends SimplePen {

    public static boolean doCaching = true;

    public static final int DEFAULT_CACHE_SIZE = 10;

    public static final double CACHE_SCALE = 8;

    private final Random rndSegement = new Random();

    private final Random rndBucket = new Random();

    private final int cacheSize = DEFAULT_CACHE_SIZE;

    public CachedRandomPen(final Color color) {
        super(color);
    }

    public CachedRandomPen(final Color color, final double segmentLength) {
        super(color,segmentLength);
    }

    private final Image[] cache = new Image[cacheSize];

    protected void invalidate() {
        int i = cache.length;
        while (--i >= 0) {
            cache[i] = null;
        }
    }

    private int getNextBucket(final int no) {
        rndBucket.setSeed(seed + no);
        return rndBucket.nextInt(cache.length);
    }

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

    protected void setSeed(final int no) {
        rndSegement.setSeed(seed + no);
    }

    private Rectangle2D oldBBox;

    @Override
    public final void draw(final Graphics2D g, final int no,
            final double rotation) {
        if (!doCaching) {
            setSeed(no);
            drawSegment(g);
            return;
        }
        final Rectangle2D bbox = getBoundingBox(SEG_NORM, rotation);
        if (oldBBox == null || !oldBBox.equals(bbox)) {
            invalidate();
            oldBBox = bbox;
        }
        final double dx = bbox.getMinX();
        final double dy = bbox.getMinY();
        final int width = (int) Math.ceil(bbox.getWidth() * CACHE_SCALE);
        final int height = (int) Math.ceil(bbox.getHeight() * CACHE_SCALE);
        final int bucket = getNextBucket(no);
        if (cache[bucket] == null) {
            setSeed(bucket);
            final BufferedImage img = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            final Graphics2D gfx = img.createGraphics();
            gfx.setColor(g.getColor());
            gfx.setStroke(g.getStroke());
            gfx.setRenderingHints(g.getRenderingHints());
            gfx.scale(CACHE_SCALE, CACHE_SCALE);
            gfx.translate(-dx, -dy);
            drawSegment(gfx);
            gfx.dispose();
            cache[bucket] = img.getScaledInstance(
                    (int) Math.ceil(bbox.getWidth()),
                    (int) Math.ceil(bbox.getHeight()), Image.SCALE_SMOOTH);
        }
        g.translate(dx, dy);
        g.drawImage(cache[bucket], 0, 0, null);
        // g.setColor(new Color(0x10ff00ff, true));
        // g.fill(new Rectangle2D.Double(0, 0, width, height));
    }

    protected abstract void drawSegment(Graphics2D g);

    protected double rndNextDouble() {
        return rndSegement.nextDouble();
    }

    protected double rndNextGaussian() {
        return rndSegement.nextGaussian();
    }

}
