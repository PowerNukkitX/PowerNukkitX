package cn.nukkit.plugin.js;

import org.graalvm.polyglot.Context;

import java.util.Collection;
import java.util.Map;

public interface JSFeature {
    String getName();

    Collection<String> availableModuleNames();

    Map<String, ?> generateModule(String moduleName, Context context);

    default boolean needsInject() {
        return false;
    }

    default void injectIntoContext(Context context) {

    }
}
