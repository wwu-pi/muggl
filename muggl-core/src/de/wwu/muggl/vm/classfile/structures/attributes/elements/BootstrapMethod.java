package de.wwu.muggl.vm.classfile.structures.attributes.elements;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.ClassFileStructure;

/**
 * Representation of a bootstrap_method entry
 *
 * @author Max Schulze
 */
public class BootstrapMethod extends ClassFileStructure {
	private int bootstrapMethodRef;
	private int numBootstrapArguments;
	private int[] bootstrapArguments;

	/**
	 * Basic constructor.
	 * 
	 * @param classFile
	 *            The ClassFile the local_variable_table belongs to.
	 * @throws IOException
	 *             Thrown on errors reading from the DataInputStream of the
	 *             class.
	 * @throws ClassFileException
	 *             If any incorrect values are detected.
	 */
	public BootstrapMethod(ClassFile classFile)
			throws IOException, ClassFileException {
		super(classFile);

		this.bootstrapMethodRef = classFile.getDis().readUnsignedShort();
		this.numBootstrapArguments = classFile.getDis().readUnsignedShort();
		this.bootstrapArguments = new int[numBootstrapArguments];

		for (int b = 0; b < numBootstrapArguments; b++) {
			this.bootstrapArguments[b] = classFile.getDis().readUnsignedShort();
		}

		checkIndexIntoTheConstantPool(this.bootstrapMethodRef);
		if (this.classFile.getConstantPool()[this.bootstrapMethodRef]
				.getTag() != ClassFile.CONSTANT_METHODHANDLE) {
			throw new ClassFileException(
					"Encountered a corrupt class file: bootstrap_method_ref of a bootstrap_method should point to a CONSTANT_MethodHandle.");
		}
		if (Globals.getInst().logger.isTraceEnabled())
			Globals.getInst().logger
					.trace("Parsing: Read a bootstrap_method. MethodRef "
							+ this.bootstrapMethodRef + " arguments: "
							+ Arrays.stream(bootstrapArguments)
									.mapToObj(i -> Integer.toString(i))
									.collect(Collectors.joining(", ")));
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
		dos.writeShort(this.bootstrapMethodRef);
		dos.writeShort(this.numBootstrapArguments);
		for (int a = 0; a < this.numBootstrapArguments; a++) {
			dos.writeShort(this.bootstrapArguments[a]);
		}
	}

	public int getBootstrapMethodRef() {
		return bootstrapMethodRef;
	}

	public int getNumBootstrapArguments() {
		return numBootstrapArguments;
	}

	public int[] getBootstrapArguments() {
		return bootstrapArguments;
	}

	/**
	 * Get a String representation of this class file's structure name.
	 * 
	 * @return A String representation of this class file's structure name.
	 */
	@Override
	public String getStructureName() {
		return "bootstrap_method";
	}

}
