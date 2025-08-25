package cn.nukkit.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * MoLang variable map compatible with Bedrock's SpawnParticleEffectPacket payload.
 * Produces a JSON array of entries like BDS:
 * [
 *   {"name":"variable.direction_x","value":{"type":"float","value":0.25}},
 *   {"name":"variable.direction_y","value":{"type":"float","value":-5.0}}
 * ]
 */
public final class MolangVariableMap {

    private static final class Entry {
        final String name;
        final String type;
        final Object value;

        Entry(String name, String type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
    }

    private final LinkedHashMap<String, Entry> vars = new LinkedHashMap<>();

    public MolangVariableMap() {
    }

    public MolangVariableMap(Map<String, Float> initialFloats) {
        if (initialFloats != null) {
            for (Map.Entry<String, Float> e : initialFloats.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    setFloat(e.getKey(), e.getValue());
                }
            }
        }
    }

    public MolangVariableMap setFloat(String name, float value) {
        put(name, "float", Float.valueOf(value));
        return this;
    }

    public MolangVariableMap setInt(String name, int value) {
        put(name, "int", Integer.valueOf(value));
        return this;
    }

    public MolangVariableMap setBool(String name, boolean value) {
        put(name, "bool", Boolean.valueOf(value));
        return this;
    }

    public MolangVariableMap setString(String name, String value) {
        Objects.requireNonNull(value, "value");
        put(name, "string", value);
        return this;
    }

    public boolean remove(String name) {
        return vars.remove(name) != null;
    }

    public MolangVariableMap clear() {
        vars.clear();
        return this;
    }

    public boolean isEmpty() {
        return vars.isEmpty();
    }

    public int size() {
        return vars.size();
    }

    public String toJson() {
        if (vars.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder(vars.size() * 48).append('[');
        boolean first = true;
        for (Entry e : vars.values()) {
            if (!first) sb.append(',');
            first = false;

            final String nameOut = normalizeName(e.name);

            sb.append('{')
              .append("\"name\":\"").append(escape(nameOut)).append("\",")
              .append("\"value\":{")
              .append("\"type\":\"").append(e.type).append("\",")
              .append("\"value\":");

            switch (e.type) {
                case "float":
                case "int":
                    sb.append(e.value.toString());
                    break;
                case "bool":
                    sb.append(((Boolean) e.value).booleanValue() ? "true" : "false");
                    break;
                case "string":
                    sb.append('"').append(escape((String) e.value)).append('"');
                    break;
                default:
                    sb.append('"').append(escape(String.valueOf(e.value))).append('"');
            }

            sb.append("}}");
        }
        return sb.append(']').toString();
    }

    private static String normalizeName(String name) {
        if (name.startsWith("variable.") || name.startsWith("context.")) return name;
        return "variable." + name;
    }

    private void put(String name, String type, Object value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(value, "value");
        vars.put(name, new Entry(name, type, value));
    }

    private static String escape(String s) {
        StringBuilder out = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':
                case '\\':
                    out.append('\\').append(c);
                    break;
                case '\b': out.append("\\b"); break;
                case '\f': out.append("\\f"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        String hex = Integer.toHexString(c);
                        out.append("\\u");
                        for (int k = hex.length(); k < 4; k++) out.append('0');
                        out.append(hex);
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }
}
