package org.powernukkitx.molang;

/**
 * Thrown while compiling a malformed Molang expression.
 * <p>
 * Callers that compile pack-authored expressions should not let this escape into
 * gameplay code - {@link MolangExpression#compile(String)} already turns a malformed
 * expression into one that evaluates as {@code 0}.
 */
public class MolangParseException extends RuntimeException {

    private final String expression;
    private final int position;

    public MolangParseException(String message, String expression, int position) {
        super(message + " at position " + position + " in: " + expression);
        this.expression = expression;
        this.position = position;
    }

    /**
     * @return the expression that failed to compile
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @return the zero-based offset the parser stopped at
     */
    public int getPosition() {
        return position;
    }
}
