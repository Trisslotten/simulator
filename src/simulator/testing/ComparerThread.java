
package simulator.testing;

import java.util.ArrayList;

class ComparerThread extends Thread {
	
	public ArrayList<Test> list;
	public int index;
	
	public ComparerThread(ArrayList<Test> asd, int i) {
		list = asd;
		index = i;
	}
	
	public void move(String asd) {
		String ble = "";
		for (int k = 0; k / 10 < index; k++)
			ble += " ";
		System.out.println(ble + asd);
	}
	
	public synchronized void wait(Object waiter) {
		try {
			waiter.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		for (int i = 1; i <= list.size() - 1; i++) {
			int j = (i + index) % list.size();
			move("T" + index + " " + list.get(index).asd + "-" + list.get(j).asd);
		}
		/*
		 * counter.incr(); if (counter.n < list.size()) { while (counter.n < list.size()) { move("T" + index + "|wait|" + counter.n); wait(waiter); if (counter.n >= list.size()) { move("T" + index + "|notd|" + counter.n); break; } } } else { move("T" + index + "|rels|" + counter.n); counter.n = 0; synchronized (waiter) { waiter.notifyAll(); } }
		 */
		
		// wait for threads to finish
		
	}
}