package jkit.pen;

import java.awt.Graphics2D;

public abstract class SimplePen implements Pen {

	protected double segmentLength;

	public SimplePen() {
		this(10.0);
	}

	public SimplePen(final double segmentLength) {
		setSegmentLength(segmentLength);
	}

	@Override
	public void start(final Graphics2D g) {
		draw(g);
	}

	@Override
	public void end(final Graphics2D g) {
		draw(g);
	}

	public void setSegmentLength(final double segmentLength) {
		this.segmentLength = segmentLength;
	}

	@Override
	public double segmentLength() {
		return segmentLength;
	}

}
