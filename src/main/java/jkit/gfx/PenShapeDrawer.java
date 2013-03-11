package jkit.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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

    /** The array to hold the coordinates. */
    private final double[] coords = new double[6];

    /** The segment type. */
    private final int segmentType;

    /** The current position. */
    private final Point2D cur;

    /** Whether the segment is a move operation. */
    private final boolean isMove;

    /** Whether this segment is a first segment of a line. */
    private boolean isFirst;

    /** Whether this segment is a last segment of a line. */
    private boolean isLast;

    /** The previous position. */
    private Point2D last;

    /** The current position of the last move-to segment. */
    private Point2D curMoveTo;

    /** The starting x position. */
    private double x;

    /** The starting y position. */
    private double y;

    /** The distance moved in x direction by this segment. */
    private double dx;

    /** The distance moved in y direction by this segment. */
    private double dy;

    /**
     * Creates a segment.
     * 
     * @param pi The path iterator.
     * @param cmt The current position of the last move-to segment.
     */
    public Segment(final PathIterator pi, final Point2D cmt) {
      curMoveTo = cmt;
      segmentType = pi.currentSegment(coords);
      isMove = segmentType == PathIterator.SEG_MOVETO;
      switch(segmentType) {
        case PathIterator.SEG_MOVETO:
          cur = create(0);
          curMoveTo = cur;
          break;
        case PathIterator.SEG_CLOSE:
          cur = curMoveTo;
          curMoveTo = null;
          break;
        case PathIterator.SEG_LINETO:
          cur = create(0);
          break;
        case PathIterator.SEG_QUADTO:
          cur = create(2);
          break;
        case PathIterator.SEG_CUBICTO:
          cur = create(4);
          break;
        default:
          throw new InternalError();
      }
    }

    /**
     * Sets the previous segment and initializes corresponding values.
     * 
     * @param lastSeg The previous segment.
     */
    public void setLast(final Segment lastSeg) {
      last = lastSeg != null ? lastSeg.cur : null;
      if(last != null) {
        isFirst = isFirst || lastSeg.isMove;
        if(isMove) {
          lastSeg.isLast = true;
        } else {
          x = last.getX();
          y = last.getY();
          dx = cur.getX() - x;
          dy = cur.getY() - y;
        }
      } else {
        isFirst = true;
      }
    }

    /**
     * Getter
     * 
     * @return The length of this segment.
     */
    private double getLength() {
      return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Getter.
     * 
     * @return The orientation of this segment.
     */
    private double getOrientation() {
      if(dx == 0.0) return Math.PI * (dy > 0.0 ? 0.5 : 1.5);
      // TODO FIXME possible bottle-neck
      return (dx < 0 ? Math.PI : 0) + Math.atan(dy / dx);
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
     * @param pos The position in the coordinates array.
     * @return The position specified by the coordinates array.
     */
    private Point2D create(final int pos) {
      return new Point2D.Double(coords[pos], coords[pos + 1]);
    }

    /**
     * Draws the current segment.
     * 
     * @param gfx The graphics context.
     * @param no The number of the segment.
     * @return The number of the next segment. May be the same number as
     *         <code>no</code> when nothing was drawn.
     */
    public int drawCurrentSegment(final Graphics2D gfx, final int no) {
      if(dx == 0.0 && dy == 0.0) return no;
      final Graphics2D seg = (Graphics2D) gfx.create();
      final AffineTransform at = AffineTransform.getTranslateInstance(x, y);
      final double rot = getOrientation();
      at.rotate(rot);
      seg.transform(at);
      final int newNo = drawSeg(seg, getLength(), rot, no);
      seg.dispose();
      return newNo;
    }

    /**
     * Adds the bounding box of this segment to the given rectangle.
     * 
     * @param r The rectangle.
     */
    public void bboxCurrentSegment(final Rectangle2D r) {
      if(dx == 0.0 && dy == 0.0) return;
      final AffineTransform at = AffineTransform.getTranslateInstance(x, y);
      final double rot = getOrientation();
      at.rotate(rot);
      bbox(r, at, getLength(), rot);
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
      return g.getClip().intersects(pen.getBoundingBox(type, rot));
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
  public void draw(final Graphics gfx, final Shape outline) {
    int no = 0;
    final Graphics2D g = (Graphics2D) gfx.create();
    pen.prepare(g, outline);
    final PathIterator pi = outline.getPathIterator(null, Math.sqrt(segLen));
    Point2D curMoveTo = null;
    Segment last = null;
    Segment cur = null;
    while(!pi.isDone()) {
      cur = new Segment(pi, curMoveTo);
      cur.setLast(last);
      no = drawIfNotNull(g, last, no);
      curMoveTo = cur.getCurMoveTo();
      last = cur;
      pi.next();
    }
    if(cur != null) {
      cur.setIsLast(true);
    }
    drawIfNotNull(g, cur, no);
    g.dispose();
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

  @Override
  public Rectangle2D getBounds(final Shape s) {
    final Rectangle2D r = new Rectangle2D.Double();
    final Shape sb = pen.getSpecialBounds(s);
    if(sb != null) {
      unite(r, sb);
    }
    final PathIterator pi = s.getPathIterator(null, Math.sqrt(segLen));
    Point2D curMoveTo = null;
    Segment last = null;
    Segment cur = null;
    while(!pi.isDone()) {
      cur = new Segment(pi, curMoveTo);
      cur.setLast(last);
      bboxIfNotNull(r, last);
      curMoveTo = cur.getCurMoveTo();
      last = cur;
      pi.next();
    }
    if(cur != null) {
      cur.setIsLast(true);
    }
    bboxIfNotNull(r, cur);
    return r;
  }

}
