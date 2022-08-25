package cn.nukkit.plugin.js.feature;

import cn.nukkit.plugin.js.JSFeature;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.proxy.Proxy;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.*;

public class ConvertFeature implements JSFeature {
    @Override
    public String getName() {
        return "Convert";
    }

    @Override
    public Collection<String> availableModuleNames() {
        return List.of("Convertor", "Convert");
    }

    @Override
    public Map<String, ?> generateModule(String moduleName, Context context) {
        if ("Convertor".equals(moduleName) || "Convert".equals(moduleName)) {
            var map = new HashMap<String, Proxy>(1, 1);
            map.put("convertToJava", (ProxyExecutable) arguments -> {
                if (arguments.length == 2) {
                    //noinspection unchecked
                    return arguments[1].as(arguments[0].as(Class.class));
                } else {
                    throw new IllegalArgumentException("2 arguments are required.");
                }
            });
            return map;
        }
        return Collections.emptyMap();
    }
}
