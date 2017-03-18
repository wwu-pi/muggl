package de.wwu.muggl.test;

import de.wwu.muggl.Supported;
import de.wwu.muggl.vm.VmSymbols;
import de.wwu.muggl.vm.VmSymbols.BasicType;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

/**
 * Test for checking that the various basicType conversion work well.
 * 
 * @author Max Schulze
 *
 */
public class TestVmSymbols extends TestSkeleton {

	// Signature <--> basic Type
	@Test
	@Category(Supported.class)
	public final void test_SignatureConv() {
		assertEquals("B", VmSymbols.SIGNATURE_BYTE);

		assertEquals(BasicType.T_BYTE, VmSymbols.signature2BasicType(VmSymbols.SIGNATURE_BYTE));
		assertEquals(BasicType.T_VOID, VmSymbols.signature2BasicType(VmSymbols.SIGNATURE_VOID));
		assertEquals(BasicType.T_LONG, VmSymbols.signature2BasicType(VmSymbols.SIGNATURE_LONG));
		assertEquals(BasicType.T_INT, VmSymbols.signature2BasicType(VmSymbols.SIGNATURE_INT));
		assertEquals(BasicType.T_OBJECT, VmSymbols.signature2BasicType("K"));

		assertEquals(VmSymbols.basicType2Signature(BasicType.T_BOOLEAN), VmSymbols.SIGNATURE_BOOL);
		assertEquals(VmSymbols.basicType2Signature(BasicType.T_SHORT), VmSymbols.SIGNATURE_SHORT);
		assertEquals(VmSymbols.basicType2Signature(BasicType.T_CHAR), VmSymbols.SIGNATURE_CHAR);
		assertEquals(VmSymbols.basicType2Signature(BasicType.T_DOUBLE), VmSymbols.SIGNATURE_DOUBLE);

	}

	// basic Type --> Class
	@Test
	public final void test_BasicTypeClass() {
		assertEquals(int.class, VmSymbols.basicType2Class(BasicType.T_INT));
		assertEquals(void.class, VmSymbols.basicType2Class(BasicType.T_VOID));
		assertEquals(float.class, VmSymbols.basicType2Class(BasicType.T_FLOAT));

	}

	// basic Type <--> JavaClassName
	@Test
	public final void test_BasicTypeClassName() {
		assertEquals(VmSymbols.java_lang_Boolean, VmSymbols.basicType2JavaClassName(BasicType.T_BOOLEAN));
		assertEquals(VmSymbols.java_lang_Void, VmSymbols.basicType2JavaClassName(BasicType.T_VOID));
		assertEquals(BasicType.T_FLOAT, VmSymbols.javaClassName2BasicType(VmSymbols.java_lang_Float));
		assertEquals(BasicType.T_INT, VmSymbols.javaClassName2BasicType(VmSymbols.java_lang_Integer));
		assertEquals(BasicType.T_ILLEGAL, VmSymbols.javaClassName2BasicType("java/lang/invoke/MethodHandle"));
		assertEquals(BasicType.T_VOID, VmSymbols.javaClassName2BasicType(VmSymbols.java_lang_Void));
	}

	// basic Type <--> Indexed Array
	@Test
	public final void test_BasicTypeIdxArray() {
		assertEquals(BasicType.T_VOID, VmSymbols.BasicTypeArr[BasicType.T_VOID.value]);
		assertEquals(BasicType.T_BOOLEAN, VmSymbols.BasicTypeArr[BasicType.T_BOOLEAN.value]);
		assertEquals(BasicType.T_FLOAT, VmSymbols.BasicTypeArr[BasicType.T_FLOAT.value]);
	}

	// primtive Name --> BasicType
	@Test
	public final void test_PrimNameBasicType() {
		assertEquals(BasicType.T_VOID, VmSymbols.primitiveName2BasicType("void"));
		assertEquals(BasicType.T_INT, VmSymbols.primitiveName2BasicType("int"));
		assertEquals(BasicType.T_LONG, VmSymbols.primitiveName2BasicType("long"));
		assertEquals(BasicType.T_BOOLEAN, VmSymbols.primitiveName2BasicType("boolean"));
		assertEquals(BasicType.T_CHAR, VmSymbols.primitiveName2BasicType("char"));
	}

}
