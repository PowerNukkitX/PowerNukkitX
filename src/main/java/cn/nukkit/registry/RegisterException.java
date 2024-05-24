package cn.nukkit.registry;

import cn.nukkit.utils.exception.FormativeException;

public class RegisterException extends FormativeException {
    public RegisterException(String msg) {
        super(msg);
    }

    public RegisterException(String format, Object... arguments) {
        super(format, arguments);
    }

    public RegisterException(Exception e) {
        super(e);
    }
}
