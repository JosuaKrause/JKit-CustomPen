package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class LinePen extends SimplePen {

	private final Stroke stroke;

	public LinePen() {
		this(null);
	}

	public LinePen(final Stroke stroke) {
		this(stroke, null);
	}

	public LinePen(final Stroke stroke, final Color color) {
		this(stroke, color, 10.0);
	}

	public LinePen(final Stroke stroke, final Color color,
			final double segmentLength) {
		super(color, segmentLength);
		this.stroke = stroke;
	}

	@Override
	public void prepare(final Graphics2D g, final Shape s) {
		if (stroke != null) {
			g.setStroke(stroke);
		}
		super.prepare(g, s);
	}

	@Override
	public void draw(final Graphics2D g, final double rotation) {
		g.draw(new Line2D.Double(0, 0, segmentLength, 0));
	}

	@Override
	public Rectangle2D getBoundingBox(final int type, final double rotation) {
		final Shape s = new Line2D.Double(0, 0, segmentLength, 0);
		return getBounds(s);
	}

}
