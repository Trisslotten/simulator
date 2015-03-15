package simulator;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GUI {

	public static void render(double xoffset, double yoffset, double scale, double xcenter, double ycenter) {
		double width = 3;
		double height = 10;
		double au = 149597870700.0;
		double auinpixels = scale*au;
		double ausonscreen = Display.getWidth()/auinpixels;
		for(int i=0;i<ausonscreen;i++) {
			
		}
		
		
	}

	public static void drawRekt(double x, double y, double x2, double y2) {
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2d(x, y);
			GL11.glVertex2d(x, y2);
			GL11.glVertex2d(x2, y2);
			GL11.glVertex2d(x2, y);
		}
		GL11.glEnd();
	}

}

