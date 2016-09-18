package de.wwu.muggl.vm.execution;

import de.wwu.muggl.vm.VirtualMachine;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.initialization.Objectref;
import de.wwu.muggl.vm.initialization.PrimitiveWrappingImpossibleException;
import de.wwu.muggl.vm.initialization.ReferenceValue;

public class BoxingConversion {
	/**
	 * Boxing conversion converts expressions of primitive type to corresponding expressions of reference type.
	 * Specifically, the following nine conversions are called the boxing conversions:
	 * 
	 * @see JLS §5.1.7 Boxing Conversion
	 * @return
	 */
	public static ReferenceValue Boxing(VirtualMachine vm, Object object) {
		Objectref referenceValue = null;
		try {
			referenceValue = vm.getClassLoader().getClassAsClassFile(object.getClass().getName())
					.getAPrimitiveWrapperObjectref(vm);
		} catch (PrimitiveWrappingImpossibleException | ClassFileException e) {
			e.printStackTrace();
		}

		System.out.println(object.getClass().getName());
		try {
			switch (object.getClass().getName()) {
			case "java.lang.Boolean":
				referenceValue.putField(
						vm.getClassLoader().getClassAsClassFile("java.lang.Boolean").getFieldByName("value"),
						(boolean) object);
				break;

			case "java.lang.Integer":
				// FIXME mxs: boxing should really use the constructors!
				referenceValue.putField(
						vm.getClassLoader().getClassAsClassFile("java.lang.Integer").getFieldByName("value"),
						(int) object);

				break;
			default:
				break;
			}
		} catch (ClassFileException e) {
			e.printStackTrace();
		}

		return referenceValue;
	}
}
