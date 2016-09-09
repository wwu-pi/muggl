package de.wwu.muggl.vm.classfile.structures.constants;

import java.io.DataOutputStream;
import java.io.IOException;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Constant;
import de.wwu.muggl.vm.initialization.ReferenceValue;

/**
 * Representation of a CONSTANT_MethodHandle_info of a class.
 *
 * @author Max Schulze
 */
public class ConstantMethodHandle extends Constant {
	private ReferenceKind referenceKind;
	private int referenceIndex;

	public static enum ReferenceKind {
		REF_getField(1), REF_getStatic(2), REF_putField(3), REF_putStatic(4), REF_invokeVirtual(5), REF_invokeStatic(
				6), REF_invokeSpecial(7), REF_newInvokeSpecial(8), REF_invokeInterface(9);

		private final int referenceKindIdx;

		ReferenceKind(int idx) {
			this.referenceKindIdx = idx;
		}

		public int getReferenceKindIdx() {
			return referenceKindIdx;
		}

		public static ReferenceKind valueOf(int value) {
			for (ReferenceKind e : ReferenceKind.values()) {
				if (e.referenceKindIdx == value) {
					return e;
				}
			}
			return null;// not found
		}

		public String toString() {
			return super.toString().replaceAll("REF_", "").toLowerCase();
		}
	}

	/**
	 * Basic constructor.
	 * 
	 * @param classFile
	 *            The ClassFile the constant belongs to.
	 * @throws IOException
	 *             Thrown on errors reading from the DataInputStream of the class.
	 * @throws ClassFileException
	 *             If the referenceIndex is invalid.
	 */
	public ConstantMethodHandle(ClassFile classFile) throws IOException, ClassFileException {
		super(classFile);

		this.referenceKind = ReferenceKind.valueOf(classFile.getDis().readByte());
		this.referenceIndex = classFile.getDis().readUnsignedShort();
		checkIndexIntoTheConstantPool(this.referenceIndex);
		if (Globals.getInst().parserLogger.isTraceEnabled())
			Globals.getInst().parserLogger.trace("Parsing: Read new Constant: Method Handle, reference_kind is "
					+ getReferenceKind() + ", reference_index is " + getReferenceIndex());
	}

	/**
	 * Write the represented structure to the output stream provided.
	 * 
	 * @param dos
	 *            A DataOutputStream to write the represented structure to.
	 * @throws IOException
	 *             If writing to the output stream failed.
	 */
	@Override
	public void writeToClassFile(DataOutputStream dos) throws IOException {
		super.writeToClassFile(dos);
		dos.write(this.referenceKind.getReferenceKindIdx());
		dos.writeShort(this.referenceIndex);
	}

	/**
	 * Get a String representation of this class file's structure name.
	 * 
	 * @return A String representation of this class file's structure name.
	 */
	@Override
	public String getStructureName() {
		return "CONSTANT_MethodHandle_info";
	}

	public ReferenceKind getReferenceKind() {
		return referenceKind;
	}

	public int getReferenceIndex() {
		return referenceIndex;
	}

	/**
	 * Get the value of the constant.
	 * 
	 * @return The constants value as a String.
	 */
	@Override
	public String getValue() {
		return this.referenceKind.toString() + " "
				+ ((Constant) this.classFile.getConstantPool()[this.referenceIndex]).getValue();

	}

	/**
	 * Get the string value of the constant.
	 * 
	 * @return The constants value in its String representation.
	 */
	@Override
	public String getStringValue() {
		return getValue();
	}

	/**
	 * Get this Constant's tag.
	 * 
	 * @return The Constant's tag as a byte.
	 */
	@Override
	public byte getTag() {
		return ClassFile.CONSTANT_METHODHANDLE;
	}
}
