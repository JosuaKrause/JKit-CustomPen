package jkit.gfx.pen;

import java.awt.Graphics2D;

public abstract class PenAdapter implements Pen {

    @Override
    public void start(final Graphics2D g, final double rotation) {
        draw(g, rotation);
    }

    @Override
    public void end(final Graphics2D g, final double rotation) {
        draw(g, rotation);
    }

}
