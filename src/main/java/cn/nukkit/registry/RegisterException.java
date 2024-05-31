package cn.nukkit.registry;

import cn.nukkit.utils.exception.FormativeException;

public class RegisterException extends FormativeException {
    /**
     * @deprecated 
     */
    
    public RegisterException(String msg) {
        super(msg);
    }
    /**
     * @deprecated 
     */
    

    public RegisterException(String format, Object... arguments) {
        super(format, arguments);
    }
    /**
     * @deprecated 
     */
    

    public RegisterException(Exception e) {
        super(e);
    }
}
