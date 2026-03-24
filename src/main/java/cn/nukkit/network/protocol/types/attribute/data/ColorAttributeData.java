package cn.nukkit.network.protocol.types.attribute.data;

import lombok.Data;

@Data
public class ColorAttributeData implements AttributeData {

    private Integer color;
    private Operation operation;

    public enum Operation {
        OVERRIDE,
        ALPHA_BLEND,
        ADD,
        SUBTRACT,
        MULTIPLY;

        private static final Operation[] VALUES = values();

        public static Operation from(int ordinal) {
            if (ordinal >= 0 && ordinal < VALUES.length) {
                return VALUES[ordinal];
            }
            throw new UnsupportedOperationException("Detected unknown ColorAttributeData.Operation ID: " + ordinal);
        }
    }

    @Override
    public AttributeData.Type getType() {
        return Type.COLOR;
    }
}