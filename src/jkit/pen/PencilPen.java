package jkit.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.Random;

public class PencilPen extends SimplePen {

	private final Random rnd = new Random();

	protected double metricShiftX = 0.5;

	protected double metricShiftY = 0;

	protected double metricSpreadX = 0.5;

	protected double metricSpreadY = 0.125;

	protected double metricMaxLength = 1.125;

	protected int count = 25;

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

	private double dx;

	private double dy;

	private double lx;

	private double ly;

	private double ll;

	@Override
	public void setSegmentLength(final double segmentLength) {
		super.setSegmentLength(segmentLength);
		lx = segmentLength * metricSpreadX;
		ly = segmentLength * metricSpreadY;
		dx = segmentLength * metricShiftX;
		dy = segmentLength * metricShiftY;
		ll = segmentLength * metricMaxLength;
	}

	protected final double getNextX() {
		return rnd.nextGaussian() * lx + dx;
	}

	protected final double getNextY() {
		return rnd.nextGaussian() * ly + dy;
	}

	protected final double getNextLine() {
		return rnd.nextGaussian() * ll;
	}

	@Override
	public void draw(final Graphics2D g) {
		for (int i = 0; i < count; ++i) {
			final double x = getNextX();
			final double y = getNextY();
			final double len = getNextLine();
			g.draw(new Line2D.Double(x, y, x + len, y));
		}
	}

}
