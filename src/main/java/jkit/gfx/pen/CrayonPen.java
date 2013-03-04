package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A pen with a crayon pen style.
 * 
 * @author Joschi <josua.krause@gmail.com>
 * 
 */
public class CrayonPen extends CachedRandomPen {

	private double thickness;

	private double pressure;

	public CrayonPen(final Color color, final double thickness) {
		this(color, thickness, 2.0);
	}

	public CrayonPen(final Color color, final double thickness,
			final double pressure) {
		super(color);
		this.thickness = thickness;
		this.pressure = pressure;
	}

	public void setPressure(final double pressure) {
		this.pressure = pressure;
		invalidate();
	}

	public double getPressure() {
		return pressure;
	}

	public void setThickness(final double thickness) {
		this.thickness = thickness;
		invalidate();
	}

	public double getThickness() {
		return thickness;
	}

	@Override
	public void prepare(final Graphics2D g, final Shape s) {
		super.prepare(g, s);
		g.setStroke(new BasicStroke(1f));
		postPrepare(g);
	}

	private Rectangle2D bbox;

	@Override
	protected void invalidate() {
		super.invalidate();
		bbox = null;
	}

	@Override
	protected void drawSegment(final Graphics2D g) {
		final int t = (int) Math.round(thickness * pressure);
		final double ht = thickness * 0.5;
		for (double pos = 0.0; pos <= segmentLength + 2.0; pos += 1.0) {
			for (int i = 0; i < t; ++i) {
				final double h = rndNextDouble() * thickness;
				final Shape s = new Rectangle2D.Double(pos - 0.5, h - ht - 0.5,
						1.0, 1.0);
				g.fill(s);
			}
		}
	}

	@Override
	public Rectangle2D getBoundingBox(final int type, final double rotation) {
		if (bbox == null) {
			final double posMin = 0.0;
			final double posMax = segmentLength + 2.0;
			final double ht = thickness * 0.5;
			final double minH = 0;
			final double maxH = thickness;
			final double left = posMin - 0.5;
			final double right = posMax + 0.5;
			final double top = minH - ht - 0.5;
			final double bottom = maxH - ht + 0.5;
			final Shape s = new Rectangle2D.Double(left, top, right - left,
					bottom - top);
			bbox = getBounds(s);
		}
		return bbox;
	}

}
