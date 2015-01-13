
package simulator.testing2;

import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Main implements Runnable {
	
	int n = 500;
	ArrayList<Body> bodies = new ArrayList<Body>();
	int followID = 0;
	int mousex, mousey;
	double scale = 0.0001, timeScale = 10000, oldTimeScale = timeScale;
	double xcam = -Display.getWidth() / 2, ycam = -Display.getHeight() / 2;
	double seconds = 0;
	
	protected void init() {
		xcam = -Display.getWidth() / 2;
		ycam = -Display.getHeight() / 2;
		
		Random rand = new Random();
		double period = 0;
		
		bodies.add(new Body(0,0,0,0,Math.pow(1, 29),10000));
		
		for (int i = 0; i < n; i++) {
			period += Math.PI / (rand.nextInt(10) + 4);
			double radius = ((double) rand.nextInt(1000000) + 1000.0);
			
			double x = radius * Math.cos(period);
			double y = radius * Math.sin(period);
			double velocity = 0.0001;
			double xspd = velocity*Math.cos(period+Math.PI/4);
			double yspd = velocity*Math.sin(period+Math.PI/4);
			
			bodies.add(new Body(x, y,xspd,yspd,10000000,1000));
		}
	}
	
	protected void update(double dt) {
		for (int i = 0; i < bodies.size(); i++) {
			for (int j = i + 1; j < bodies.size(); j++) {
				handleGravity(i, j);
			}
		}
		for (int i = 0; i < bodies.size(); i++) {
			for (int j = i + 1; j < bodies.size(); j++) {
				handleCollisions(i, j);
			}
		}
		if (followID >= 0) {
			xcam += bodies.get(followID).xspd * scale * timeScale;
			ycam += bodies.get(followID).yspd * scale * timeScale;
		}
		
		for (int i = 0; i < bodies.size(); i++) {
			bodies.get(i).update(timeScale);
		}
		seconds += timeScale;
	}
	
	protected void render() {
		double dt = 1.0 / renderFPS;
		
		double timeScaleSpeed = 2;
		if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
			timeScale *= Math.pow(timeScaleSpeed, dt);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
			timeScale /= Math.pow(timeScaleSpeed, dt);
		}
		
		for (Body body : bodies) {
			body.render((int) xcam, (int) ycam, scale);
		}
		
		int xmouse = Mouse.getX();
		int ymouse = Display.getHeight() - Mouse.getY();
		if (Mouse.isButtonDown(0)) {
			xcam += mousex - xmouse;
			ycam += mousey - ymouse;
		}
		double scalespd = 1.00001;
		
		int mouseWheel = Mouse.getDWheel();
		if (mouseWheel != 0) {
			double xtarget = Mouse.getX();
			double ytarget = Display.getHeight() - Mouse.getY();
			double scrollspd = 20000;
			double newScale = scale;
			if (mouseWheel < 0) {
				newScale = scale / Math.pow(scalespd, Math.abs(mouseWheel * dt * scrollspd));
			} else {
				newScale = scale * Math.pow(scalespd, Math.abs(mouseWheel * dt * scrollspd));
			}
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
		mousex = xmouse;
		mousey = ymouse;
	}
	
	private void handleCollisions(int a, int b) {
		double dx = bodies.get(a).x - bodies.get(b).x;
		double dy = bodies.get(a).y - bodies.get(b).y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		if (distance < bodies.get(a).radius + bodies.get(b).radius) {
			double angle = Math.atan2(dy, dx);
			double totalmass = bodies.get(a).mass + bodies.get(b).mass;
			double percent = bodies.get(b).mass / totalmass;
			bodies.get(a).xspd = (bodies.get(a).mass * bodies.get(a).xspd + bodies.get(b).mass * bodies.get(b).xspd) / (bodies.get(a).mass + bodies.get(b).mass);
			bodies.get(a).yspd = (bodies.get(a).mass * bodies.get(a).yspd + bodies.get(b).mass * bodies.get(b).yspd) / (bodies.get(a).mass + bodies.get(b).mass);
			bodies.get(a).x += dx * percent * Math.cos(angle);
			bodies.get(a).y += dy * percent * Math.sin(angle);
			bodies.get(a).mass += bodies.get(b).mass;
			double space = Math.pow(bodies.get(a).radius, 3) * Math.PI * 4.0 / 3.0 + Math.pow(bodies.get(b).radius, 3) * Math.PI * 4.0 / 3.0;
			bodies.get(a).radius = Math.pow(3 * space / (4 * Math.PI), 1.0 / 3.0);
			bodies.get(a).setCirclePoints();
			bodies.remove(b);
			n = bodies.size();
		}
	}
	
	private void handleGravity(int a, int b) {
		double dx = bodies.get(a).x - bodies.get(b).x;
		double dy = bodies.get(a).y - bodies.get(b).y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		double angle = Math.atan2(dy, dx);
		double g = 0.0000000000667384;
		double force = g * bodies.get(a).mass * bodies.get(b).mass / (distance * distance);
		
		bodies.get(a).addspd(-timeScale * Math.cos(angle) * force / bodies.get(a).mass, -timeScale * Math.sin(angle) * force / bodies.get(a).mass);
		bodies.get(b).addspd(timeScale * Math.cos(angle) * force / bodies.get(b).mass, timeScale * Math.sin(angle) * force / bodies.get(b).mass);
		
	}
	
	private double renderFPS = 144.0;
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
