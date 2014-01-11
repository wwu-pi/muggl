package test.misc;


@SuppressWarnings("all")
public class SymbolicTests2 {

	public SymbolicTests2() {
		super();
	}
	
	public int ifTest1(int a, int b) {
		if (b < 5) a++;
		a = ifTest2(a);
		if (a + b < 8) a++;
		return a;
	}
	
	public int ifTest2(int a) {
		if (a > 6) return 7;
		return 6;
	}

	public void instantiateClass() {
		SymbolicTests t = new SymbolicTests();
		t.callMySelf();
	}

}
