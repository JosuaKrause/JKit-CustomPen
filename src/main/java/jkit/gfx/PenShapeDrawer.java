package jkit.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jkit.gfx.pen.Pen;

/**
 * A shape drawer using a {@link Pen}.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public final class PenShapeDrawer extends AbstractShapeDrawer {

  /** The pen. */
  protected final Pen pen;

  /** The maximal segment length. */
  protected final double segLen;

  /**
   * Creates a shape drawer for the given pen.
   * 
   * @param pen The pen.
   */
  public PenShapeDrawer(final Pen pen) {
    if(pen == null) throw new NullPointerException("pen");
    this.pen = pen;
    segLen = pen.segmentLength();
  }

  /**
   * A segment of an path iterator.
   * 
   * @author Joschi <josua.krause@gmail.com>
   */
  private final class Segment {

    /** The current position. */
    private final Point2D cur;

    /** Whether the segment is a move operation. */
    private final boolean isMove;

    /** Whether this segment is a first segment of a line. */
    private final boolean isFirst;

    /** The previous position. */
    private final Point2D last;

    /** The current position of the last move-to segment. */
    private final Point2D curMoveTo;

    /** The affine transform of this segment. */
    private final AffineTransform at;

    /** Whether this segment is a no-op. */
    private final boolean isNop;

    /** Whether this segment is a last segment of a line. */
    private boolean isLast;

    /**
     * Creates a segment.
     * 
     * @param pi The path iterator.
     * @param cmt The current position of the last move-to segment.
     * @param coords The array suitable to hold the coordinates.
     * @param lastSeg The previous segment.
     */
    public Segment(final PathIterator pi, final Point2D cmt,
        final double[] coords, final Segment lastSeg) {
      final int segmentType = pi.currentSegment(coords);
      isMove = segmentType == PathIterator.SEG_MOVETO;
      switch(segmentType) {
        case PathIterator.SEG_MOVETO:
          cur = create(coords, 0);
          curMoveTo = cur;
          break;
        case PathIterator.SEG_CLOSE:
          cur = cmt;
          curMoveTo = null;
          break;
        case PathIterator.SEG_LINETO:
          cur = create(coords, 0);
          curMoveTo = cmt;
          break;
        case PathIterator.SEG_QUADTO:
          // will not be used since we have a flattened path iterator
          cur = create(coords, 2);
          curMoveTo = cmt;
          break;
        case PathIterator.SEG_CUBICTO:
          // will not be used since we have a flattened path iterator
          cur = create(coords, 4);
          curMoveTo = cmt;
          break;
        default:
          throw new InternalError();
      }
      // set last
      final boolean valid;
      final double x, y, dx, dy;
      last = lastSeg != null ? lastSeg.cur : null;
      if(last != null) {
        isFirst = lastSeg.isMove;
        if(isMove) {
          lastSeg.isLast = true;
          dx = dy = x = y = Double.NaN;
          valid = false;
        } else {
          x = last.getX();
          y = last.getY();
          dx = cur.getX() - x;
          dy = cur.getY() - y;
          valid = true;
        }
      } else {
        isFirst = true;
        dx = dy = x = y = Double.NaN;
        valid = false;
      }
      isNop = dx == 0.0 && dy == 0.0;
      // compute len
      if(valid) {
        len = Math.sqrt(dx * dx + dy * dy);
      } else {
        len = Double.NaN;
      }
      // compute rot
      final double rot;
      if(valid) {
        if(dx == 0.0) {
          rot = Math.PI * (dy > 0.0 ? 0.5 : 1.5);
        } else {
          rot = (dx < 0 ? Math.PI : 0) + fastArcTan(dy / dx);
        }
      } else {
        rot = Double.NaN;
      }
      this.rot = rot;
      // compute affine transformation
      if(valid && !isNop) {
        at = AffineTransform.getTranslateInstance(x, y);
        at.rotate(rot);
      } else {
        at = null;
      }
    }

    /** The length. */
    private final double len;

    /** A quarter pi. */
    public static final double M_PI_4 = Math.PI / 4.0;

    /**
     * A fast implementation of {@link Math#atan(double)}. The maximum error is
     * <code>0.0015</code> radians. The behavior is the same as the library
     * function. The algorithm comes from:
     * <em>"Efficient approximations for the arctangent function",
     * Rajan, S. Sichun Wang Inkol, R. Joyal, A., May 2006</em>
     * 
     * @param a The value whose arc tangent is to be returned.
     * @return The arc tangent of the argument.
     * @see Math#atan(double)
     */
    private double fastArcTan(final double a) {
      if(a < -1 || a > 1) return Math.atan(a);
      return M_PI_4 * a - a * (Math.abs(a) - 1) * (0.2447 + 0.0663 * Math.abs(a));
    }

    /**
     * Getter.
     * 
     * @return The position of the last move-to operation.
     */
    public Point2D getCurMoveTo() {
      return curMoveTo;
    }

    /**
     * Getter.
     * 
     * @return Whether this segment is a move-to segment.
     */
    public boolean isMove() {
      return isMove;
    }

    /**
     * Getter.
     * 
     * @param coords The array holding the coordinates.
     * @param pos The position in the coordinates array.
     * @return The position specified by the coordinates array.
     */
    private Point2D create(final double[] coords, final int pos) {
      return new Point2D.Double(coords[pos], coords[pos + 1]);
    }

    /** The value for the rotation. */
    private final double rot;

    /**
     * Draws the current segment.
     * 
     * @param gfx The graphics context.
     * @param no The number of the segment.
     * @return The number of the next segment. May be the same number as
     *         <code>no</code> when nothing was drawn.
     */
    public int drawCurrentSegment(final Graphics2D gfx, final int no) {
      if(isNop) return no;
      final Graphics2D seg = (Graphics2D) gfx.create();
      seg.transform(at);
      final int newNo = drawSeg(seg, len, rot, no);
      seg.dispose();
      return newNo;
    }

    /**
     * Adds the bounding box of this segment to the given rectangle.
     * 
     * @param r The rectangle.
     */
    public void bboxCurrentSegment(final Rectangle2D r) {
      if(isNop) return;
      bbox(r, new AffineTransform(at), len, rot);
    }

    /**
     * Whether this segment has to be drawn because it is in the visible area of
     * the graphics context.
     * 
     * @param g The graphics context.
     * @param type The segment type.
     * @param rot The rotation of the segment.
     * @return Whether this segment needs to be drawn.
     */
    private boolean mustDraw(final Graphics2D g, final int type, final double rot) {
      final Shape clip = g.getClip();
      return clip == null || clip.intersects(pen.getBoundingBox(type, rot));
    }

    /**
     * Adds the bounding box of the segment to the given rectangle.
     * 
     * @param r The rectangle holding the bounding box.
     * @param af The transformation.
     * @param length The length of this segment.
     * @param rot The rotation of this segment.
     */
    private void bbox(final Rectangle2D r,
        final AffineTransform af, final double length, final double rot) {
      double pos = 0.0;
      final double end = Math.max(length - segLen * 0.5, 0.0);
      while(pos <= end) {
        final AffineTransform at = new AffineTransform(af);
        final Shape s;
        if(isFirst && pos == 0.0) {
          s = pen.getBoundingBox(Pen.SEG_START, rot);
        } else if(isLast && pos + segLen > end) {
          s = pen.getBoundingBox(Pen.SEG_END, rot);
        } else {
          s = pen.getBoundingBox(Pen.SEG_NORM, rot);
        }
        unite(r, transform(at, s));
        pos += segLen;
        af.translate(segLen, 0.0);
      }
    }

    /**
     * Transforms a shape and then computes the bounding box. Note that this
     * method is faster than actually transforming the shape.
     * 
     * @param at The transformation.
     * @param s The shape.
     * @return The bounding box of the transformed shape.
     */
    private Rectangle2D transform(final AffineTransform at, final Shape s) {
      final Rectangle2D r = s.getBounds2D();
      final Point2D a = new Point2D.Double(r.getMinX(), r.getMinY());
      final Point2D b = new Point2D.Double(r.getMaxX(), r.getMaxY());
      final Point2D c = new Point2D.Double(r.getMinX(), r.getMaxY());
      final Point2D d = new Point2D.Double(r.getMaxX(), r.getMinY());
      at.transform(a, a);
      at.transform(b, b);
      at.transform(c, c);
      at.transform(d, d);
      final double minX = Math.min(a.getX(),
          Math.min(b.getX(), Math.min(c.getX(), d.getX())));
      final double maxX = Math.max(a.getX(),
          Math.max(b.getX(), Math.max(c.getX(), d.getX())));
      final double minY = Math.min(a.getY(),
          Math.min(b.getY(), Math.min(c.getY(), d.getY())));
      final double maxY = Math.max(a.getY(),
          Math.max(b.getY(), Math.max(c.getY(), d.getY())));
      return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Draws the segment.
     * 
     * @param seg The graphics context.
     * @param length The length of the segment.
     * @param rot The rotation of the segment.
     * @param oldNo The segment number before calling this method.
     * @return The segment number after calling this method.
     */
    private int drawSeg(final Graphics2D seg,
        final double length, final double rot, final int oldNo) {
      int no = oldNo;
      double pos = 0.0;
      final double end = Math.max(length - segLen * 0.5, 0.0);
      while(pos <= end) {
        final Graphics2D s = (Graphics2D) seg.create();
        if(isFirst && pos == 0.0) {
          if(mustDraw(s, Pen.SEG_START, rot)) {
            pen.start(s, no, rot);
          }
        } else if(isLast && pos + segLen > end) {
          if(mustDraw(s, Pen.SEG_END, rot)) {
            pen.end(s, no, rot);
          }
        } else {
          if(mustDraw(s, Pen.SEG_NORM, rot)) {
            pen.draw(s, no, rot);
          }
        }
        pos += segLen;
        ++no;
        s.dispose();
        seg.translate(segLen, 0.0);
      }
      return no;
    }

    /**
     * Setter.
     * 
     * @param isLast Whether this segment is the last of a line.
     */
    public void setIsLast(final boolean isLast) {
      this.isLast = isLast;
    }

  }

  @Override
  public Drawable getDrawable(final Shape outline) {
    final List<Segment> list = new ArrayList<>();
    createSegments(outline, list);
    return new Drawable() {

      @Override
      public void draw(final Graphics2D gfx) {
        int no = 0;
        final Graphics2D g = (Graphics2D) gfx.create();
        pen.prepare(g, outline);
        for(final Segment s : list) {
          no = drawIfNotNull(g, s, no);
        }
        g.dispose();
      }

      @Override
      protected Rectangle2D computeBounds() {
        final Rectangle2D r = new Rectangle2D.Double();
        final Shape sb = pen.getSpecialBounds(outline);
        if(sb != null) {
          unite(r, sb);
        }
        for(final Segment s : list) {
          bboxIfNotNull(r, s);
        }
        return r;
      }

    };
  }

  /**
   * Creates segments from the given shape and puts them into the list.
   * 
   * @param outline The outline.
   * @param list The list.
   */
  private void createSegments(final Shape outline, final List<Segment> list) {
    final PathIterator pi = outline.getPathIterator(null, Math.sqrt(segLen));
    final double[] coords = new double[6];
    Point2D curMoveTo = null;
    Segment last = null;
    Segment cur = null;
    while(!pi.isDone()) {
      cur = new Segment(pi, curMoveTo, coords, last);
      list.add(last);
      curMoveTo = cur.getCurMoveTo();
      last = cur;
      pi.next();
    }
    if(cur != null) {
      cur.setIsLast(true);
    }
    list.add(cur);
  }

  /**
   * Draws the segment if it is not <code>null</code>.
   * 
   * @param gfx The graphics context.
   * @param seg The segment.
   * @param no The number of the segment.
   * @return The segment number after a call to this method.
   */
  protected static int drawIfNotNull(final Graphics gfx, final Segment seg, final int no) {
    if(!(seg != null && !seg.isMove())) return no;
    return seg.drawCurrentSegment((Graphics2D) gfx, no);
  }

  /**
   * Adds the bounding box of the segment to the given rectangle if the segment
   * is not <code>null</code>.
   * 
   * @param r The rectangle.
   * @param seg The segment.
   */
  protected static void bboxIfNotNull(final Rectangle2D r, final Segment seg) {
    if(seg == null || seg.isMove()) return;
    seg.bboxCurrentSegment(r);
  }

  /**
   * Combines a rectangle with the bounding box of a shape.
   * 
   * @param r The rectangle to enlarge.
   * @param s The shape.
   */
  protected static void unite(final Rectangle2D r, final Shape s) {
    final Rectangle2D b = s.getBounds2D();
    if(r.isEmpty()) {
      r.setRect(b);
    } else {
      r.add(b);
    }
  }

  @Override
  public void setColor(final Color color) {
    pen.setColor(color);
  }

}
