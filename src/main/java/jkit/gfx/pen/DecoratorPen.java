package jkit.gfx.pen;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A pen decorating another pen.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public abstract class DecoratorPen implements Pen {

  /** The other pen. */
  protected final Pen pen;

  /**
   * Creates a decorator pen.
   * 
   * @param pen The other pen.
   */
  public DecoratorPen(final Pen pen) {
    this.pen = pen;
  }

  @Override
  public void prepare(final Graphics2D g, final Shape s) {
    pen.prepare(g, s);
  }

  @Override
  public void start(final Graphics2D g, final int no, final double rotation) {
    pen.start(g, no, rotation);
  }

  @Override
  public void draw(final Graphics2D g, final int no, final double rotation) {
    pen.draw(g, no, rotation);
  }

  @Override
  public void end(final Graphics2D g, final int no, final double rotation) {
    pen.end(g, no, rotation);
  }

  @Override
  public double segmentLength() {
    return pen.segmentLength();
  }

  @Override
  public Rectangle2D getBoundingBox(final int type, final double rotation) {
    return pen.getBoundingBox(type, rotation);
  }

}
