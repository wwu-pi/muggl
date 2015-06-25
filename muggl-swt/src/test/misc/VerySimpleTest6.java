package test.misc;

@SuppressWarnings("all")
public class VerySimpleTest6 {

	int multiArrayTest(int b) {
		int[][][] c = new int[2][3][4];
		c[0][1][2] = b;
		return c[0][1][2];
	}
	
	void iinc(int a, long b) {
		a++;
		b++;
	}
	
	public static void main(String[] args) {
		System.out.print(Integer.MIN_VALUE);
	}
    
    void H_dohanoi(int n, int t, int f, int u) { if (n > 0) { H_dohanoi(n-1, u, f, t); H_dohanoi(n-1, t, u, f); } }

	@SuppressWarnings("unused")
	public void severalEditableArguments(String a, String[] b, int c, Integer d, boolean e, Boolean f, char g, Character h, short i, Short j) {}

	@SuppressWarnings("unused")
	public void severalEditableArguments(String a, String[] b, int[] c, Integer[] d, boolean[] e, Boolean[] f, char[] g, Character[] h, short[] i, Short[] j) {}

	@SuppressWarnings("unused")
	public void severalEditableArguments(String[][] b, int[][][] c) {};
	
	@SuppressWarnings("unused")
	public void severalEditableArguments(int[][] b, int[][][] c, int[][][][] d) {};
		
}
