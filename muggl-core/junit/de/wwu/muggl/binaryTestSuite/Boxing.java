package de.wwu.muggl.binaryTestSuite;

public class Boxing {
	
	
	// should generate a putstatic for boolean
	private static boolean test3 = true;

	private boolean testing = false;
	private Boolean testing2 = false;

	
	public Boolean boxbooleanField(Boolean in) {
		this.testing = in;
		return this.testing;
	}

	public Boolean returnInitiatedField() {
		return this.testing;
	}

	public boolean boxbooleanField2(boolean in) {
		this.testing2 = in;
		return this.testing2;
	}

	// this should produce an ireturn instruction showcasing both
	// boolean-Boolean boxing and handling as integer
	public boolean returnInitiatedField2() {
		return this.testing2;
	}
	
	public boolean returnInitiatedFieldRaw() {
		return this.testing;
	}
	// how to work with a value you got from ireturn
	public boolean returnInitiatedFieldWrapped() {
		return this.returnInitiatedField2() || this.returnInitiatedField2();
	}

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

	public static String boxPlaceholderChar(char in) {
		return (new Boxing()).new Placeholder<Character>(in).getTString();
	}

	public static String boxPlaceholderint() {
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
