package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.JFrame;

import jkit.pen.AbstractShapeDrawer;
import jkit.pen.BloodTrailPen;
import jkit.pen.CirclePen;
import jkit.pen.FacingTrianglePen;
import jkit.pen.Pen;
import jkit.pen.PenShapeDrawer;
import jkit.pen.PencilPen;

public class Test {

	private static JFrame jFrame;
	private static Shape shape;
	private static AbstractShapeDrawer shapeDrawer;

	public static void main(final String[] args) {
		shape = createShape();
		Pen p;
		final int a = 2;
		switch (a) {
		case 0:
			p = new FacingTrianglePen(Color.BLUE, 30.0);
			break;
		case 1:
			p = new BloodTrailPen();
			break;
		case 2:
			p = new PencilPen();
			break;
		default:
			p = new CirclePen();
			break;
		}
		shapeDrawer = new PenShapeDrawer(p);
		jFrame = new JFrame();
		jFrame.add(new JComponent() {

			private static final long serialVersionUID = 2257038010168418793L;

			@Override
			protected void paintComponent(final Graphics gfx) {
				final Graphics2D g = (Graphics2D) gfx.create();
				g.setColor(Color.BLACK);
				g.draw(shape);
				shapeDrawer.draw(g, shape);
				g.dispose();
			}

		});
		jFrame.setSize(new Dimension(800, 600));
		jFrame.setLocationRelativeTo(null);
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jFrame.setVisible(true);
	}

	private static Shape createShape() {
		final GeneralPath path = new GeneralPath();
		path.moveTo(100.0, 100.0);
		path.lineTo(150.0, 200.0);
		path.quadTo(250.0, 250.0, 300.0, 150.0);
		path.curveTo(400.0, 80.0, 450.0, 160.0, 500.0, 400.0);
		path.moveTo(600.0, 300.0);
		path.quadTo(650.0, 250.0, 700.0, 300.0);
		path.quadTo(700.0, 350.0, 650.0, 350.0);
		path.closePath();
		path.moveTo(200.0, 350.0);
		path.quadTo(200.0, 300.0, 250.0, 300.0);
		path.quadTo(300.0, 300.0, 300.0, 350.0);
		path.quadTo(300.0, 400.0, 250.0, 400.0);
		path.quadTo(200.0, 400.0, 200.0, 350.0);
		return path;
	}

}
