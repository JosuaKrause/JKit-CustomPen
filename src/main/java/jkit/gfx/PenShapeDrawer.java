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
 * 
 */
public class PenShapeDrawer extends AbstractShapeDrawer {

	/** The pen. */
	private final Pen pen;

	/** The maximal segment length. */
	final double segLen;

	/**
	 * Creates a shape drawer for the given pen.
	 * 
	 * @param pen
	 *            The pen.
	 */
	public PenShapeDrawer(final Pen pen) {
		if (pen == null) {
			throw new NullPointerException("pen");
		}
		this.pen = pen;
		segLen = pen.segmentLength();
	}

	class Segment {

		private final double[] coords = new double[6];

		private final int segmentType;

		private final Point2D cur;

		private final boolean isMove;

		private boolean isFirst;

		private boolean isLast;

		private Point2D last;

		private Point2D curMoveTo;

		private double x;

		private double y;

		private double dx;

		private double dy;

		public Segment(final PathIterator pi, final Point2D cmt) {
			curMoveTo = cmt;
			segmentType = pi.currentSegment(coords);
			isMove = segmentType == PathIterator.SEG_MOVETO;
			switch (segmentType) {
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

		public void setLast(final Segment lastSeg) {
			last = lastSeg != null ? lastSeg.cur : null;
			if (last != null) {
				isFirst = isFirst || lastSeg.isMove;
				if (isMove) {
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

		private double getLength() {
			return Math.sqrt(dx * dx + dy * dy);
		}

		private double getOrientation() {
			if (dx == 0.0) {
				return Math.PI * (dy > 0.0 ? 0.5 : 1.5);
			}
			return (dx < 0 ? Math.PI : 0) + Math.atan(dy / dx);
		}

		public Point2D getCurMoveTo() {
			return curMoveTo;
		}

		public Point2D getCur() {
			return cur;
		}

		public boolean isMove() {
			return isMove;
		}

		private Point2D create(final int pos) {
			return new Point2D.Double(coords[pos], coords[pos + 1]);
		}

		public int drawCurrentSegment(final Graphics2D gfx, int no) {
			if (dx == 0.0 && dy == 0.0) {
				return no;
			}
			final Graphics2D seg = (Graphics2D) gfx.create();
			final AffineTransform at = AffineTransform.getTranslateInstance(x,
					y);
			final double rot = getOrientation();
			at.rotate(rot);
			seg.transform(at);
			no = drawSeg(seg, getLength(), rot, no);
			seg.dispose();
			return no;
		}

		public void bboxCurrentSegment(final Rectangle2D r) {
			if (dx == 0.0 && dy == 0.0) {
				return;
			}
			final AffineTransform at = AffineTransform.getTranslateInstance(x,
					y);
			final double rot = getOrientation();
			at.rotate(rot);
			bbox(r, at, getLength(), rot);
		}

		private boolean mustDraw(final Graphics2D g, final int type,
				final double rot) {
			return g.getClip().intersects(pen.getBoundingBox(type, rot));
		}

		private void bbox(final Rectangle2D r, final AffineTransform af,
				final double length, final double rot) {
			double pos = 0.0;
			final double end = Math.max(length - segLen * 0.5, 0.0);
			while (pos <= end) {
				final AffineTransform at = new AffineTransform(af);
				final Shape s;
				if (isFirst && pos == 0.0) {
					s = pen.getBoundingBox(Pen.SEG_START, rot);
				} else if (isLast && pos + segLen > end) {
					s = pen.getBoundingBox(Pen.SEG_END, rot);
				} else {
					s = pen.getBoundingBox(Pen.SEG_NORM, rot);
				}
				unite(r, transform(at, s));
				pos += segLen;
				af.translate(segLen, 0.0);
			}
		}

		private Rectangle2D transform(final AffineTransform at, final Shape s) {
			final Rectangle2D r = s.getBounds2D();
			Point2D a;
			Point2D b;
			Point2D c;
			Point2D d;
			a = at.transform(
					new Point2D.Double(r.getMinX(), r.getMinY()), null);
			b = at.transform(
					new Point2D.Double(r.getMaxX(), r.getMaxY()), null);
			c = at.transform(
					new Point2D.Double(r.getMinX(), r.getMaxY()), null);
			d = at.transform(
					new Point2D.Double(r.getMaxX(), r.getMinY()), null);
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

		private int drawSeg(final Graphics2D seg, final double length,
				final double rot, int no) {
			double pos = 0.0;
			final double end = Math.max(length - segLen * 0.5, 0.0);
			while (pos <= end) {
				final Graphics2D s = (Graphics2D) seg.create();
				if (isFirst && pos == 0.0) {
					if (mustDraw(s, Pen.SEG_START, rot)) {
						pen.start(s, no, rot);
					}
				} else if (isLast && pos + segLen > end) {
					if (mustDraw(s, Pen.SEG_END, rot)) {
						pen.end(s, no, rot);
					}
				} else {
					if (mustDraw(s, Pen.SEG_NORM, rot)) {
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

		public void setIsLast(final boolean isLast) {
			this.isLast = isLast;
		}

	}

	@Override
	public void draw(final Graphics gfx, final Shape outline) {
		int no = 0;
		final Graphics2D g = (Graphics2D) gfx.create();
		pen.prepare(g, outline);
		final PathIterator pi = outline
				.getPathIterator(null, Math.sqrt(segLen));
		Point2D curMoveTo = null;
		Segment last = null;
		Segment cur = null;
		while (!pi.isDone()) {
			cur = new Segment(pi, curMoveTo);
			cur.setLast(last);
			no = drawIfNotNull(g, last, no);
			curMoveTo = cur.getCurMoveTo();
			last = cur;
			pi.next();
		}
		if (cur != null) {
			cur.setIsLast(true);
		}
		drawIfNotNull(g, cur, no);
		g.dispose();
	}

	private static int drawIfNotNull(final Graphics gfx, final Segment seg,
			final int no) {
		if (!(seg != null && !seg.isMove())) {
			return no;
		}
		return seg.drawCurrentSegment((Graphics2D) gfx, no);
	}

	private static void bboxIfNotNull(final Rectangle2D r, final Segment seg) {
		if (seg != null && !seg.isMove()) {
			seg.bboxCurrentSegment(r);
		}
	}

	private static void unite(final Rectangle2D r, final Shape s) {
		if (r.isEmpty()) {
			r.setRect(s.getBounds2D());
		} else {
			r.add(s.getBounds2D());
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
		if (sb != null) {
			unite(r, sb);
		}
		final PathIterator pi = s.getPathIterator(null, Math.sqrt(segLen));
		Point2D curMoveTo = null;
		Segment last = null;
		Segment cur = null;
		while (!pi.isDone()) {
			cur = new Segment(pi, curMoveTo);
			cur.setLast(last);
			bboxIfNotNull(r, last);
			curMoveTo = cur.getCurMoveTo();
			last = cur;
			pi.next();
		}
		if (cur != null) {
			cur.setIsLast(true);
		}
		bboxIfNotNull(r, cur);
		return r;
	}

}
