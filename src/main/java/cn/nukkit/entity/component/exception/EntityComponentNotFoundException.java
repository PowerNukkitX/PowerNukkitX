package cn.nukkit.entity.component.exception;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 组件不存在错误
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EntityComponentNotFoundException extends RuntimeException {
    public EntityComponentNotFoundException() {
    }

    public EntityComponentNotFoundException(String message) {
        super(message);
    }

    public EntityComponentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityComponentNotFoundException(Throwable cause) {
        super(cause);
    }

    public EntityComponentNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
