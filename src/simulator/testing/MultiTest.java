
package simulator.testing;

import java.util.ArrayList;

public class MultiTest {
	
	public MultiTest() {
		ArrayList<Test> asd = new ArrayList<Test>();
		asd.add(new Test("asd"));
		asd.add(new Test("qwe"));
		asd.add(new Test("zxc"));
		/*
		 * asd.add(new Test("qfq")); asd.add(new Test("vas")); asd.add(new Test("bgr")); asd.add(new Test("asc")); asd.add(new Test("rhc")); asd.add(new Test("tnd"));
		 */
		ArrayList<Thread> compareList = new ArrayList<Thread>();
		
		ArrayList<Thread> updateList = new ArrayList<Thread>();
		
		for (int j = 0; j < 50; j++) {
			for (int i = 0; i < asd.size(); i++) {
				compareList.add(new ComparerThread(asd, i));
			}
			for (Thread thread : compareList) {
				thread.start();
			}
			for (Thread thread : compareList) {
				try {
					thread.join();
				} catch (InterruptedException e) {
				}
			}
			compareList.clear();
			
			for (int i = 0; i < asd.size(); i++) {
				updateList.add(new UpdateThread(asd, i));
			}
			for (Thread thread : updateList) {
				thread.start();
			}
			for (Thread thread : updateList) {
				try {
					thread.join();
				} catch (InterruptedException e) {
				}
			}
			updateList.clear();
			
		}
		
	}
	
	public static void main(String[] args) {
		new MultiTest();
	}
	
}