package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import jkit.gfx.AbstractShapeDrawer;
import jkit.gfx.SimpleShapeDrawer;
import jkit.gfx.pen.ArrowPen;
import jkit.gfx.pen.BloodTrailPen;
import jkit.gfx.pen.CirclePen;
import jkit.gfx.pen.CrayonPen;
import jkit.gfx.pen.Pen;
import jkit.gfx.pen.PencilPen;
import jkit.gfx.pen.SnowPen;

public class Test {

	private static JFrame jFrame;
	private static Shape shape;
	private static AbstractShapeDrawer shapeDrawer;
	private static int curPen = 0;
	private static Pen[] pens = new Pen[] {

			null,

			new PencilPen(),

			new BloodTrailPen(),

			new ArrowPen(),

			new CrayonPen(Color.GREEN, 5.0),

			new SnowPen(
					new SimpleShapeDrawer(new BasicStroke(5f), Color.BLACK),
					5.0, 2.0, 45, true),

			// new FacingTrianglePen(Color.BLUE, 30.0),

			new CirclePen(),

	};

	private static int curBg = 0;

	private static Color[] bgs = new Color[] {

	null,

	Color.WHITE,

	Color.BLACK,

	Color.GREEN,

	Color.RED,

	};

	public static final void setPen() {
		final Pen p = pens[curPen];
		shapeDrawer = AbstractShapeDrawer.getShapeDrawerForPen(p);
	}

	public static void main(final String[] args) {
		setPen();
		shape = createShape();
		jFrame = new JFrame();
		bgs[0] = jFrame.getBackground();
		jFrame.add(new JComponent() {

			private static final long serialVersionUID = 2257038010168418793L;

			@Override
			protected void paintComponent(final Graphics gfx) {
				final Graphics2D g = (Graphics2D) gfx.create();
				g.setColor(bgs[curBg]);
				final Dimension dim = jFrame.getSize();
				g.fill(new Rectangle2D.Double(0, 0, dim.getWidth(), dim
						.getHeight()));
				g.setColor(Color.BLACK);
				shapeDrawer.draw(g, shape);
				g.setColor(new Color(0x80ff80ff, true));
				g.fill(shapeDrawer.getBounds(shape));
				g.dispose();
			}

		});
		jFrame.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				switch (e.getButton()) {
				case MouseEvent.BUTTON1:
					++curPen;
					if (curPen >= pens.length) {
						curPen = 0;
					}
					setPen();
					break;
				case MouseEvent.BUTTON3:
					++curBg;
					if (curBg >= bgs.length) {
						curBg = 0;
					}
					jFrame.setBackground(bgs[curBg]);
					break;
				}
				jFrame.repaint();
			}

		});
		jFrame.setSize(new Dimension(800, 600));
		jFrame.setLocationRelativeTo(null);
		jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
