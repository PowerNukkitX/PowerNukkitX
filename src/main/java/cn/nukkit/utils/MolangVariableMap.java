package cn.nukkit.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.nukkit.math.Vector3;

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

    /** Set a float variable */
    public MolangVariableMap setFloat(String varName, float value) {
        put(varName, "float", Float.valueOf(value));
        return this;
    }

    /** Set a int variable */
    public MolangVariableMap setInt(String varName, int value) {
        put(varName, "int", Integer.valueOf(value));
        return this;
    }

    /** Set a boolean variable */
    public MolangVariableMap setBool(String varName, boolean value) {
        put(varName, "bool", Boolean.valueOf(value));
        return this;
    }

    /** Set a string variable */
    public MolangVariableMap setString(String varName, String value) {
        Objects.requireNonNull(value, "value");
        put(varName, "string", value);
        return this;
    }

    /** Set a Vec3 location */
    public MolangVariableMap setVec3(String varName, float x, float y, float z) {
        ArrayList<Entry> children = new ArrayList<>(3);
        children.add(new Entry(".x", "float", Float.valueOf(x)));
        children.add(new Entry(".y", "float", Float.valueOf(y)));
        children.add(new Entry(".z", "float", Float.valueOf(z)));
        put(varName, "member_array", children);
        return this;
    }

    /** RGB Colors set as value [0-1] */
    public MolangVariableMap setColorRGB(String varName, float r, float g, float b) {
        ArrayList<Entry> children = new ArrayList<>(3);
        children.add(new Entry(".r", "float", Float.valueOf(r)));
        children.add(new Entry(".g", "float", Float.valueOf(g)));
        children.add(new Entry(".b", "float", Float.valueOf(b)));
        put(varName, "member_array", children);
        return this;
    }

    /** RGBA Colors set as value [0-1] */
    public MolangVariableMap setColorRGBA(String varName, float r, float g, float b, float a) {
        java.util.ArrayList<Entry> children = new java.util.ArrayList<>(4);
        children.add(new Entry(".r", "float", Float.valueOf(r)));
        children.add(new Entry(".g", "float", Float.valueOf(g)));
        children.add(new Entry(".b", "float", Float.valueOf(b)));
        children.add(new Entry(".a", "float", Float.valueOf(a)));
        put(varName, "member_array", children);
        return this;
    }

    /** Set speed and direction animation */
    public MolangVariableMap setSpeedAndDirection(String varName, float speed, Vector3 direction) {
        setFloat(varName + ".speed",        speed);
        setFloat(varName + ".direction_x",  (float) direction.x);
        setFloat(varName + ".direction_y",  (float) direction.y);
        setFloat(varName + ".direction_z",  (float) direction.z);
        return this;
    }

    public boolean remove(String varName) {
        return vars.remove(varName) != null;
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
                    sb.append(((Boolean) e.value) ? "true" : "false");
                    break;
                case "string":
                    sb.append('"').append(escape((String) e.value)).append('"');
                    break;
                case "member_array": {
                    @SuppressWarnings("unchecked")
                    List<Entry> children = (List<Entry>) e.value;
                    sb.append('[');
                    boolean firstChild = true;
                    for (Entry c : children) {
                        if (!firstChild) sb.append(',');
                        firstChild = false;
                        sb.append('{')
                        .append("\"name\":\"").append(escape(c.name)).append("\",")
                        .append("\"value\":{")
                        .append("\"type\":\"").append(c.type).append("\",")
                        .append("\"value\":");
                        switch (c.type) {
                            case "float":
                            case "int": sb.append(c.value.toString()); break;
                            case "bool": sb.append(((Boolean)c.value) ? "true" : "false"); break;
                            case "string": sb.append('"').append(escape((String)c.value)).append('"'); break;
                            default: sb.append('"').append(escape(String.valueOf(c.value))).append('"');
                        }
                        sb.append("}}");
                    }
                    sb.append(']');
                    break;
                }
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
