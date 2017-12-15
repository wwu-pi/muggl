package de.wwu.muggl.solvers.expressions;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.solver.constraints.Assignment;

/**
 * @author Christoph Lembeck
 * 
 */
public interface Expression {

    /**
     * TODOME: change type to this style
     * @author Marko Ernsting
     */
    public enum Type{ 
		BOOLEAN,
		BYTE,
	    CHAR,
		DOUBLE,
		FLOAT,
		INT,
		LONG,
		SHORT;
	
    	/**
    	 * Get the expression type by the given primitive wrapper type of <code>typeString</code>.
    	 * @param typeString
    	 * @return the expression type (>=0), or -1 if not existent
    	 */
    	public static byte getPrimitiveWrapperTypeByString(String typeString) {
    		switch(typeString) {
				case "java.lang.Byte"      : return Expression.BYTE;
				case "java.lang.Short"     : return Expression.SHORT;
				case "java.lang.Integer"   : return Expression.INT;
				case "java.lang.Long"      : return Expression.LONG;
				case "java.lang.Character" : return Expression.CHAR;
				case "java.lang.Boolean"   : return Expression.BOOLEAN;
				case "java.lang.Float"     : return Expression.FLOAT;
				case "java.lang.Double"    : return Expression.DOUBLE;
				default: return -1;
    		}
		}
    	
    	public static byte getPrimitiveTypeByString(String typeString) {
    		switch(typeString) {
				case "byte"      : return Expression.BYTE;
				case "short"     : return Expression.SHORT;
				case "int"       : return Expression.INT;
				case "long"      : return Expression.LONG;
				case "char"      : return Expression.CHAR;
				case "boolean"   : return Expression.BOOLEAN;
				case "float"     : return Expression.FLOAT;
				case "double"    : return Expression.DOUBLE;
				default: return -1;
    		}
		}
    	
    	public static String getPrimitiveWrapper(String typeString) {
    		switch(typeString) {
				case "byte"      : return Byte.class.getName();
				case "short"     : return Short.class.getName();
				case "int"       : return Integer.class.getName();
				case "long"      : return Long.class.getName();
				case "char"      : return Character.class.getName();
				case "boolean"   : return Boolean.class.getName();
				case "float"     : return Float.class.getName();
				case "double"    : return Double.class.getName();
				default: return null;
			}
    	}
    }

    /**
     * Represents the basic type boolean.
     */
    public static final byte BOOLEAN     = 1;
    /**
     * Represents the basic type byte.
     */
    public static final byte BYTE        = 2;
    /**
     * Represents the basic type double.
     */
    public static final byte DOUBLE      = 8;
    /**
     * Represents the basic type float.
     */
    public static final byte FLOAT       = 6;
    /**
     * Represents the basic type int.
     */
    public static final byte INT         = 5;
    /**
     * Represents the basic type long.
     */
    public static final byte LONG        = 7;
    /**
     * Represents the basic type short.
     */
    public static final byte SHORT       = 4;
    /**
     * Represents the basic type char.
     */
    public static final byte CHAR        = 3;
    
    /**
     * Represents an array.
     */
    // public static final byte ARRAY       = 10;
    /**
     * Represents the "empty element" null.
     */
    // public static final byte NULL        = 9;

    /**
     * Represents an object.
     */
    // public static final byte OBJECT      = 11;

    /**
     * represents a void return value
     */
    // public static final byte VOID		   = -1;

    /**
     * represents an exception that is uncaught
     */
    // public static final byte EXCEPTION   = 12;

    /**
     * Represents the lower border for numeric values.
     */
    // public static final byte NUMERIC_LOW = 1;

    /**
     * Represents the upper border for numeric values.
     */
    // public static final byte NUMERIC_HIGH = 9;

    
    /**
     * Verifies the internal types of the whole expression tree and throws
     * an TypeCheckException if the tree contains compositions of objects whose
     * types do not fit together (e.g. an addition of boolean variables).
     * @throws TypeCheckException if the tree contains compositions of objects
     * whose types do not fit together.
     */
    public abstract void checkTypes() throws TypeCheckException;

    /**
     * Substitutes the contained variables of the expression by the values defined
     * in the passed solution object. Remaining variables may be substituted by
     * zeros using the produceNumericSolution parameter.
     * @param solution the bindings that should be used for the substitutions.
     * @param produceNumericSolution <code>true</code> if the missing variables
     * should be replaced by zeros, <code>false</code> if the variables that are
     * not specified in the solution object should remain in the resulting expression.
     * @return the new Expression that does not contain the variables specified in
     * the solution object any more.
     */
    public abstract Expression insert(Solution solution, boolean produceNumericSolution);

    /**
     * TODOME: doc!
     * @param assignment
     * @return
     */
    public abstract Expression insertAssignment(Assignment assignment);
    
    /**
     * Returns whether the actual expression object is of type boolean or not
     * @return <i>true</i> if the expression is of type boolean, <i>false</i>
     * otherwise.
     */
    public abstract boolean isBoolean();

    /**
     * Checks whether the expression is a constant or a combined term containing
     * unbound variables.
     * @return <code>true</code> if the expression is a constant expression,
     * <code>false</code> if the expression contains variables.
     */
    public abstract boolean isConstant();

    /**
     * Returns a string representation of the expression.
     * @param useInternalVariableNames <code>true</code> if the internal
     * names of the variables should be used in the output, <code>false</code> if
     * the originally given names should be used.
     * @return a string representation of the expression.
     */
    public abstract String toString(boolean useInternalVariableNames);

    /**
     * Returns the type of the actual expression.
     * @return the type of this expression.
     */
    public abstract byte getType();
    
    /**
     * Returns a string which can be embedded into a tex file.
     * @param useInternalVariableNames
     * @return
     */
    public abstract String toTexString(boolean useInternalVariableNames);
    
    /**
     * @return
     */
    public abstract String toHaskellString();
    
}
    