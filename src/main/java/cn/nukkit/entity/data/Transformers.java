package cn.nukkit.entity.data;

import cn.nukkit.block.Block;

import java.util.EnumSet;
import java.util.function.Function;

public class Transformers {
    public static final Function<Block, Integer> BLOCK = (block -> block.getBlockState().blockStateHash());
    public static final Function<Boolean, Byte> BOOLEAN_TO_BYTE = (b -> b ? (byte) 1 : (byte) 0);
    public static final Function<EnumSet<EntityFlag>, Long> FLAGS = (set) -> {
        long value = 0;
        int lower = 0;
        int upper = lower + 64;
        for (EntityFlag flag : set) {
            int flagIndex = flag.getValue();
            if (flagIndex >= lower && flagIndex < upper) {
                value |= 1L << (flagIndex & 0x3f);
            }
        }
        return value;
    };
    public static final Function<EnumSet<EntityFlag>, Long> FLAGS_EXTEND = (set) -> {
        long value = 0;
        int lower = 64;
        int upper = lower + 64;
        for (EntityFlag flag : set) {
            int flagIndex = flag.getValue();
            if (flagIndex >= lower && flagIndex < upper) {
                value |= 1L << (flagIndex & 0x3f);
            }
        }
        return value;
    };
}
