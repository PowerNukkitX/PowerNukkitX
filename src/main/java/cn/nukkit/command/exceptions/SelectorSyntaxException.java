package cn.nukkit.command.exceptions;

/**
 * Exception thrown when a selector syntax error occurs in command parsing.
 * <p>
 * This exception is used to indicate that a selector (such as @p, @a, @e, etc.) in a command
 * contains invalid syntax or cannot be parsed correctly. It provides detailed error messages,
 * including the full cause chain, to help diagnose and resolve selector issues.
 * <p>
 * Features:
 * <ul>
 *   <li>Supports multiple constructors for different error scenarios (message, cause, suppression, stack trace).</li>
 *   <li>Overrides {@link #getMessage()} to include the full cause chain for easier debugging.</li>
 *   <li>Can be used in command parsing, selector validation, and error reporting systems.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Throw this exception when a selector in a command is invalid or cannot be parsed.</li>
 *   <li>Use the detailed message output for logging and user feedback.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * if (!isValidSelector(selector)) {
 *     throw new SelectorSyntaxException("Invalid selector syntax: " + selector);
 * }
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @since PowerNukkitX 2.0.0
 */
public class SelectorSyntaxException extends Exception {

    /**
     * Constructs a SelectorSyntaxException with no detail message.
     */
    public SelectorSyntaxException() {}

    /**
     * Constructs a SelectorSyntaxException with the specified detail message.
     *
     * @param message the detail message
     */
    public SelectorSyntaxException(String message) {
        super(message);
    }

    /**
     * Constructs a SelectorSyntaxException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public SelectorSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SelectorSyntaxException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public SelectorSyntaxException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SelectorSyntaxException with full control over suppression and stack trace writability.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param enableSuppression whether suppression is enabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    protected SelectorSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Returns the detail message string of this exception, including the full cause chain.
     * <p>
     * The message includes all nested causes, formatted for easier debugging and error reporting.
     *
     * @return the detailed message string with cause chain
     */
    @Override
    public String getMessage() {
        var builder = new StringBuilder(super.getMessage());
        Throwable t = this;
        while (t.getCause() != null) {
            // Traverse to the deepest cause
            t = t.getCause();
            builder.append("\n");
            builder.append("§cCaused by ");
            builder.append(t.getClass().getSimpleName());
            builder.append(": ");
            builder.append(t.getMessage());
        }
        return builder.toString();
    }
}
