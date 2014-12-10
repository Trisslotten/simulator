
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
	
	Body[] bodies;
	int followID = 5;
	int mousex, mousey;
	double scale = 0.000000001, timeScale = 1, oldTimeScale = timeScale;
	double xcam = -Display.getWidth() / 2, ycam = -Display.getHeight() / 2;
	double seconds = 0;
	
	protected void init() {
		xcam = -Display.getWidth() / 2;
		ycam = -Display.getHeight() / 2;
		bodies = new Body[8];
		// Sun
		bodies[0] = (new Body(0, 0, 0, 0, 1.98855 * Math.pow(10, 30), 696342000));
		// Mercury
		bodies[1] = (new Body(57909050000.0, 0, 0, 47362, 0.33022 * Math.pow(10, 24), 2439700));
		// Venus
		bodies[2] = (new Body(0, 108200000000.0, -35000, 0, 4.87 * Math.pow(10, 24), 6052000));
		
		// Earth
		bodies[3] = (new Body(-152098232000.0, 0, 0, -29780, 5.97219 * Math.pow(10, 24), 6371000));
		// Moon
		bodies[4] = (new Body(-152098232000.0 - 384399000, 0, 0, -29780 - 1022, 7.3477 * Math.pow(10, 22), 1737100));
		
		// Mars
		bodies[5] = (new Body(0, -227939100000.0, 24077, 0, 0.642 * Math.pow(10, 24), 338950));
		// Demos
		bodies[6] = (new Body(0, -227939100000.0 - 9376000, 24077 + 2138, 0, 1.0659 * Math.pow(10, 16), 11266.7));
		// Phobos
		bodies[7] = (new Body(0, -227939100000.0 + 23463200, 24077 - 1351.3, 0, 1.4762 * Math.pow(10, 15), 6200));
	}
	
	protected void update(double dt) {
		double scalespd = 1.00001;
		
		double xtarget = Mouse.getX();
		double ytarget = Display.getHeight() - Mouse.getY();
		
		int mouseWheel = Mouse.getDWheel();
		
		double timeScaleSpeed = 2;
		if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) {
			timeScale *= Math.pow(timeScaleSpeed, dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {
			timeScale /= Math.pow(timeScaleSpeed, dt);
		}
		
		if (mouseWheel < 0) {
			double newScale = scale / Math.pow(scalespd, Math.abs(mouseWheel * dt * 200000));
			
			double oldx = (xcam + xtarget) * scale;
			double newx = (xcam + xtarget) * newScale;
			double xdiff = oldx - newx;
			double oldy = (ycam + ytarget) * scale;
			double newy = (ycam + ytarget) * newScale;
			double ydiff = oldy - newy;
			xcam -= xdiff / scale;
			ycam -= ydiff / scale;
			scale = newScale;
		}
		if (mouseWheel > 0) {
			double newScale = scale * Math.pow(scalespd, Math.abs(mouseWheel * dt * 200000));
			
			double oldx = (xcam + xtarget) * scale;
			double newx = (xcam + xtarget) * newScale;
			double xdiff = oldx - newx;
			double oldy = (ycam + ytarget) * scale;
			double newy = (ycam + ytarget) * newScale;
			double ydiff = oldy - newy;
			xcam -= xdiff / scale;
			ycam -= ydiff / scale;
			scale = newScale;
		}
		
		for (int i = 0; i < bodies.length; i++) {
			for (int j = i + 1; j < bodies.length; j++) {
				handleGravity(bodies[i], bodies[j]);
			}
		}
		xcam += bodies[followID].xspd * scale * timeScale;
		ycam += bodies[followID].yspd * scale * timeScale;
		for (int i = 0; i < bodies.length; i++) {
			bodies[i].update(timeScale);
		}
		
		seconds += timeScale;
	}
	
	protected void render() {
		for (Body body : bodies) {
			body.render((int) xcam, (int) ycam, scale);
		}
		
		int xmouse = Mouse.getX();
		int ymouse = Display.getHeight() - Mouse.getY();
		if (Mouse.isButtonDown(0)) {
			xcam += mousex - xmouse;
			ycam += mousey - ymouse;
		}
		mousex = xmouse;
		mousey = ymouse;
	}
	
	private void handleGravity(Body a, Body b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		double angle = Math.atan2(dy, dx);
		double g = 0.0000000000667384;
		double force = g * a.mass * b.mass / (distance * distance);
		a.xspd -= timeScale * Math.cos(angle) * force / a.mass;
		a.yspd -= timeScale * Math.sin(angle) * force / a.mass;
		b.xspd += timeScale * Math.cos(angle) * force / b.mass;
		b.yspd += timeScale * Math.sin(angle) * force / b.mass;
	}
	
	private double renderFPS = 288.0;
	private int width, height;
	public int updates = 0;
	
	public void run() {
		if (!(width > 0 && height > 0)) {
			width = 1280;
			height = 700;
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
			
			dt += (now - lastTime) * renderFPS;
			lastTime = now;
			while (dt >= 1) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				render();
				Display.update();
				dt -= 1.0;
				if (Display.wasResized()) {
					resize();
				}
			}
			
			if (getTime() - timer > 1) {
				timer += 1;
				double days = seconds / 86400;
				Display.setTitle("updates: " + this.updates + " | ups: " + updates + " | Dayspassed: " + (long) days + " | Timescale: " + timeScale);
				updates = 0;
				
			}
			
		}
		Display.destroy();
	}
	
	protected void resize() {
		this.width = Display.getWidth();
		this.height = Display.getHeight();
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		// GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		
	}
	
	private double nanoTime() {
		double nanoTime = System.nanoTime();
		return nanoTime / 1000000000.0;
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
	
	private void glinit(int width, int height) {
		this.width = width;
		this.height = height;
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			
			Display.setTitle("updates: " + updates);
			Display.setResizable(true);
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
