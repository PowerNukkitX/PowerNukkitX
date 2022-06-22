package cn.nukkit.plugin.js.feature;

import cn.nukkit.plugin.js.JSFeature;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.proxy.Proxy;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.*;

public class TestFeature implements JSFeature {
    @Override
    public String getName() {
        return "testFeature";
    }

    @Override
    public Collection<String> availableModuleNames() {
        return List.of("test");
    }

    @Override
    public Map<String, Proxy> generateModule(String moduleName, Context context) {
        if ("test".equals(moduleName)) {
            var map = new HashMap<String, Proxy>(1, 1);
            map.put("answer", (ProxyExecutable) arguments ->
                    "PNX!!!");
            return map;
        }
        return Collections.emptyMap();
    }
}
