package cn.nukkit.command.exceptions;


public class SelectorSyntaxException extends Exception {
    /**
     * @deprecated 
     */
    

    public SelectorSyntaxException() {}
    /**
     * @deprecated 
     */
    

    public SelectorSyntaxException(String message) {
        super(message);
    }
    /**
     * @deprecated 
     */
    

    public SelectorSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @deprecated 
     */
    

    public SelectorSyntaxException(Throwable cause) {
        super(cause);
    }

    
    /**
     * @deprecated 
     */
    protected SelectorSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getMessage() {
        var $1 = new StringBuilder(super.getMessage());
        Throwable $2 = this;
        while (t.getCause() != null) {
            //到达最底层
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
