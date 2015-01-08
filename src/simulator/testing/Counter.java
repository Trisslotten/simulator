
package simulator.testing;

public class Counter {
	
	public volatile int n;
	
	public Counter(int value) {
		n = value;
	}
	
	public void incr() {
		n++;
	}
	
	public void reset() {
		n = 0;
	}
	
	public String toString() {
		return Integer.toString(n);
	}
	
}
