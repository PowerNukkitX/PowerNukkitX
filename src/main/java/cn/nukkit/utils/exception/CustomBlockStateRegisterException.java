package cn.nukkit.utils.exception;

import cn.nukkit.utils.ServerException;

public class CustomBlockStateRegisterException extends ServerException {
    public CustomBlockStateRegisterException(String message) {
        super(message);
    }

    public CustomBlockStateRegisterException(String message, Throwable cause) {
        super(message, cause);
    }
}
