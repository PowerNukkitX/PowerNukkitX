package cn.nukkit.event;

import java.lang.reflect.Method;

public interface CompiledExecutor {
    /**
     *
     * @return the method to be executed when fallback
     */
    Method getOriginMethod();
}
