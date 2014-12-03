package simulator;

import org.lwjgl.opengl.GL11;

public class Body {

	public double x, y, xspd, yspd, mass, radius;
	public double[][] circlePoints;

	public Body(double x, double y) {
		this(x, y, 0, 0, 1, 10);
	}

	public Body(double x, double y, double xspd, double yspd, double mass, double radius) {
		this.x = x;
		this.y = y;
		this.xspd = xspd;
		this.yspd = yspd;
		this.mass = mass;
		this.radius = radius;
		circlePoints = new double[2][16];
		setCirclePoints();
	}

	public void update(double timeScale) {
		x += xspd*timeScale;
		y += yspd*timeScale;
	}

	public void render(int xoffset, int yoffset, double scale) {
		double xd = xoffset;
		double yd = yoffset;
		GL11.glColor3d(1, 1, 1);
		GL11.glBegin(GL11.GL_POLYGON);
		for (int i = 0; i < circlePoints[0].length; i++) {
			GL11.glVertex2d(circlePoints[0][i] + x - xd, circlePoints[1][i] + y - yd);
		}
		GL11.glEnd();
	}

	public void setCirclePoints() {
		for (int i = 0; i < circlePoints[0].length; i++) {
			double length = circlePoints[0].length;
			double index = i;
			double xdraw = radius * Math.cos(index * 2.0 * Math.PI / length);
			double ydraw = radius * Math.sin(index * 2.0 * Math.PI / length);
			circlePoints[0][i] = xdraw;
			circlePoints[1][i] = ydraw;
		}
	}

}
