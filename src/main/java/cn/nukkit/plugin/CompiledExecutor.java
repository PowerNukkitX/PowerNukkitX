package cn.nukkit.plugin;

import java.lang.reflect.Method;

public interface CompiledExecutor {
    /**
     *
     * @return the method to be executed when fallback
     */
    Method getOriginMethod();
}
