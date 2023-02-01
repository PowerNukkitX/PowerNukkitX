package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 可携带异常信息的的结果
 *
 * @param <E> the error parameter
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public record OK<E>(boolean ok, @Nullable E error) {
    public OK(boolean ok) {
        this(ok, null);
    }

    public void assertOK() throws AssertionError {
        if (!ok) {
            if (error instanceof Throwable throwable) {
                throw new AssertionError(throwable);
            } else {
                throw new AssertionError(Objects.toString(error, "Unknown error"));
            }
        }
    }
}
