package jkit.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.Random;

public class PencilPen extends SimplePen {

	private final Random rnd = new Random();

	public PencilPen() {
		this(10.0);
	}

	public PencilPen(final double segmentLength) {
		super(new Color(0x40303030, true), segmentLength);
	}

	@Override
	public void prepare(final Graphics2D g, final Shape s) {
		super.prepare(g, s);
		g.setStroke(new BasicStroke(.5f));
		rnd.setSeed(s.getBounds2D().hashCode());
	}

	@Override
	public void draw(final Graphics2D g) {
		final double dx = segmentLength * 0.125;
		final double dy = segmentLength * 0.125;
		final double lx = segmentLength * 0.25;
		final double ly = segmentLength * 0.125;
		final double ll = segmentLength * 1.125;
		for (int i = 0; i < 25; ++i) {
			final double y = rnd.nextGaussian() * ly - dy;
			final double x = rnd.nextGaussian() * lx - dx;
			final double len = rnd.nextGaussian() * ll;
			g.draw(new Line2D.Double(x, y, x + len, y));
		}
	}

}
