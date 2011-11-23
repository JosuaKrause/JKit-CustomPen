package jkit.gfx.pen;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class ArrowPen extends PencilPen {

	public ArrowPen() {
		metricMaxLength = 1.0;
		metricShiftX = 0;
		metricSpreadX = 0.1;
		metricShiftY = 0;
		metricSpreadY = 0.1;
	}

	@Override
	public void end(final Graphics2D g, final double rotation) {
		final double sl = segmentLength;
		final double sl2 = sl * 0.15;
		final double sl3 = sl * 0.50;
		final double dx = -0.5 * segmentLength;
		g.setStroke(new BasicStroke(1.5f));
		final int c2 = count / 2;
		for (int i = 0; i < c2; ++i) {
			final double x = getNextX() + dx;
			final double y = getNextY();
			// g.draw(new Line2D.Double(0, 0, sl, 0));
			g.draw(new Line2D.Double(x + sl, y, x + sl2, y + sl3));
			g.draw(new Line2D.Double(x + sl, y, x + sl2, y - sl3));
		}
	}

}
