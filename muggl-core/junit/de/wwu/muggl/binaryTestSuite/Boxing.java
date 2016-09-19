package de.wwu.muggl.binaryTestSuite;

public class Boxing {

	public static String boxboolean() {
		boolean in = true;
		return boxboolean2(in);
	}

	public static String boxboolean2(Boolean in) {
		return in.toString();
	}

	public static String boxBoolean(Boolean in) {
		return "" + boxBoolean2(in);
	}

	public static String boxint() {
		return boxint2(340);
	}

	public static String boxint2(int in) {
		Object test = in;
		return test.toString();
	}

	// shoud produce an ireturn instruction at the end
	public static boolean boxBoolean2(boolean in) {
		return in;
	}

	public static String boxbooleanObj() {
		boolean in = true;
		return boxObjString(in);
	}

	public static String boxObjString(Object in) {
		return in.toString();
	}

	public static String boxPlaceholder(Boolean in) {
		return (new Boxing()).new Placeholder<Boolean>(in).getTString();
	}
	
	public static String boxPlaceholderint() {
		int test = 599;
		
		return (new Boxing()).new Placeholder<Integer>(599).getTString();
	}


	public class Placeholder<T> {
		private final T t;

		public Placeholder(T t) {
			this.t = t;
		}

		public Object getT() {
			return this.t;
		}

		public String getTString() {
			return getT().toString();
		}
	}
}
