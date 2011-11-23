package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class BloodTrailPen extends SimplePen {

	private final Random rnd = new Random();

	public BloodTrailPen() {
		this(10.0);
	}

	public BloodTrailPen(final double segmentLength) {
		super(new Color(0x407F0000, true), segmentLength);
	}

	@Override
	public void prepare(final Graphics2D g, final Shape s) {
		super.prepare(g, s);
		rnd.setSeed(s.getBounds2D().hashCode());
	}

	@Override
	public void start(final Graphics2D g, final double rotation) {
		final double dx = segmentLength * 0.25;
		final double dy = segmentLength * 0.25;
		final double lx = segmentLength * 0.5;
		final double ly = segmentLength * 0.5;
		final double ll = segmentLength * 1.00;
		for (int i = 0; i < 100; ++i) {
			final double y = rnd.nextGaussian() * ly - dy;
			final double x = rnd.nextGaussian() * lx - dx;
			final double len = rnd.nextGaussian() * ll;
			final double l2 = len * 0.5;
			g.fill(new Ellipse2D.Double(x - l2, y - l2, len, len));
		}
	}

	@Override
	public void draw(final Graphics2D g, final double rotation) {
		final double dx = segmentLength * 0.25;
		final double dy = segmentLength * 0.25;
		final double lx = segmentLength * 0.5;
		final double ly = segmentLength * 0.5;
		final double ll = segmentLength * 1.25;
		g.setStroke(new BasicStroke(2f));
		for (int i = 0; i < 25; ++i) {
			final double y = rnd.nextGaussian() * ly - dy;
			final double x = rnd.nextGaussian() * lx - dx;
			final double len = rnd.nextGaussian() * ll;
			g.draw(new Line2D.Double(x, y, x + len, y));
		}
	}

	private static final double GAUSS_NULL = 4;

	@Override
	public Rectangle2D getBoundingBox(final int type, final double rotation) {
		switch (type) {
		case SEG_START: {
			final double dx = segmentLength * 0.25;
			final double dy = segmentLength * 0.25;
			final double lx = segmentLength * 0.5;
			final double ly = segmentLength * 0.5;
			final double ll = segmentLength * 1.00;
			final double r = GAUSS_NULL * ll * 0.5;
			final double left = -GAUSS_NULL * lx - dx - r;
			final double right = GAUSS_NULL * lx - dx + r;
			final double top = -GAUSS_NULL * ly - dy - r;
			final double bottom = GAUSS_NULL * ly - dy + r;
			final Shape s = new Rectangle2D.Double(left, top, right - left,
					bottom - top);
			return getBounds(s);
		}
		case SEG_NORM:
		case SEG_END:
		default: {
			final double dx = segmentLength * 0.25;
			final double dy = segmentLength * 0.25;
			final double lx = segmentLength * 0.5;
			final double ly = segmentLength * 0.5;
			final double ll = segmentLength * 1.25;
			final double r = GAUSS_NULL * ll;
			final double left = -GAUSS_NULL * lx - dx - r;
			final double right = GAUSS_NULL * lx - dx + r;
			final double top = -GAUSS_NULL * ly - dy;
			final double bottom = GAUSS_NULL * ly - dy;
			final Shape s = new Rectangle2D.Double(left, top, right - left,
					bottom - top);
			return new BasicStroke(2f).createStrokedShape(s).getBounds2D();
		}
		}
	}

}
