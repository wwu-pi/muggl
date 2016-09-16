package de.wwu.muggl.binaryTestSuite.invokevirtual;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MethodHandleTest {

	public static void main(String[] args) throws Throwable  {
		execute();
	}

	public static void execute() throws Throwable {
//		Object x, y;
//		String s;
//		int i;
//		MethodType mt;
//		MethodHandle mh;
//		MethodHandles.Lookup lookup = MethodHandles.lookup();
//		// mt is {(char,char) => String}
//		mt = MethodType.methodType(String.class, char.class, char.class);
//		mh = lookup.findVirtual(String.class, "replace", mt);
//		// (Ljava/lang/String;CC)Ljava/lang/String;
//		s = (String) mh.invokeExact("daddy", 'd', 'n');
//		assert (s.equals("nanny"));
//		// weakly typed invocation (using MHs.invoke)
//		s = (String) mh.invokeWithArguments("sappy", 'p', 'v');
//		assert (s.equals("savvy"));
//		// mt is {Object[] => List}
//		mt = MethodType.methodType(java.util.List.class, Object[].class);
//		mh = lookup.findStatic(java.util.Arrays.class, "asList", mt);
//		// mt is {(Object,Object,Object) => Object}
////		mt = MethodType.genericMethodType(3);
////		mh = MethodHandles.collectArguments(mh, mt);
////		// mt is {(Object,Object,Object) => Object}
////		// (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
////		x = mh.invokeExact((Object) 1, (Object) 2, (Object) 3);
////		assert (x.equals(java.util.Arrays.asList(1, 2, 3)));
//		// mt is { => int}
//		mt = MethodType.methodType(int.class);
//		mh = lookup.findVirtual(java.util.List.class, "size", mt);
//		// (Ljava/util/List;)I
//		i = (int) mh.invokeExact(java.util.Arrays.asList(1, 2, 3));
//		assert (i == 3);
////		mt = MethodType.methodType(void.class, String.class);
////		mh = lookup.findVirtual(java.io.PrintStream.class, "println", mt);
////		mh.invokeExact(System.out, "Hello, world.");
////		// (Ljava/io/PrintStream;Ljava/lang/String;)V
		
	}

}
