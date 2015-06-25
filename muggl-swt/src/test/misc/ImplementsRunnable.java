package test.misc;

@SuppressWarnings("all")
public class ImplementsRunnable implements Runnable {

	public void run() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO
		}
	}
	
	public static void main(String... args) {
		ImplementsRunnable r = new ImplementsRunnable();
		Thread t = new Thread(r);
		t.start();
		int a = 6;
	}
	
}
