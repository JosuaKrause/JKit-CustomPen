package jkit.gfx.pen;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

public interface Pen {

	void prepare(Graphics2D g, Shape s);

	void start(Graphics2D g);

	void draw(Graphics2D g);

	void end(Graphics2D g);

	Rectangle getSegmentBoundingBox();

	double segmentLength();

}
