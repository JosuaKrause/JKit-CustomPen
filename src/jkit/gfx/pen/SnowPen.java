package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import jkit.gfx.AbstractShapeDrawer;

public class SnowPen extends DecoratorPen {

	private final AbstractShapeDrawer origin;

	private final CrayonPen crayon;

	private final double maxSlope;

	private double maxThickness;

	public SnowPen(final AbstractShapeDrawer origin, final double thickness) {
		this(origin, thickness, 2.0);
	}

	public SnowPen(final AbstractShapeDrawer origin, final double thickness,
			final double pressure) {
		this(origin, thickness, pressure, Math.PI / 6.0);
	}

	public SnowPen(final AbstractShapeDrawer origin, final double thickness,
			final double pressure, final double maxSlope, final boolean degrees) {
		this(origin, thickness, pressure, degrees ? Math.toRadians(maxSlope)
				: maxSlope, 10.0);
	}

	public SnowPen(final AbstractShapeDrawer origin, final double thickness,
			final double pressure, final double maxSlope) {
		this(origin, thickness, pressure, maxSlope, 10.0);
	}

	public SnowPen(final AbstractShapeDrawer origin, final double thickness,
			final double pressure, final double maxSlope,
			final double segmentLength) {
		super(new CrayonPen(Color.WHITE, thickness, pressure));
		this.origin = origin;
		crayon = (CrayonPen) pen;
		setSegmentLength(segmentLength);
		setThickness(thickness);
		this.maxSlope = maxSlope;
	}

	public void setSegmentLength(final double segmentLength) {
		crayon.setSegmentLength(segmentLength);
	}

	public void setThickness(final double thickness) {
		maxThickness = thickness;
	}

	public double getThickness() {
		return maxThickness;
	}

	@Override
	public void prepare(final Graphics2D g, final Shape s) {
		origin.draw(g, s);
		g.translate(0.0, -getThickness() * 2.0 / 3.0);
		crayon.prepare(g, s);
	}

	@Override
	public Rectangle2D getSpecialBounds(final Shape s) {
		return origin.getBounds(s);
	}

	private static final double MAX_SLOPE = Math.PI * 0.5;

	private static final double SLOPE_STEP = MAX_SLOPE * 2;

	private static double slope(double rot) {
		while (Math.abs(rot) > MAX_SLOPE) {
			rot -= SLOPE_STEP;
		}
		return Math.abs(rot);
	}

	private void adjustThickness(final double rot) {
		final double factor = slope(rot) / MAX_SLOPE;
		crayon.setThickness(maxThickness * factor);
	}

	private boolean isCorrectRotation(final double rot) {
		return slope(rot) <= maxSlope;
	}

	@Override
	public void start(final Graphics2D g, final int no, final double rotation) {
		if (isCorrectRotation(rotation)) {
			adjustThickness(rotation);
			super.start(g, no, rotation);
		}
	}

	@Override
	public void end(final Graphics2D g, final int no, final double rotation) {
		if (isCorrectRotation(rotation)) {
			adjustThickness(rotation);
			super.end(g, no, rotation);
		}
	}

	@Override
	public void draw(final Graphics2D g, final int no, final double rotation) {
		if (isCorrectRotation(rotation)) {
			adjustThickness(rotation);
			super.draw(g, no, rotation);
		}
	}

	@Override
	public Rectangle2D getBoundingBox(final int type, final double rotation) {
		if (isCorrectRotation(rotation)) {
			adjustThickness(rotation);
			return super.getBoundingBox(type, rotation);
		}
		return new Rectangle2D.Double();
	}

}
