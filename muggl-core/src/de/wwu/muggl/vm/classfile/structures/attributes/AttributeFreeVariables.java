package de.wwu.muggl.vm.classfile.structures.attributes;

import java.io.DataOutputStream;
import java.io.IOException;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Attribute;
import de.wwu.muggl.vm.classfile.structures.attributes.elements.FreeVariable;

public class AttributeFreeVariables extends Attribute {
	
	private int freeVariablesLength;
	private FreeVariable[] freeVariables;

	public AttributeFreeVariables(ClassFile classFile, int attributeNameIndex)  throws ClassFileException, IOException {
		super(classFile, attributeNameIndex);
		if (!this.classFile.getConstantPool()[this.attributeNameIndex].getStringValue().equals("FreeVariables")) {
			throw new ClassFileException("Encountered a corrupt class file: attribute_name_index of an attribute_free_variables must be \"FreeVariables\".");
		}
		if (Globals.getInst().logger.isTraceEnabled()) Globals.getInst().logger.trace("Parsing: Read the Attribute \"FreeVariables\"");
		
		this.freeVariablesLength = classFile.getDis().readUnsignedShort();
		if (this.freeVariablesLength < 0) throw new ClassFileException("Encountered a corrupt class file: free_variables_length of an attribute_free_variables must not be negative.");
		this.freeVariables = new FreeVariable[this.freeVariablesLength];
		for (int a = 0; a < this.freeVariablesLength; a++) {
			this.freeVariables[a] = new FreeVariable(classFile);
		}
	}

	@Override
	public String getStructureName() {
		return "attribute_free_variables";
	}

	/**
	 * Write the represented structure to the output stream provided.
	 * @param dos A DataOutputStream to write the represented structure to.
	 * @throws IOException If writing to the output stream failed.
	 */
	@Override
	public void writeToClassFile(DataOutputStream dos) throws IOException {
		super.writeToClassFile(dos);
		dos.writeShort(this.freeVariablesLength);
		for (int a = 0; a < this.freeVariablesLength; a++) {
			this.freeVariables[a].writeToClassFile(dos);
		}
	}

	public int getFreeVariablesLength() {
		return this.freeVariablesLength;
	}

	public FreeVariable[] getFreeVariables() {
		return this.freeVariables;
	}

}
