package jkit.pen;

import java.awt.Graphics2D;

public interface Pen {

	void start(Graphics2D g);

	void draw(Graphics2D g);

	void end(Graphics2D g);

	double segmentLength();

}
