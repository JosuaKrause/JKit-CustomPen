package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.JFrame;

import jkit.pen.CirclePen;
import jkit.pen.ShapeDrawer;


public class Test {

	private static JFrame jFrame;
	private static Shape shape;
	private static ShapeDrawer shapeDrawer;

	public static void main(final String[] args) {
		shape = createShape();
		shapeDrawer = new ShapeDrawer(new CirclePen());
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
		return path;
	}

}
