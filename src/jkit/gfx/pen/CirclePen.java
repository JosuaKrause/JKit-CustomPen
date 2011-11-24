package jkit.gfx.pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class CirclePen extends SimplePen {

	private Ellipse2D circ;

	public CirclePen() {
		this(10.0);
	}

	public CirclePen(final double segLen) {
		super(Color.YELLOW, segLen);
	}

	@Override
	public void setSegmentLength(final double segLen) {
		super.setSegmentLength(segLen);
		circ = new Ellipse2D.Double(0, -segLen * 0.25, segLen, segLen * 0.5);
	}

	@Override
	public void start(final Graphics2D g, final int no, final double rotation) {
		g.setColor(Color.BLUE);
		g.fill(circ);
		g.setColor(Color.BLACK);
		g.draw(circ);
	}

	@Override
	public void draw(final Graphics2D g, final int no, final double rotation) {
		g.fill(circ);
		g.setColor(Color.BLACK);
		g.draw(circ);
	}

	@Override
	public void end(final Graphics2D g, final int no, final double rotation) {
		g.setColor(Color.RED);
		g.fill(circ);
		g.setColor(Color.BLACK);
		g.draw(circ);
	}

	@Override
	public Rectangle2D getBoundingBox(final int type, final double rotation) {
		return getBounds(circ);
	}

}
