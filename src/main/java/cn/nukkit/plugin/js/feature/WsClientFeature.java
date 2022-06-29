package cn.nukkit.plugin.js.feature;

import cn.nukkit.plugin.js.JSFeature;
import cn.nukkit.plugin.js.feature.ws.WsClientBuilder;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.proxy.Proxy;

import java.util.*;

public class WsClientFeature implements JSFeature {
    @Override
    public String getName() {
        return "WsClient";
    }

    @Override
    public Collection<String> availableModuleNames() {
        return List.of("WsClient");
    }

    @Override
    public Map<String, Proxy> generateModule(String moduleName, Context context) {
        if ("WsClient".equals(moduleName)) {
            var map = new HashMap<String, Proxy>(1, 1);
            map.put("WsClientBuilder", new WsClientBuilder(context));
            return map;
        }
        return Collections.emptyMap();
    }
}
