package cn.nukkit.network.protocol.types.attribute.data;

import lombok.Data;

@Data
public class BoolAttributeData implements AttributeData {

    private boolean value;
    private Operation operation;

    public enum Operation {
        OVERRIDE,
        ALPHA_BLEND,
        AND,
        NAND,
        OR,
        NOR,
        XOR,
        XNOR;

        private static final Operation[] VALUES = values();

        public static Operation from(int ordinal) {
            if (ordinal >= 0 && ordinal < VALUES.length) {
                return VALUES[ordinal];
            }
            throw new UnsupportedOperationException("Detected unknown BoolAttributeData.Operation ID: " + ordinal);
        }
    }

    @Override
    public AttributeData.Type getType() {
        return Type.BOOL;
    }
}