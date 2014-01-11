package test.misc;

/**
 *
 */
public class WrapperTest {

	public static void bla() {
		System.out.println(4);
	}
	
	public static void bla2() {
		Integer[] src = {4, 3, 5};
		int srcPos = 0;
		Integer[] dest = new Integer[3];
		int destPos = 1;
		int length = 2;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
		
		for (Integer value : src) {
			System.out.print(value + " ");
		}
		System.out.println();
		for (Integer value : dest) {
			System.out.print(value + " ");
		}
	}
	
	public static void bla3() {
		Integer[] src = {1, 2, 3, 4, 5, 6};
		int srcPos = 0;
		int destPos = 3;
		int length = 3;
		
		for (Integer value : src) {
			System.out.print(value + " ");
		}
		System.out.println();
		
		System.arraycopy(src, srcPos, src, destPos, length);
		
		for (Integer value : src) {
			System.out.print(value + " ");
		}
	}
	
	public static void exception1() {
		Integer[] src = null;
		int srcPos = 1;
		Integer[] dest = new Integer[3];
		int destPos = 1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception2() {
		Integer[] src = {4, 3, 5};
		int srcPos = 1;
		Integer[] dest = null;
		int destPos = 1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception3() {
		Integer src = 4;
		int srcPos = 1;
		Integer[] dest = new Integer[3];
		int destPos = 1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception4() {
		Integer[] src = {4, 3, 5};
		int srcPos = 1;
		Integer dest = new Integer(6);
		int destPos = 1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception5() {
		int[] src = {4, 3, 5};
		int srcPos = 1;
		long[] dest = {4L, 3L, 5L};
		int destPos = 1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception6() {
		int[] src = {4, 3, 5};
		int srcPos = 1;
		Integer[] dest = new Integer[3];
		int destPos = 1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception7() {
		Integer[] src = {4, 3, 5};
		int srcPos = 1;
		int[] dest = {4, 6, 5};
		int destPos = 1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception8() {
		Integer[] src = {4, 3, 5};
		int srcPos = -1;
		Integer[] dest = new Integer[3];
		int destPos = 1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception9() {
		Integer[] src = {4, 3, 5};
		int srcPos = 1;
		Integer[] dest = new Integer[3];
		int destPos = -1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception10() {
		Integer[] src = {4, 3, 5};
		int srcPos = 1;
		Integer[] dest = new Integer[3];
		int destPos = 1;
		int length = -1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception11() {
		Integer[] src = {4, 3, 5};
		int srcPos = 1;
		Integer[] dest = new Integer[3];
		int destPos = 1;
		int length = 100;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception12() {
		Integer[] src = {4, 3, 5};
		int srcPos = 1;
		Integer[] dest = new Integer[3];
		int destPos = 1;
		int length = 100;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}
	
	public static void exception13() {
		Object[] src = {4, 3, 5};
		int srcPos = 1;
		Object[] dest = new Long[3];
		int destPos = 1;
		int length = 1;
		
		System.arraycopy(src, srcPos, dest, destPos, length);
	}

	public static void main(String... args) {
		try {
		bla2();
		System.out.println("\n");
		bla3();
		exception13();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
}
