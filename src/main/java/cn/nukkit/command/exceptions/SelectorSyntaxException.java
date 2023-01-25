package cn.nukkit.command.exceptions;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class SelectorSyntaxException extends RuntimeException {

    public SelectorSyntaxException() {

    }

    public SelectorSyntaxException(String message) {
        super(message);
    }

    public SelectorSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public SelectorSyntaxException(Throwable cause) {
        super(cause);
    }

    protected SelectorSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
