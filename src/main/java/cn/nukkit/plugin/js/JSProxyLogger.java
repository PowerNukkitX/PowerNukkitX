package cn.nukkit.plugin.js;

import cn.nukkit.plugin.PluginLogger;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;

public final class JSProxyLogger implements ProxyObject {
    private static final Value NULL = Value.asValue(null);

    private final ProxyExecutable log;
    private final ProxyExecutable warn;
    private final ProxyExecutable debug;
    private final ProxyExecutable error;

    public JSProxyLogger(PluginLogger logger) {
        this.log = arguments -> {
            logger.info(joinValues(arguments));
            return NULL;
        };
        this.warn = arguments -> {
            logger.warning(joinValues(arguments));
            return NULL;
        };
        this.debug = arguments -> {
            logger.debug(joinValues(arguments));
            return NULL;
        };
        this.error = arguments -> {
            logger.error(joinValues(arguments));
            return NULL;
        };
    }

    private String joinValues(Value... values) {
        var sb = new StringBuilder();
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            var each = values[i];
            if (i != 0) {
                sb.append(' ');
            }
            sb.append(each.toString());
        }
        return sb.toString();
    }

    @Override
    public Object getMember(String key) {
        return switch (key) {
            case "log", "info" -> log;
            case "warn" -> warn;
            case "error" -> error;
            case "debug" -> debug;
            default -> null;
        };
    }

    @Override
    public Object getMemberKeys() {
        return new ProxyArray() {
            @Override
            public Object get(long index) {
                return switch ((int) index) {
                    case 0 -> "log";
                    case 1 -> "info";
                    case 2 -> "warn";
                    case 3 -> "error";
                    case 4 -> "debug";
                    default -> null;
                };
            }

            @Override
            public void set(long index, Value value) {

            }

            @Override
            public long getSize() {
                return 5;
            }
        };
    }

    @Override
    public boolean hasMember(String key) {
        return "log".equals(key) || "info".equals(key) || "warn".equals(key) || "error".equals(key) || "debug".equals(key);
    }

    @Override
    public void putMember(String key, Value value) {

    }
}
