package simulator;

import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Main implements Runnable {

	Body b, c;
	double scale = 1, timeScale = 1;

	protected void init() {
		b = new Body(300, 400, 0.05, 0, 1, 5);
		c = new Body(300, 300, -0.05, -0, 1, 5);
	}

	protected void update(double dt) {

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			timeScale *= 1.0001;
			System.out.println(timeScale);
		}

		b.update(timeScale);
		c.update(timeScale);

		double dx = b.x - c.x;
		double dy = b.y - c.y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		double angle = Math.atan2(dy, dx);
		double g = 0.0000000000666666667;
		double force = b.mass * c.mass / (distance * distance);
		b.xspd -= timeScale * Math.cos(angle) * force / b.mass;
		b.yspd -= timeScale * Math.sin(angle) * force / b.mass;
		c.xspd += timeScale * Math.cos(angle) * force / c.mass;
		c.yspd += timeScale * Math.sin(angle) * force / c.mass;

	}

	protected void render() {
		b.render(0, 0, scale);
		c.render(0, 0, scale);
	}

	public void run() {
		if (!(width > 0 && height > 0)) {
			width = 800;
			height = 640;
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
			update(0);
			updates++;
			this.updates++;

			double now = nanoTime();

			dt += (now - lastTime) * 120;
			lastTime = now;
			// System.out.println(dt);
			while (dt >= 1) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				render();
				System.out.println("render");
				System.out.println(dt);
				dt -= 1.0;
			}

			if (getTime() - timer > 1) {
				System.out.println(getMillis());
				timer += 1;
				Display.setTitle("updates: " + this.updates + " | ups: " + updates + " | delta: " + delta);
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
		newUpdateTime = getTime();
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
