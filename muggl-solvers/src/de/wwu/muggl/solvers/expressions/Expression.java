package de.wwu.muggl.solvers.expressions;

import de.wwu.muggl.solvers.Solution;
import de.wwu.muggl.solvers.solver.constraints.Assignment;

/**
 * @author Christoph Lembeck
 * 
 */
public interface Expression{

    /**
     * @author Marko Ernsting, Jan C. Dageförde
     */
    enum Type{
        BOOLEAN(Expression.BOOLEAN),
        BYTE(Expression.BYTE),
        CHAR(Expression.CHAR),
        DOUBLE(Expression.DOUBLE),
        FLOAT(Expression.FLOAT),
        INT(Expression.INT),
        LONG(Expression.LONG),
        SHORT(Expression.SHORT),
        ARRAY(Expression.ARRAY),
        OBJECT(Expression.OBJECT),
        CLASSCONSTRAINT((byte)20);

        private final byte type;

        Type(byte type){
            this.type = type;
        }

        public byte toByte() {
            return type;
        }
    }

    /**
     * Represents the basic type boolean.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte BOOLEAN     = 1;
    /**
     * Represents the basic type byte.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte BYTE        = 2;
    /**
     * Represents the basic type double.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte DOUBLE      = 8;
    /**
     * Represents the basic type float.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte FLOAT       = 6;
    /**
     * Represents the basic type int.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte INT         = 5;
    /**
     * Represents the basic type long.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte LONG        = 7;
    /**
     * Represents the basic type short.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte SHORT       = 4;
    /**
     * Represents the basic type char.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte CHAR        = 3;
    
    /**
     * Represents an array.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte ARRAY       = 10;
    /**
     * Represents the "empty element" null.
     */
    // public static final byte NULL        = 9;

    /**
     * Represents an object.
     * @deprecated use the Expression.Type enum instead!
     */
    @Deprecated
    public static final byte OBJECT      = 11;

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
    