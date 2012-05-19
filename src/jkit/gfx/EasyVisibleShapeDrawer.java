package jkit.gfx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

public class EasyVisibleShapeDrawer extends AbstractShapeDrawer {

  private final Stroke inner;

  private final Stroke outer;

  private final Color fg;

  private final Color bg;

  public EasyVisibleShapeDrawer() {
    this(Color.WHITE, Color.BLACK, 1, 1);
  }

  public EasyVisibleShapeDrawer(final Color fg, final Color bg,
      final double radius, final double outerRadius) {
    inner = new BasicStroke((float) radius);
    outer = new BasicStroke((float) (radius + outerRadius + 1));
    this.fg = fg;
    this.bg = bg;
  }

  @Override
  public void draw(final Graphics gfx, final Shape outline) {
    final Graphics2D g = (Graphics2D) gfx.create();
    g.setColor(bg);
    g.fill(outer.createStrokedShape(outline));
    g.setColor(fg);
    g.fill(inner.createStrokedShape(outline));
    g.dispose();
  }

  @Override
  public Rectangle2D getBounds(final Shape s) {
    return outer.createStrokedShape(s).getBounds2D();
  }

}
