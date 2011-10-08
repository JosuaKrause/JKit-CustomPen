package jkit.gfx.pen;

import java.awt.Graphics2D;
import java.awt.Shape;

public interface Pen {

    /**
     * Prepares the pen for the upcoming shape. This method is called once per
     * shape before any drawing happens. The graphics object can be manipulated.
     * Those changes stay during the complete drawing process of the current
     * shape.
     * 
     * @param g
     *            The graphics object.
     * @param s
     *            The shape to be finally drawn.
     */
    void prepare(Graphics2D g, Shape s);

    /**
     * Provides a special method to draw the beginning of a shape section. This
     * method is called instead of {@link #draw(Graphics2D, double)} at the
     * beginning of a shape section (after each <code>moveTo</code> command).
     * When no special start should be drawn one can just call
     * {@link #draw(Graphics2D, double)}.
     * 
     * @param g
     *            The graphics to draw on.
     * @param rotation
     *            The absolute rotation of the line segment.
     * 
     * @see #draw(Graphics2D, double)
     */
    void start(Graphics2D g, double rotation);

    /**
     * Draws one line segment of the length {@link #segmentLength()}. A shape is
     * split into such segments. The graphics object is rotated such that the
     * line starts at {@code (0, 0)} and goes to
     * <code>({@link #segmentLength()}, 0)</code>.
     * 
     * @param g
     *            The graphics to draw on.
     * @param rotation
     *            The absolute rotation of the line segment.
     */
    void draw(Graphics2D g, double rotation);

    /**
     * Provides a special method to draw the end of a shape section. This method
     * is called instead of {@link #draw(Graphics2D, double)} at the end of a
     * shape section (before each <code>moveTo</code> command or the end of the
     * shape). When no special end should be drawn one can just call
     * {@link #draw(Graphics2D, double)}.
     * 
     * @param g
     *            The graphics to draw on.
     * @param rotation
     *            The absolute rotation of the line segment.
     * 
     * @see #draw(Graphics2D, double)
     */
    void end(Graphics2D g, double rotation);

    /**
     * @return The length of segments of this pen. The drawn shape is split into
     *         line segments of this length.
     */
    double segmentLength();

}
