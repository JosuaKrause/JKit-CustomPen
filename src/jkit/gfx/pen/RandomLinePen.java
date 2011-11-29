package jkit.gfx.pen;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Random;

public class RandomLinePen extends DecoratorPen {

    private final Random rnd = new Random();

    public RandomLinePen(final Pen pen) {
        super(pen);
    }

    @Override
    public void prepare(final Graphics2D g, final Shape s) {
        rnd.setSeed(s.getBounds2D().hashCode());
        super.prepare(g, s);
    }

}
