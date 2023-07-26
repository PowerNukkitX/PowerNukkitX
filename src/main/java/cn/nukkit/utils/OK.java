package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * 可携带异常信息的的结果
 *
 * @param <E> the error parameter
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public record OK<E>(boolean ok, @Nullable E error) {
    public static final OK<Void> TRUE = new OK<>(true);

    public OK(boolean ok) {
        this(ok, null);
    }

    public Throwable getError() {
        if (error instanceof Throwable throwable) {
            return new AssertionError(throwable);
        } else {
            return new AssertionError(Objects.toString(error, "Unknown error"));
        }
    }

    /**
     * 断言该结果是否为真，如果不为真则会抛出AssertionError
     * <p>
     * Asserts whether the result is true or not, and throws an AssertionError if it is not true
     *
     * @throws AssertionError the assertion error
     */
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
