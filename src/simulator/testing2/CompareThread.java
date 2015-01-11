
package simulator.testing2;

import java.util.ArrayList;

public class CompareThread extends Thread {
	
	public ArrayList<Body> bodies;
	public int startindex, endindex;
	
	public Object waiter;
	
	public double timeScale;
	
	public boolean running, isWaiting;
	
	public CompareThread(ArrayList<Body> bodies, int startindex, int endindex, boolean running, double timeScale, Object waiter) {
		this.bodies = bodies;
		this.startindex = startindex;
		this.endindex = endindex;
		this.running = running;
		this.timeScale = timeScale;
		this.waiter = waiter;
	}
	
	@Override
	public void run() {
		while (running) {
			
			for (int i = 1; i < bodies.size() - (endindex - startindex + 1); i++) {
				int j = (i + endindex) % bodies.size();
				for (int index = startindex; index <= endindex; index++) {
					System.out.println("handling Gravity " + j + " " + index);
					handleGravity(index, j);
				}
			}
			isWaiting = true;
			synchronized (waiter) {
				try {
					waiter.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private void handleGravity(int a, int b) {
		double dx = bodies.get(a).x - bodies.get(b).x;
		double dy = bodies.get(a).y - bodies.get(b).y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		double angle = Math.atan2(dy, dx);
		double g = 0.0000000000667384;
		double force = g * bodies.get(a).mass * bodies.get(b).mass / (distance * distance);
		
		bodies.get(a).addforce(-timeScale * force, angle);
		bodies.get(b).addforce(timeScale * force, angle);
		
	}
	
}
