package de.wwu.muggl.vm.classfile.structures.attributes;

import java.io.IOException;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Attribute;
import de.wwu.muggl.vm.classfile.structures.attributes.elements.BootstrapMethod;

/**
 * Representation of a BootstrapMethods_attribute of a class.
 *
 * @author Max schulze
 */
public class AttributeBootstrapMethods extends Attribute {
	private int numBootstrapMethods;
	private BootstrapMethod[] bootstrapMethods;

	/**
	 * Basic constructor.
	 * 
	 * @param classFile
	 *            The ClassFile the attribute belongs to.
	 * @param attributeNameIndex
	 *            The index in the constant_pool that hold the attribute's name
	 *            as a UTF8.
	 * @throws ClassFileException
	 *             If the index is invalid.
	 * @throws IOException
	 *             Thrown on errors reading from the DataInputStream of the
	 *             class.
	 */
	public AttributeBootstrapMethods(ClassFile classFile,
			int attributeNameIndex) throws ClassFileException, IOException {
		super(classFile, attributeNameIndex);

		if (!this.classFile.getConstantPool()[this.attributeNameIndex]
				.getStringValue().equals("BootstrapMethods")) {
			throw new ClassFileException(
					"Encountered a corrupt class file: attribute_name_index of an attribute_bootstrapsMethods must be \"BootstrapMethods\".");
		}
		this.numBootstrapMethods = classFile.getDis().readUnsignedShort();

		this.bootstrapMethods = new BootstrapMethod[numBootstrapMethods];

		for (int a = 0; a < this.numBootstrapMethods; a++) {
			this.bootstrapMethods[a] = new BootstrapMethod(classFile);
		}
		if (Globals.getInst().parserLogger.isTraceEnabled())
			Globals.getInst().parserLogger
					.trace("Parsing: Read the Attribute \"BootstrapMethods\" with "
							+ numBootstrapMethods + " bootstrap methods");
	}

	/**
	 * Get a String representation of this class file's structure name.
	 * 
	 * @return A String representation of this class file's structure name.
	 */
	@Override
	public String getStructureName() {
		return "attribute_bootstrap_methods";
	}

	public int getNumBootstrapMethods() {
		return numBootstrapMethods;
	}

	public BootstrapMethod[] getBootstrapMethods() {
		return bootstrapMethods;
	}

}
