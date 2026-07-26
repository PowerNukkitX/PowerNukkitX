package org.powernukkitx.utils.exception;

import org.powernukkitx.utils.ServerException;

public class CustomBlockStateRegisterException extends ServerException {
    public CustomBlockStateRegisterException(String message) {
        super(message);
    }

    public CustomBlockStateRegisterException(String message, Throwable cause) {
        super(message, cause);
    }
}
