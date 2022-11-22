package cn.nukkit.level.terra.handles;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.40-r3")
@Getter
@Setter
public class JeBlockState {

    private final String identifier;
    private final Map<String, Object> attributes = new Object2ObjectArrayMap<>(1);
    private boolean equalsIgnoreAttributes = false;
    private boolean equalsIgnoreWaterlogged = false;

    public JeBlockState(String str) {
        var strings = str.replace("[", ",").replace("]", ",").replace(" ", "").split(",");
        identifier = strings[0];
        if (strings.length > 1) {
            for (int i = 1; i < strings.length; i++) {
                final var tmp = strings[i];
                final var index = tmp.indexOf("=");
                attributes.put(tmp.substring(0, index), tmp.substring(index + 1));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JeBlockState state) {
            if (equalsIgnoreAttributes || state.equalsIgnoreAttributes) {
                if (state.identifier.equals(identifier)) return true;
            }
            if (equalsIgnoreWaterlogged || state.equalsIgnoreWaterlogged) {
                var m1 = new Object2ObjectArrayMap<>(attributes);
                var m2 = new Object2ObjectArrayMap<>(state.attributes);
                m1.remove("waterlogged");
                m2.remove("waterlogged");
                if (state.identifier.equals(identifier) && m1.equals(m2)) return true;
            }
            return state.identifier.equals(identifier) && attributes.equals(state.attributes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(identifier).append(";");
        attributes.forEach((k, v) -> ret.append(k).append("=").append(v).append(";"));
        return ret.toString();
    }
}
