package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

public class FacingTrianglePen extends SimplePen {

	private Shape triangle;

	private double width;

	private double distance;

	public FacingTrianglePen() {
		this(Color.BLACK, 30.0);
	}

	public FacingTrianglePen(final Color color, final double segLen) {
		super(color, segLen);
	}

	@Override
	public void setSegmentLength(final double segLen) {
		super.setSegmentLength(segLen);
		width = segLen * 0.5;
		distance = segLen * 0.5;
		triangle = createTriangleFor(width);
	}

	private Shape createTriangleFor(final double width) {
		final GeneralPath path = new GeneralPath();
		path.moveTo(0.0, 0.0);
		path.lineTo(width, 0.0);
		path.lineTo(width * 0.5, width * 0.5);
		path.closePath();
		return path;
	}

	@Override
	public void draw(final Graphics2D gfx) {
		final Graphics2D g = (Graphics2D) gfx.create();
		g.translate(width, -distance);
		g.fill(triangle);
		g.translate(0, distance * 2.0);
		final AffineTransform at = AffineTransform.getQuadrantRotateInstance(2);
		at.translate(-width, 0.0);
		g.transform(at);
		g.fill(triangle);
		g.dispose();
		gfx.setColor(Color.BLACK);
		gfx.translate(width, -distance);
		gfx.draw(triangle);
		gfx.translate(0, distance * 2.0);
		gfx.transform(at);
		gfx.draw(triangle);

	}

}
