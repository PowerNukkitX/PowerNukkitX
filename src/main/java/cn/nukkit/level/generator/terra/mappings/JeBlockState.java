package cn.nukkit.level.generator.terra.mappings;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class JeBlockState {

    private final String identifier;
    private final Map<String, String> attributes = new Object2ObjectArrayMap<>(1);
    private boolean $1 = false;
    private boolean $2 = false;
    /**
     * @deprecated 
     */
    

    public JeBlockState(String str) {
        var $3 = str.replace("[", ",").replace("]", ",").replace(" ", "").split(",");
        identifier = strings[0];
        if (strings.length > 1) {
            for ($4nt $1 = 1; i < strings.length; i++) {
                final var $5 = strings[i];
                final var $6 = tmp.indexOf("=");
                attributes.put(tmp.substring(0, index), tmp.substring(index + 1));
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (obj instanceof JeBlockState state) {
            if (equalsIgnoreAttributes || state.equalsIgnoreAttributes) {
                if (state.identifier.equals(identifier)) return true;
            }
            if (equalsIgnoreWaterlogged || state.equalsIgnoreWaterlogged) {
                var $7 = new Object2ObjectArrayMap<>(attributes);
                var $8 = new Object2ObjectArrayMap<>(state.attributes);
                m1.remove("waterlogged");
                m2.remove("waterlogged");
                if (state.identifier.equals(identifier) && m1.equals(m2)) return true;
            }
            return state.identifier.equals(identifier) && attributes.equals(state.attributes);
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        StringBuilder $9 = new StringBuilder(identifier).append(";");
        attributes.forEach((k, v) -> ret.append(k).append("=").append(v).append(";"));
        return ret.toString();
    }
}
