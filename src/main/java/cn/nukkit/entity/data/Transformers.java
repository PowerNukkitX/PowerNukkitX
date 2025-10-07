package cn.nukkit.entity.data;

import cn.nukkit.block.Block;

import java.util.EnumSet;
import java.util.function.Function;

public class Transformers {
    public static final Function<Block, Integer> BLOCK = (block -> block.getBlockState().blockStateHash());
    public static final Function<Boolean, Byte> BOOLEAN_TO_BYTE = (b -> b ? (byte) 1 : (byte) 0);
    public static final Function<EnumSet<EntityFlag>, Long> FLAGS = (set) -> packEntityFlags(set, 0);
    public static final Function<EnumSet<EntityFlag>, Long> FLAGS_EXTEND = (set) -> packEntityFlags(set, 64);

    private static long packEntityFlags(EnumSet<EntityFlag> set, int base) {
        long bits = 0L;
        if (set == null || set.isEmpty()) return bits;
        for (EntityFlag f : set) {
            int id = f.getValue();
            if (id >= base && id < base + 64) {
                bits |= (1L << (id & 0x3F));
            }
        }
        return bits;
    }
}
