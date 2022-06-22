package cn.nukkit.plugin.js;

import cn.nukkit.plugin.js.feature.TestFeature;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.graalvm.polyglot.proxy.Proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class JSFeatures {
    public static final Map<String, JSFeature> JS_FEATURE_MAP = new HashMap<>();
    public static final Map<String, JSFeature> FEATURE_MODULE_MAP = new HashMap<>();
    public static final Int2ObjectOpenHashMap<Proxy> FEATURE_GENERATED_TMP_MAP = new Int2ObjectOpenHashMap<>();
    public static final AtomicInteger FEATURE_GENERATED_TMP_ID = new AtomicInteger(0);

    private JSFeatures() {
    }

    public static void registerFeature(JSFeature feature) {
        JS_FEATURE_MAP.put(feature.getName(), feature);
        for (String moduleName : feature.availableModuleNames()) {
            FEATURE_MODULE_MAP.put(moduleName, feature);
        }
    }

    public static JSFeature getFeature(String name) {
        return JS_FEATURE_MAP.get(name);
    }

    public static JSFeature getFeatureByModule(String moduleName) {
        return FEATURE_MODULE_MAP.get(moduleName);
    }

    public static boolean hasFeature(String name) {
        return JS_FEATURE_MAP.containsKey(name);
    }

    public static boolean hasFeatureByModule(String moduleName) {
        return FEATURE_MODULE_MAP.containsKey(moduleName);
    }

    public static void unregisterFeature(String name) {
        JS_FEATURE_MAP.remove(name);
        for (String moduleName : JS_FEATURE_MAP.get(name).availableModuleNames()) {
            FEATURE_MODULE_MAP.remove(moduleName);
        }
    }

    public static void clearFeatures() {
        JS_FEATURE_MAP.clear();
    }

    public static void initInternalFeatures() {
        registerFeature(new TestFeature());
    }
}
