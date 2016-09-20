package de.wwu.muggl.vm.classfile.structures;

import java.io.IOException;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeConstantValue;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeDeprecated;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeFreeField;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeRuntimeInvisibleAnnotations;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeRuntimeVisibleAnnotations;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeSynthetic;
import de.wwu.muggl.vm.classfile.structures.attributes.AttributeUnknownSkipped;
import de.wwu.muggl.vm.classfile.structures.constants.ConstantUtf8;

/**
 * Representation of a field in classes. This class is a concrete implementation of the
 * abstract FieldMethod.
 *
 * @author Tim Majchrzak
 * @version 1.0.0, 2011-05-03
 */
public class Field extends FieldMethod {
	// Fields for the parsed access flags.
	private boolean accVolatile;
	private boolean accTransient;
	private boolean accEnum;

	/**
	 * Basic constructor.
	 *
	 * @param classFile The ClassFile the Method belongs to.
	 * @throws ClassFileException On unexpected fatal errors when parsing the class file.
	 * @throws IOException On errors reading from the DataInputStream of the class.
	 */
	public Field(ClassFile classFile) throws ClassFileException, IOException {
		super(classFile);
	}

	/**
	 * Get a String representation of this class file's structure name.
	 * @return A String representation of this class file's structure name.
	 */
	@Override
	public String getStructureName() {
		return "Field_info";
	}

	/**
	 * Parse the attribute number a. Only such attributes will be read that are of relevance
	 * for fields. Other attributes will be skipped.
	 * @param a The number of the attribute.
	 * @throws ClassFileException Thrown on unexpected fatal errors when parsing the class file.
	 * @throws IOException Thrown on errors reading from the DataInputStream of the class.
	 */
	@Override
	protected void readAttribute(int a) throws ClassFileException, IOException {
    	int attributeNameIndex = this.classFile.getDis().readUnsignedShort();
    	checkIndexIntoTheConstantPool(attributeNameIndex);
    	ConstantUtf8 constant = null;
    	try {
    		constant = (ConstantUtf8) this.classFile.getConstantPool()[attributeNameIndex];
    	} catch (ClassCastException e) {
    		throw new ClassFileException("Expected a ConstantUtf8 at " + attributeNameIndex + " in the constant_pool when reading the attributes, but got " + this.classFile.getConstantPool()[attributeNameIndex].getClass().getName() + ".");
    	}

    	String attributeName = constant.getStringValue();
    	// Which Attribute is it?
    	if (attributeName.equals("ConstantValue")) {
    		this.attributes[a] = new AttributeConstantValue(this.classFile, attributeNameIndex);
    	} else if (attributeName.equals("Synthetic")) {
    		this.attributes[a] = new AttributeSynthetic(this.classFile, attributeNameIndex);
    	} else if (attributeName.equals("FreeField")) {
    		this.attributes[a] = new AttributeFreeField(this.classFile, attributeNameIndex);
    	} else if (attributeName.equals("Deprecated")) {
    		this.attributes[a] = new AttributeDeprecated(this.classFile, attributeNameIndex);
		} else if (attributeName.equals("RuntimeVisibleAnnotations")) {
			this.attributes[a] = new AttributeRuntimeVisibleAnnotations(this.classFile,
					attributeNameIndex);
		} else if (attributeName.equals("RuntimeInvisibleAnnotations")) {
			this.attributes[a] = new AttributeRuntimeInvisibleAnnotations(this.classFile,
					attributeNameIndex);
    	} else {
			if (Globals.getInst().parserLogger.isDebugEnabled())
				Globals.getInst().parserLogger.debug("Parsing: Encountered an unknown attribute \""
						+ attributeName + "\"");
			this.attributes[a] = new AttributeUnknownSkipped(this.classFile, attributeNameIndex);
    	}
	}

	/**
	 * Simply return "Field" as a String.
	 * @return "Field" as a String.
	 */
	@Override
	protected String getFieldMethod() {
		return "Field";
	}

	/**
	 * Parse the access flags field so that the single access flags can be easily checked.
	 *
	 * @throws ClassFileException If the access_flags have a value less than zero or if there is an
	 *         illegal combination of flags.
	 */
	@Override
	protected void parseAccessFlags() throws ClassFileException {
		int flags = this.accessFlags;
		if (flags < 0) throw new ClassFileException("Encountered a corrupt class file: access_flags of a " + getName() + " is less than zero.");

		int unknowns = (flags & ~(ClassFile.ACC_ENUM | ClassFile.ACC_SYNTHETIC | ClassFile.ACC_TRANSIENT
				| ClassFile.ACC_VOLATILE | ClassFile.ACC_FINAL | ClassFile.ACC_STATIC | ClassFile.ACC_PROTECTED
				| ClassFile.ACC_PRIVATE | ClassFile.ACC_PUBLIC));

		if (unknowns != 0)
			Globals.getInst().logger
					.debug("Encountered and ignored flag (or flags) that are unknown for a field. Unknown flags: 0x"
							+ Integer.toHexString(unknowns));

		// Parse the flags.
		this.accEnum = (flags & ClassFile.ACC_ENUM) != 0;
		this.accSynthetic = (flags & ClassFile.ACC_SYNTHETIC) != 0;
		this.accTransient = (flags & ClassFile.ACC_TRANSIENT) != 0;
		this.accVolatile = (flags & ClassFile.ACC_VOLATILE) != 0;
		this.accFinal = (flags & ClassFile.ACC_FINAL) != 0;
		this.accStatic = (flags & ClassFile.ACC_STATIC) != 0;
		this.accProtected = (flags & ClassFile.ACC_PROTECTED) != 0;
		this.accPrivate = (flags & ClassFile.ACC_PRIVATE) != 0;
		this.accPublic = (flags & ClassFile.ACC_PUBLIC) != 0;


		// Check the flags.
		if ((this.accPublic && (this.accPrivate | this.accProtected))
				|| (this.accPrivate && this.accProtected))
			throw new ClassFileException(
					"At most one of the flags ACC_PRIVATE, ACC_PROTECTED and ACC_PUBLIC is allowed for a field.");
		if (this.accFinal && this.accVolatile)
			throw new ClassFileException("A field must not have both the flags ACC_FINAL and ACC_VOLATILE set.");
		if (this.classFile.isAccInterface()) {
			if (!this.accPublic)
				throw new ClassFileException("A field of an interface must have its ACC_PUBLIC flag set.");
			if (!this.accStatic)
				throw new ClassFileException("A field of an interface must have its ACC_STATIC flag set.");
			if (!this.accFinal)
				throw new ClassFileException("A field of an interface must have its ACC_FINAL flag set.");
			if (this.accEnum)
				throw new ClassFileException("A field of an interface must not have the ACC_ENUM flag set.");
			if (this.accTransient)
				throw new ClassFileException("A field of an interface must not have the ACC_TRANSIENT flag set.");
			if (this.accVolatile)
				throw new ClassFileException("A field of an interface must not have the ACC_VOLATILE flag set.");
			if (this.accProtected)
				throw new ClassFileException("A field of an interface must not have the ACC_PROTECTED flag set.");
			if (this.accPrivate)
				throw new ClassFileException("A field of an interface must not have the ACC_PRIVATE flag set.");
		}
	}

	/**
	 * Getter for the access flag "transient".
	 * @return true, if the field is transient, false otherwise.
	 */
	public boolean isAccTransient() {
		return this.accTransient;
	}

	/**
	 * Getter for the access flag "volatile".
	 * @return true, if the field is volatile, false otherwise.
	 */
	public boolean isAccVolatile() {
		return this.accVolatile;
	}

	/**
	 * Getter for the access flag "enum".
	 * @return true, if the field is an enum, false otherwise.
	 */
	public boolean isAccEnum() {
		return this.accEnum;
	}

	/**
	 * Build and return a String representation of the access flags.
	 * @return A String representation of the access flags.
	 */
	@Override
	public String getPrefix() {
		String prefix = super.getPrefix();

		// currently not in Modifier.toString(mod)
		if (this.accEnum) {
			prefix += "enum ";
		}

		return prefix;
	}

	/**
	 * Get a String representation of the type of this field.
	 * 
	 * @return A String representation of the type.
	 */
	public String getType() {
		return getLongTypeDescription(getDescriptor());
	}

	/**
	 * Find out if the type is a primitive type. If the return type is an array that consists
	 * of primitive elements, this method will return true. This behavior is different from
	 * {@link java.lang.Class#isPrimitive()}, which would not return true in that case.
	 *
	 * @return true, if the type of this field is primitive, false otherwise.
	 */
	public boolean isPrimitiveType() {
		return getDescriptor().replace("[", "").length() == 1;
	}

	/**
	 * Get the full name including prefixes and type.
	 *
	 * @return The full name.
	 */
	@Override
	public String getFullName() {
		return getPrefix() + getType() + " " + getName();
	}

}
