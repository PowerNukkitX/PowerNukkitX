package cn.nukkit.network.protocol.types.attribute.data;

import lombok.Data;
import lombok.Value;

@Data
public class AttributeLayerSettings {

    private int priority;
    private WeightData weight;
    private boolean enabled;
    private boolean transitionsPaused;

    @Value
    public static class WeightData {
        Type type;
        Object value;

        public float getAsFloat() {
            return (float) this.value;
        }

        public String getAsString() {
            return (String) this.value;
        }

        public enum Type {
            FLOAT,
            STRING;

            private static final Type[] VALUES = values();

            public static Type from(int ordinal) {
                if (ordinal >= 0 && ordinal < VALUES.length) {
                    return VALUES[ordinal];
                }
                throw new UnsupportedOperationException("Detected unknown AttributeLayerSettings.WeightData.Type ID: " + ordinal);
            }
        }
    }
}