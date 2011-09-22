package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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

	private Rectangle rect;

	@Override
	public void setSegmentLength(final double segmentLength) {
		super.setSegmentLength(segmentLength);
		dx = segmentLength * 0.25;
		dy = segmentLength * 0.25;
		lx = segmentLength * 0.5;
		ly = segmentLength * 0.5;
		llb = segmentLength * 1.00;
		lls = segmentLength * 1.25;
		rect = new Rectangle2D.Double(2 * lx - dx - lls, 2 * ly - dy - llb
				* 0.5, lls * 3, llb).getBounds();
	}

	private double dx;

	private double dy;

	private double lx;

	private double ly;

	private double lls;

	private double llb;

	@Override
	public Rectangle getSegmentBoundingBox() {
		return rect;
	}

	@Override
	public void start(final Graphics2D g) {
		for (int i = 0; i < 100; ++i) {
			final double y = rnd.nextGaussian() * ly - dy;
			final double x = rnd.nextGaussian() * lx - dx;
			final double len = rnd.nextGaussian() * llb;
			final double l2 = len * 0.5;
			g.fill(new Ellipse2D.Double(x - l2, y - l2, len, len));
		}
	}

	@Override
	public void draw(final Graphics2D g) {
		g.setStroke(new BasicStroke(2f));
		for (int i = 0; i < 25; ++i) {
			final double y = rnd.nextGaussian() * ly - dy;
			final double x = rnd.nextGaussian() * lx - dx;
			final double len = rnd.nextGaussian() * lls;
			g.draw(new Line2D.Double(x, y, x + len, y));
		}
	}

}
