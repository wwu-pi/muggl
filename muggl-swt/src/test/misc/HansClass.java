package test.misc;

@SuppressWarnings("all")
public class HansClass implements HansInterface {

	public int hansInt(int a, int b, int c) {
		return a - b * c;
	}

	public static int initMysqlf(int a, int b, int c) {
		HansClass h = new HansClass();
		return h.hansInt(a, b, c);
	}

}
