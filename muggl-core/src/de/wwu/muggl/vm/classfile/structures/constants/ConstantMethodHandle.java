package de.wwu.muggl.vm.classfile.structures.constants;

import java.io.DataOutputStream;
import java.io.IOException;
import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Constant;

/**
 * Representation of a CONSTANT_MethodHandle_info of a class.
 *
 * @author Max Schulze
 */
public class ConstantMethodHandle extends Constant {
	private byte referenceKind;
	private int referenceIndex;

	/**
	 * Basic constructor.
	 * 
	 * @param classFile
	 *            The ClassFile the constant belongs to.
	 * @throws IOException
	 *             Thrown on errors reading from the DataInputStream of the
	 *             class.
	 * @throws ClassFileException
	 *             If the referenceIndex is invalid.
	 */
	public ConstantMethodHandle(ClassFile classFile)
			throws IOException, ClassFileException {
		super(classFile);
		this.referenceKind = classFile.getDis().readByte();
		this.referenceIndex = classFile.getDis().readUnsignedShort();
		checkIndexIntoTheConstantPool(this.referenceIndex);
		if (Globals.getInst().logger.isTraceEnabled())
			Globals.getInst().logger
					.trace("Parsing: Read new Constant: Method Handle, reference_kind is "
							+ getReferenceKind() + ", reference_index is "
							+ getReferenceIndex());
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
		dos.write(this.referenceKind);
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

	public byte getReferenceKind() {
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
		// TODO: possibly disambiguate by reference_kind type.
		// see java8 spec p.88
		return "#" + this.referenceKind + " " + ((Constant) this.classFile
				.getConstantPool()[this.referenceIndex]).getValue();
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
