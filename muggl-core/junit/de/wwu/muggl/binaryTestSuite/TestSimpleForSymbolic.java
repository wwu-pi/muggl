package de.wwu.muggl.binaryTestSuite;

public class TestSimpleForSymbolic {

	public class Person {
		private int age;

		public Person(int personAge) {
			this.age = personAge;
		}

		public int getAge() {
			return age;
		}
	}

	public static final String METHOD_olderThanSammy = "olderThanSammy";

	public int olderThanSammy(int myAge) {
		int sammysAge = 25;

		if (myAge < sammysAge) {
			return -1;
		} else if (myAge == sammysAge) {
			return 0;
		} else {
			return 1;
		}
	}

	public int olderThanSammyObj(int myAge) {
		Person sammy = new Person(25);
		Person me = new Person(myAge);

		if (me.getAge() < sammy.getAge()) {
			return -1;
		} else if (me.getAge() == sammy.getAge()) {
			return 0;
		} else {
			return 1;
		}
	}

}
