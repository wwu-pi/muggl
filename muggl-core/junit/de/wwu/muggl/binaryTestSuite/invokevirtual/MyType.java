package de.wwu.muggl.binaryTestSuite.invokevirtual;

/**
 * Test classes for method resolution in superclasses and superinterfaces
 * 
 * @author Max
 *
 * @param <T>
 */
public class MyType<T> {

	private final T t;

	public MyType(T t) {
		super();
		this.t = t;
	}

	public static <T> MySecondType<T> asSecondType(T m) {
		return new MySecondType<T>(m);
	}

	public static class MySecondType<E> extends SuperclassMySecondType<E>
			implements java.io.Serializable, MySecondInterface {
		private static final long serialVersionUID = -2764017481108945198L;
		protected final E e;

		MySecondType(E type) {
			if (type == null)
				throw new NullPointerException();
			e = type;
		}

		@Override
		public Object getObj() {
			return this.e;
		}

		@Override
		public E getObjWithType() {
			return this.e;
		}

	}

	public static String forTesting(Integer in) {
		MyType<Integer> test = new MyType<Integer>(in);
		return MyType.asSecondType(test.t).defaultInMyInterface();
	}

	public static Object putReturnObj(Object in) {
		return in;
	}

	public static Integer putReturnObj2(Integer in) {
		MyType<Integer> test = new MyType<Integer>(in);
		return test.t;
	}

	public static Integer putReturnObj3(Integer in) {
		MyType<Integer> test = new MyType<Integer>(in);
		System.out.println((Integer) MyType.asSecondType(test.t).getObj());
		return (Integer) MyType.asSecondType(test.t).getObj();
	}

	public static Integer putReturnObj4(Integer in) {
		MyType<Integer> test = new MyType<Integer>(in);
		System.out.println(MyType.asSecondType(test.t).getObjWithType());
		return MyType.asSecondType(test.t).getObjWithType();
	}

	public static Integer IntegerBoxing(Integer in) {
		return in + 4;
	}
	
	public static Boolean BooleanBoxing(boolean in) {
		return Boolean.valueOf(in);
	}
	
	public static boolean booleanTest2(boolean in) {
		// XOR
		return in ^ true;
	}
	
	public static int intNoBoxing(int in) {
		return in + 4;
	}

	public static void testPrinting() {
		System.out.println("very well.");
	}

	public static void main(String[] args) {
		System.out.println(forTesting(3));

		MyType<Integer> test = new MyType<Integer>(3);
		System.out.println(MyType.asSecondType(test.t).defaultInMySecondInterface());
		System.out.println(MyType.asSecondType(test.t).defaultInMyThirdInterface());
		System.out.println(MyType.asSecondType(test.t).getObjWithType());

	}
}
