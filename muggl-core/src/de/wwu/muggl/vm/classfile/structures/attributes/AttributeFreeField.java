package de.wwu.muggl.vm.classfile.structures.attributes;

import java.io.IOException;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Attribute;

public class AttributeFreeField extends Attribute {
	/**
	 * Basic constructor.
	 * @param classFile The ClassFile the attribute belongs to.
	 * @param attributeNameIndex The index in the constant_pool that hold the attribute's name as a UTF8.
	 * @throws ClassFileException If the index is invalid.
	 * @throws IOException Thrown on errors reading from the DataInputStream of the class.
	 */
	public AttributeFreeField(ClassFile classFile, int attributeNameIndex) throws ClassFileException, IOException {
		super(classFile, attributeNameIndex);
		if (!this.classFile.getConstantPool()[this.attributeNameIndex].getStringValue().equals("FreeField")) {
			throw new ClassFileException("Encountered a corrupt class file: attribute_name_index of an attribute_free_field must be \"FreeField\".");
		}
		if (Globals.getInst().logger.isTraceEnabled()) Globals.getInst().logger.trace("Parsing: Read the Attribute \"FreeField\"");
	}

	@Override
	public String getStructureName() {
		return "attribute_free_field";
	}

}
