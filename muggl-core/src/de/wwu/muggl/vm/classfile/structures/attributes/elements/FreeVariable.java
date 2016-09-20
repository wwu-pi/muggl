package de.wwu.muggl.vm.classfile.structures.attributes.elements;

import java.io.DataOutputStream;
import java.io.IOException;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.ClassFileStructure;

public class FreeVariable extends ClassFileStructure {
	private int nameIndex;
	private int index;

	/**
	 * Basic constructor.
	 * @param classFile The ClassFile the local_variable_table belongs to.
	 * @throws IOException Thrown on errors reading from the DataInputStream of the class.
	 * @throws ClassFileException If any incorrect values are detected.
	 */
	public FreeVariable(ClassFile classFile) throws IOException, ClassFileException {
		super(classFile);
		this.nameIndex = classFile.getDis().readUnsignedShort();
		checkIndexIntoTheConstantPool(this.nameIndex);
		if (classFile.getConstantPool()[this.nameIndex].getTag() != ClassFile.CONSTANT_UTF8) {
			throw new ClassFileException("Encountered a corrupt class file: name_index of a free_variable should point to a CONSTANT_Utf8_info.");
		}
		this.index = classFile.getDis().readUnsignedShort();
		if (Globals.getInst().logger.isTraceEnabled()) Globals.getInst().logger.trace("Parsing: Read a free variable entry with name " + getName() + ".");
	}	
	
	@Override
	public void writeToClassFile(DataOutputStream dos) throws IOException {
		dos.writeShort(this.nameIndex);
		dos.writeShort(this.index);
	}

	@Override
	public String getStructureName() {
		return "free_variable";
	}

	/**
	 * Getter for the name as fetched from the constant_pool entry with index name_index.
	 * @return The name of this variable.
	 */
	public String getName() {
		return this.classFile.getConstantPool()[this.nameIndex].getStringValue();
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public int getIndex() {
		return index;
	}
	

}
