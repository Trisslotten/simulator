
package simulator.testing2;

import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Body {
	
	static Random rand = new Random();
	public double x, y, xspd, yspd, mass, radius;
	public double[][] circlePoints;
	
	public boolean hasCompared = false;
	
	public Body(double x, double y) {
		this(x, y, 0.0, 0.0, 10000000000.0, 10000.0);
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
		x += xspd * timeScale;
		y += yspd * timeScale;
	}
	
	public void render(int xoffset, int yoffset, double scale) {
		
		double xd = xoffset;
		double yd = yoffset;
		if (!((x + radius) * scale - xd < 0 || (y + radius) * scale - yd < 0 || (x - radius) * scale - xd > Display.getWidth() || (y - radius) * scale - yd > Display.getHeight())) {
			GL11.glColor3d(1, 1, 1);
			GL11.glBegin(GL11.GL_POLYGON);
			double sizelimit = 0.75;
			
			if (circlePoints[0][0] * scale < sizelimit) {
				double length = circlePoints[0].length;
				for (int i = 0; i < circlePoints[0].length; i++) {
					double index = i;
					double xdraw = scale * (x) + sizelimit * Math.cos(index * 2.0 * Math.PI / length) - xd;
					double ydraw = scale * (y) + sizelimit * Math.sin(index * 2.0 * Math.PI / length) - yd;
					GL11.glVertex2d(xdraw, ydraw);
				}
			} else {
				for (int i = 0; i < circlePoints[0].length; i++) {
					double xdraw = scale * (circlePoints[0][i] + x) - xd;
					double ydraw = scale * (circlePoints[1][i] + y) - yd;
					GL11.glVertex2d(xdraw, ydraw);
				}
			}
			GL11.glEnd();
		}
	}
	
	public boolean collided(Body b) {
		double dx = x - b.x;
		double dy = y - b.y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		return radius + b.radius < distance;
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
	
	public void addspd(double xspd, double yspd) {
		this.xspd += xspd;
		this.yspd += yspd;
		
	}
	
}
