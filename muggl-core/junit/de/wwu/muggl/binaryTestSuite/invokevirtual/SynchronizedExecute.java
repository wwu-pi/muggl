package de.wwu.muggl.binaryTestSuite.invokevirtual;

/**
 * Test class to highligh bytecode behaviour around monitors.
 * @author Max Schulze
 *
 */
public class SynchronizedExecute {

	public static void main(String[] args) {
		executeStaticFlag();
		executeStaticInstruction();

		executeInstanceFlagWrapper();
		executeInstanceInstructionWrapper();
		executeInstanceInstructionTwiceWrapper();
	}

	/*
	 * These will produce the flag ACC_SYNCHRONIZED
	 */
	public synchronized static void executeStaticFlag() {
		System.out.println("all fine, executeStaticFlag");
	}

	public synchronized void executeInstanceFlag() {
		System.out.println("all fine, executeInstanceFlag");
	}

	/*
	 * These will produce monitorenter and monitorexit instructions
	 */
	public static void executeStaticInstruction() {
		synchronized (SynchronizedExecute.class) {
			System.out.println("all fine, too. executeStaticInstruction");
		}
	}

	public void executeInstanceInstruction() {
		synchronized (this) {
			System.out.println("all fine, too. executeInstanceInstruction");
		}
	}

	public void executeInstanceInstructionTwice() {
		synchronized (this) {
			synchronized (this) {
				System.out.println("all fine, too. executeInstanceInstructionTwice");
			}
		}
	}

	/*
	 * Those are wrappers for easier (static) calling.
	 */
	public static void executeInstanceInstructionWrapper() {
		new SynchronizedExecute().executeInstanceInstruction();
	}

	public static void executeInstanceFlagWrapper() {
		new SynchronizedExecute().executeInstanceFlag();
	}

	public static void executeSynchronizedWrapper() {
		executeStaticFlag();
	}

	public static void executeInstanceInstructionTwiceWrapper() {
		new SynchronizedExecute().executeInstanceInstructionTwice();
	}

}
