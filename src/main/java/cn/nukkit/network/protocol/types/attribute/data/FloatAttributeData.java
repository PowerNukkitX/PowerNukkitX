package cn.nukkit.network.protocol.types.attribute.data;

import lombok.Data;

@Data
public class FloatAttributeData implements AttributeData {

    private float value;
    private Operation operation;
    private Float constraintMin;
    private Float constraintMax;

    public enum Operation {
        OVERRIDE,
        ALPHA_BLEND,
        ADD,
        SUBTRACT,
        MULTIPLY,
        MINIMUM,
        MAXIMUM;

        private static final Operation[] VALUES = values();

        public static Operation from(int ordinal) {
            if (ordinal >= 0 && ordinal < VALUES.length) {
                return VALUES[ordinal];
            }
            throw new UnsupportedOperationException("Detected unknown FloatAttributeData.Operation ID: " + ordinal);
        }
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }
}