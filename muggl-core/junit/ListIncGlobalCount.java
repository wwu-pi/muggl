import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * This example is to demonstrate that you can replace the LambdaMetafactory with your own "factory". 1.) Compile this
 * example.
 * 
 * 2.) with javap -v -p <file>.class, inspect the file. Find the constant pool entry "Methodref" for
 * "myPrimitiveBootstrap" #99 = Methodref #1.#100 // PrintNumberListStaticArg.myPrimitiveBootstrap
 * 
 * Find the constant pool entry "Methodref" for "metafactory" #132 = Methodref #133.#135 //
 * java/lang/invoke/LambdaMetafactory.metafactory
 * 
 * Find the constant pool entry for the methodhandle #137 = MethodHandle #6:#99 // invokestatic
 * 
 * 3.) open the <file>.class in a hex editor. Locate the portion 0F 06 00 84 (read: 0F = 15 =const MethodHandle,
 * 06=invokestatic, 00 84 =#132 (2byte)) replace 00 84 with 00 63 (=#99 (2byte)). Save the file.
 * 
 * Now the bootstrap_method attribute should point to myPrimitiveBootstrap
 * 
 * @author max
 *
 */
public class ListIncGlobalCount {
	public static int counter;

	public class mConsumer implements Consumer<Integer> {
		@Override
		public void accept(Integer t) {
			if (t >= 10)
				counter++;
		}
	};

	public static Consumer<Integer> accept() {
		return (new ListIncGlobalCount()).new mConsumer();
	}

	// Execute the "accept" method directly
	public static int executeWithoutLambda() {
		counter = 0;
		List<Integer> numbers = Arrays.asList(1, 10, 11);
		numbers.forEach(accept());
		return counter;
	}

	public static int executeLambdaCompiledJVM() {
		counter = 0;
		List<Integer> numbers = Arrays.asList(1, 10, 11);
		numbers.forEach(x -> accept().accept(x));
		return counter;
	}

	public static int executeLambdaPure() {
		counter = 0;
		List<Integer> numbers = Arrays.asList(1, 10, 11);
		numbers.forEach(x -> {
			if (x >= 10)
				counter++;
		});
		return counter;
	}

	public static CallSite myPrimitiveBootstrap(MethodHandles.Lookup l, String name, MethodType mt, MethodType mt2,
			MethodHandle mh, MethodType mt3) {
		try {
			return new ConstantCallSite(l.findStatic(ListIncGlobalCount.class, name, mt));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void dummy() {
		// so that the compiler generates a methodRef
		myPrimitiveBootstrap(null, null, null, null, null, null);
	}

	// test the creation of objects needed for Bootstrapping
	public static void bootstrapTester() {
		myPrimitiveBootstrap(MethodHandles.lookup(), "accept", MethodType.methodType(Consumer.class), null, null, null);
	}

	public static void main(String[] args) {
		System.out.println(executeLambdaCompiledJVM());
		System.out.println(executeWithoutLambda());
		System.out.println(executeLambdaPure());
	}
}
