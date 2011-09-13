package jkit.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

public abstract class SimplePen implements Pen {

	protected double segmentLength;

	protected Color color;

	public SimplePen(final Color color) {
		this(color, 10.0);
	}

	public SimplePen(final Color color, final double segmentLength) {
		this.color = color;
		setSegmentLength(segmentLength);
	}

	@Override
	public void prepare(final Graphics2D g, final Shape s) {
		g.setColor(color);
	}

	@Override
	public void start(final Graphics2D g) {
		draw(g);
	}

	@Override
	public void end(final Graphics2D g) {
		draw(g);
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
