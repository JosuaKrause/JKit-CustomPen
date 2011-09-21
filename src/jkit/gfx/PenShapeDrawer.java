package jkit.gfx;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import jkit.gfx.pen.Pen;

public class PenShapeDrawer extends AbstractShapeDrawer {

	private final Pen pen;

	final double segLen;

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

		public void drawCurrentSegment(final Graphics2D gfx) {
			if (dx == 0.0 && dy == 0.0) {
				return;
			}
			final Graphics2D seg = (Graphics2D) gfx.create();
			final AffineTransform at = AffineTransform.getTranslateInstance(x,
					y);
			at.rotate(getOrientation());
			seg.transform(at);
			drawSeg(seg, getLength());
			seg.dispose();
		}

		private void drawSeg(final Graphics2D seg, final double length) {
			final Rectangle rect = pen.getSegmentBoundingBox();
			double pos = 0.0;
			final double end = Math.max(length - segLen * 0.5, 0.0);
			while (pos <= end) {
				final boolean visible = seg.hitClip(rect.x, rect.y, rect.width,
						rect.height);
				if (visible) {
					final Graphics2D s = (Graphics2D) seg.create();
					if (isFirst && pos == 0.0) {
						pen.start(s);
					} else if (isLast && pos + segLen > end) {
						pen.end(s);
					} else {
						pen.draw(s);
					}
					s.dispose();
				}
				pos += segLen;
				seg.translate(segLen, 0.0);
			}
		}

		public void setIsLast(final boolean isLast) {
			this.isLast = isLast;
		}

	}

	@Override
	public void draw(final Graphics gfx, final Shape outline) {
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
			drawIfNotNull(g, last);
			curMoveTo = cur.getCurMoveTo();
			last = cur;
			pi.next();
		}
		if (cur != null) {
			cur.setIsLast(true);
		}
		drawIfNotNull(g, cur);
		g.dispose();
	}

	private void drawIfNotNull(final Graphics gfx, final Segment seg) {
		if (seg != null && !seg.isMove()) {
			seg.drawCurrentSegment((Graphics2D) gfx);
		}
	}

}
