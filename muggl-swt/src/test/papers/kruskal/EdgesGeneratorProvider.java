package test.papers.kruskal;

import de.wwu.muggl.symbolic.generating.Generator;
import de.wwu.muggl.symbolic.generating.GeneratorProvider;

/**
 * This is the GeneratorProvider for an EdgesGenerator.<br />
 * <br />
 * Last modified: 2008-11-24
 * 
 * @see test.papers.kruskal.EdgesGenerator
 * @author Tim Majchrzak
 * @version 1.0.0
 */
public class EdgesGeneratorProvider implements GeneratorProvider {

	/**
	 * Provide the instance of a EdgesGenerator with standard settings.
	 * 
	 * @param parameterName The name of the parameter. Will be ignored.
	 * @return The instance of a EdgesGenerator.
	 */
	public Generator provideInstance(String parameterName) {
		return new EdgesGenerator();
	}
	
	/**
	 * Get the full path of the generator, including the packages it is in
	 * and ".class".
	 * 
	 * @return The full path of the generator
	 */
	public String getFullGeneratorPath() {
		return "test.papers.kruskal.EdgesGenerator"; 
	}

	/**
	 * Get a description of what the generator provider does and how it works.
	 * @return A description of the generator provider.
	 */
	public String getDescription() {
		return "Provides an IEdgesGenerator with default settings.";
	}
}
