package jkit.gfx;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * An object that can be drawn.
 * 
 * @author Joschi <josua.krause@gmail.com>
 */
public abstract class Drawable {

  /** The bounding box cache. */
  private Rectangle2D box;

  /**
   * Draws to the given graphics context.
   * 
   * @param gfx The graphics context.
   */
  public abstract void draw(Graphics2D gfx);

  /**
   * Computes the bounding box. The bounding box does not change and will be
   * cached.
   * 
   * @return The bounding box.
   */
  public Rectangle2D getBounds() {
    if(box == null) {
      box = computeBounds();
    }
    return box;
  }

  /**
   * Getter.
   * 
   * @return Computes the actual bounding box.
   */
  protected abstract Rectangle2D computeBounds();

  /**
   * Draws to the given graphics context if the {@link Drawable} is visible.
   * 
   * @param gfx The graphics context.
   * @param viewport The current viewport of the graphics context. The viewport
   *          may be <code>null</code>, however then the {@link Drawable} is
   *          always drawn.
   */
  public void drawIfVisible(final Graphics2D gfx, final Rectangle2D viewport) {
    if(viewport != null && !viewport.intersects(getBounds())) return;
    draw(gfx);
  }

}
