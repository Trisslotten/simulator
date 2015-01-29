
package simulator.testing;

import java.util.ArrayList;

public class UpdateThread extends Thread {
	
	public ArrayList<Test> list;
	public int index;
	
	public UpdateThread(ArrayList<Test> asd, int i) {
		list = asd;
		index = i;
	}
	
	public void run() {
		list.get(index).incr();
		move("T" + index + " " + list.get(index).a);
	}
	
	public void move(String asd) {
		String ble = "";
		for (int k = 0; k / 10 < index; k++)
			ble += " ";
		System.out.println(ble + asd);
	}
	
}
