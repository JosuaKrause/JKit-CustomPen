package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

public abstract class SimplePen extends PenAdapter {

	protected double segmentLength;

	protected Color color;

	private boolean initialized;

	public SimplePen(final Color color) {
		this(color, 10.0);
	}

	public SimplePen(final Color color, final double segmentLength) {
		this.color = color;
		this.segmentLength = segmentLength;
		initialized = false;
	}

	protected Stroke usedStroke;

	@Override
	public void prepare(final Graphics2D g, final Shape s) {
		if (!initialized) {
			setColor(color);
			setSegmentLength(segmentLength);
			initialized = true;
		}
		if (color != null) {
			g.setColor(color);
		}
		postPrepare(g);
	}

	protected void postPrepare(final Graphics2D g) {
		usedStroke = g.getStroke();
	}

	private static final BasicStroke BASE = new BasicStroke(1f);

	protected Rectangle2D getBounds(final Shape s) {
		return (usedStroke != null ? usedStroke.createStrokedShape(s) : BASE
				.createStrokedShape(s)).getBounds2D();
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public Color color() {
		return color;
	}

	public void setSegmentLength(final double segmentLength) {
		this.segmentLength = segmentLength;
	}

	@Override
	public double segmentLength() {
		return segmentLength;
	}

}
