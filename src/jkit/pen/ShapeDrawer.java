package jkit.pen;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public class ShapeDrawer {

	private final Pen pen;

	public ShapeDrawer(final Pen pen) {
		if (pen == null) {
			throw new NullPointerException("pen");
		}
		this.pen = pen;
	}

	public void draw(final Graphics gfx, final Shape outline) {
		boolean isFirst = true;
		final double segLen = pen.segmentLength();
		final Graphics2D g = (Graphics2D) gfx.create();
		final PathIterator pi = outline
				.getPathIterator(null, Math.sqrt(segLen));
		final double[] coords = new double[6];
		Point2D last = null;
		Point2D curMoveTo = null;
		while (!pi.isDone()) {
			boolean move = false;
			Point2D cur;
			pi.currentSegment(coords);
			switch (pi.getWindingRule()) {
			case PathIterator.SEG_MOVETO:
				cur = create(coords, 0);
				curMoveTo = cur;
				move = true;
				break;
			case PathIterator.SEG_CLOSE:
				cur = curMoveTo;
				curMoveTo = null;
				break;
			case PathIterator.SEG_LINETO:
				cur = create(coords, 0);
				break;
			case PathIterator.SEG_QUADTO:
				cur = create(coords, 2);
				break;
			case PathIterator.SEG_CUBICTO:
				cur = create(coords, 4);
				break;
			default:
				throw new InternalError();
			}
			pi.next();
			final boolean isLast = pi.isDone();
			if (!move && last != null && cur != null) {
				final Graphics2D seg = (Graphics2D) g.create();
				final AffineTransform at = AffineTransform
						.getTranslateInstance(last.getX(), last.getY());
				at.rotate(getOrientation(last, cur));
				seg.transform(at);
				drawSeg(seg, getLength(last, cur), segLen, isFirst, isLast);
				isFirst = false;
				seg.dispose();
			}
			last = cur;
		}
	}

	private void drawSeg(final Graphics2D seg, final double length,
			final double segLen, final boolean isFirst, final boolean isLast) {
		double pos = 0.0;
		final double end = Math.max(length - segLen * 0.5, 0.0);
		while (pos <= end) {
			if (isFirst && pos == 0.0) {
				pen.start(seg);
			} else if (isLast && pos + segLen > end) {
				pen.end(seg);
			} else {
				pen.draw(seg);
			}
			pos += segLen;
			seg.translate(segLen, 0.0);
		}
	}

	private double getLength(final Point2D a, final Point2D b) {
		final double x = b.getX() - a.getX();
		final double y = b.getY() - a.getY();
		return Math.sqrt(x * x + y * y);
	}

	private double getOrientation(final Point2D a, final Point2D b) {
		final double x = b.getX() - a.getX();
		final double y = b.getY() - a.getY();
		if (x == 0.0) {
			return Math.PI * (y > 0.0 ? 0.5 : 1.5);
		}
		return (x < 0 ? Math.PI : 0) + Math.atan(y / x);
	}

	private Point2D create(final double[] coords, final int pos) {
		return new Point2D.Double(coords[pos], coords[pos + 1]);
	}

}
