package simulator;

import java.util.ArrayList;

class ComparerThread extends Thread {

	public ArrayList<Body> bodies;
	public int index;
	public double timeScale;

	public Object waiter;

	public ComparerThread(ArrayList<Body> asd, int i, double ts, Object waiter) {
		bodies = asd;
		index = i;
		timeScale = ts;
		this.waiter = waiter;
	}

	public void move(String asd) {
		String ble = "";
		for (int k = 0; k / 10 < index; k++)
			ble += " ";
		System.out.println(ble + asd);
	}

	public void run() {
		while (true) {
			// ////////////////////////////////////////////
			for (int i = 1; i <= bodies.size() - 1; i++) {
				int j = (i + index) % bodies.size();
				handleGravity(index, j);
			}
			
			System.out.println(index + " has compared");
			// ////////////////////////////////////////////

			synchronized (waiter) {
				try {
					System.out.println(index + " is waiting");
					bodies.get(index).hasCompared = true;
					waiter.wait();	
					System.out.println(index + "!stopped!waiting!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
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

		bodies.get(a).addspd(-timeScale * Math.cos(angle) * force / bodies.get(a).mass, -timeScale * Math.sin(angle) * force / bodies.get(a).mass);
		bodies.get(b).addspd(timeScale * Math.cos(angle) * force / bodies.get(b).mass, timeScale * Math.sin(angle) * force / bodies.get(b).mass);

	}
}
