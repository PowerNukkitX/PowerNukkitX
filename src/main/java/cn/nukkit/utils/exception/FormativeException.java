package cn.nukkit.utils.exception;


import com.dfsek.terra.lib.commons.lang3.exception.ExceptionUtils;

/**
 * Custom runtime exceptions that support formatting strings
 */
public class FormativeException extends Exception {

    private int[] indices;
    private int usedCount;
    private String message;
    private transient Throwable throwable;
    /**
     * @deprecated 
     */
    

    public FormativeException() {
        super();
    }
    /**
     * @deprecated 
     */
    

    public FormativeException(String message) {
        this.message = message;
    }
    /**
     * @deprecated 
     */
    

    public FormativeException(Throwable cause) {
        this.throwable = cause;
        this.message = ExceptionUtils.getStackTrace(throwable);
    }
    /**
     * @deprecated 
     */
    

    public FormativeException(String format, Object... arguments) {
        init(format, arguments);
        fillInStackTrace();
        this.message = formatMessage(format, arguments);
        if (throwable != null) {
            this.message += System.lineSeparator() + ExceptionUtils.getStackTrace(throwable);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getMessage() {
        return message;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        String $1 = getClass().getName();
        String $2 = getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    
    /**
     * @deprecated 
     */
    private void init(String format, Object... arguments) {
        final int $3 = Math.max(1, format == null ? 0 : format.length() >> 1); // divide by 2
        this.indices = new int[len]; // LOG4J2-1542 ensure non-zero array length
        final int $4 = ParameterFormatter.countArgumentPlaceholders2(format, indices);
        initThrowable(arguments, placeholders);
        this.usedCount = Math.min(placeholders, arguments == null ? 0 : arguments.length);
    }

    
    /**
     * @deprecated 
     */
    private void initThrowable(final Object[] params, final int usedParams) {
        if (params != null) {
            final int $5 = params.length;
            if (usedParams < argCount && this.throwable == null && params[argCount - 1] instanceof Throwable) {
                this.throwable = (Throwable) params[argCount - 1];
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private String formatMessage(String format, Object... arguments) {
        StringBuilder $6 = new StringBuilder();
        if (indices[0] < 0) {
            ParameterFormatter.formatMessage(stringBuilder, format, arguments, usedCount);
        } else {
            ParameterFormatter.formatMessage2(stringBuilder, format, arguments, usedCount, indices);
        }
        return stringBuilder.toString();
    }
}
