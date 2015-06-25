package test.papers.binaryTree;

import de.wwu.muggl.symbolic.generating.Generator;
import de.wwu.muggl.symbolic.generating.GeneratorProvider;

/**
 *
 */
public class BinaryTreeGeneratorProvider implements GeneratorProvider {

	/**
	 * @see de.wwu.muggl.symbolic.generating.GeneratorProvider#provideInstance(java.lang.String)
	 */
	public Generator provideInstance(String parameterName) {
		return new BinaryTreeGenerator(0,50,3);
	}

	/**
	 * @see de.wwu.muggl.symbolic.generating.GenericGeneratorProvider#getDescription()
	 */
	public String getDescription() {
		return "Binary tree generator provider";
	}

	/**
	 * @see de.wwu.muggl.symbolic.generating.GenericGeneratorProvider#getFullGeneratorPath()
	 */
	public String getFullGeneratorPath() {
		return "test.papers.binaryTree.BinarayTreeGenerator";
	}

}
