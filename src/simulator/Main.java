
package simulator;

import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Main implements Runnable {
	
	Body sun, earth;
	double scale = 0.000000001, timeScale = 5;
	double xcam, ycam;
	double seconds = 0;
	
	protected void init() {
		sun = new Body(0, 0, 0, 0, 1.98855 * Math.pow(10, 30), 696342000);
		earth = new Body(152098232000.0, 0, 0, 29780, 5.97219 * Math.pow(10, 24), 6371000);
		
	}
	
	protected void update(double dt) {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			timeScale *= 1.0001;
			
		}
		double scalespd = 1.0001;
		if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
			double newScale = scale / scalespd;
			xcam -= width * (1 - newScale / scale) * scale / 2;
			ycam -= height * (1 - newScale / scale) * scale / 2;
			scale = newScale;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
			double newScale = scale * scalespd;
			xcam -= width * (1 - newScale / scale) * scale / 2;
			ycam -= height * (1 - newScale / scale) * scale / 2;
			scale = newScale;
		}
		
		double camspd = 200;
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			ycam += dt * camspd;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			ycam -= dt * camspd;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			xcam -= dt * camspd;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			xcam += dt * camspd;
		}
		
		// System.out.println();
		sun.update(timeScale);
		earth.update(timeScale);
		double dx = sun.x - earth.x;
		double dy = sun.y - earth.y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		double angle = Math.atan2(dy, dx);
		double g = 0.0000000000666666667;
		double force = g * sun.mass * earth.mass / (distance * distance);
		sun.xspd -= timeScale * Math.cos(angle) * force / sun.mass;
		sun.yspd -= timeScale * Math.sin(angle) * force / sun.mass;
		earth.xspd += timeScale * Math.cos(angle) * force / earth.mass;
		earth.yspd += timeScale * Math.sin(angle) * force / earth.mass;
		
		seconds += timeScale;
	}
	
	protected void render() {
		sun.render((int) xcam, (int) ycam, scale);
		earth.render((int) xcam, (int) ycam, scale);
	}
	
	public void run() {
		if (!(width > 0 && height > 0)) {
			width = 1720;
			height = 900;
		}
		glinit(width, height);
		init();
		
		double timer = getTime();
		int updates = 0;
		double delta = getUpdateDelta();
		double dt = 0;
		double lastTime = nanoTime();
		while (running && !Display.isCloseRequested()) {
			
			delta = getUpdateDelta();
			update(delta);
			updates++;
			this.updates++;
			
			double now = nanoTime();
			
			dt += (now - lastTime) * 120;
			lastTime = now;
			// System.out.println(dt);
			while (dt >= 1) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				render();
				dt -= 1.0;
			}
			
			if (getTime() - timer > 1) {
				timer += 1;
				double days = seconds / 86400;
				Display.setTitle("updates: " + this.updates + " | ups: " + updates + " | Dayspassed: " + (long)days + " | Timescale: " + timeScale);
				updates = 0;
				
			}
			Display.update();
		}
		Display.destroy();
	}
	
	private double nanoTime() {
		double nanoTime = System.nanoTime();
		return nanoTime / 1000000000.0;
	}
	
	protected int width, height;
	
	public int updates = 0;
	
	public static void main(String[] args) {
		new Main().start();
	}
	
	private void glinit(int width, int height) {
		this.width = width;
		this.height = height;
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			
			Display.setTitle("updates: " + updates);
			Display.create();
		} catch (LWJGLException e) {
			System.exit(0);
		}
		version = glGetString(GL_VERSION);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glViewport(0, 0, width, height);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
	}
	
	public double getMillis() {
		return ((double) (Sys.getTime() * 1000)) / (double) (Sys.getTimerResolution());
	}
	
	public double getTime() {
		return ((double) Sys.getTime()) / (double) Sys.getTimerResolution();
	}
	
	double oldUpdateTime, newUpdateTime;
	
	public double getUpdateDelta() {
		oldUpdateTime = newUpdateTime;
		newUpdateTime = nanoTime();
		return (newUpdateTime - oldUpdateTime);
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	protected String title = "LWJGL";
	protected double UPDATES_PER_SECOND = 60.0;
	protected String version = "";
	
	protected boolean running = false;
	private Thread thread;
	
	public void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}
	
	public void stop() {
		running = false;
	}
}
