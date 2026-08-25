package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.entity.condition.Condition;
import org.powernukkitx.level.entity.condition.ConditionTrue;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Getter
public class BlockSensor implements ISensor {

    //each offset component is stored in 10 bits, biased so that negative offsets stay positive
    private static final int OFFSET_BIAS = 512;
    private static final int MAX_OFFSET = OFFSET_BIAS - 1;

    private static final Map<Long, int[]> SEARCH_OFFSETS = new ConcurrentHashMap<>();

    protected int range;
    protected int lookY;
    protected int period;

    @NotNull
    protected Class<? extends Block> blockClass;

    @NotNull
    protected MemoryType<Block> memory;
    protected Condition condition;

    public BlockSensor(Class<? extends Block> blockClass, MemoryType<Block> memory, int range, int lookY) {
        this(blockClass, memory, range, lookY, 1);
    }

    public BlockSensor(@NotNull Class<? extends Block> blockClass, @NotNull MemoryType<Block> memory, int range, int lookY, int period) {
        this(blockClass, memory, range, lookY, period, new ConditionTrue());
    }
    public BlockSensor(@NotNull Class<? extends Block> blockClass, @NotNull MemoryType<Block> memory, int range, int lookY, int period, Condition condition) {
        if (range > MAX_OFFSET || lookY > MAX_OFFSET) {
            throw new IllegalArgumentException("The search volume of a block sensor is limited to " + MAX_OFFSET + " blocks");
        }
        this.blockClass = blockClass;
        this.memory = memory;
        this.range = range;
        this.lookY = lookY;
        this.period = period;
        this.condition = condition;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        Block block = null;
        Level level = entity.getLevel();
        int baseX = entity.getFloorX();
        int baseY = entity.getFloorY();
        int baseZ = entity.getFloorZ();
        //the offsets are ordered by distance, so the first match is the nearest one and ends the scan
        for(int offset : searchOffsets()) {
            Block lookBlock = level.getBlock(baseX + unpackX(offset), baseY + unpackY(offset), baseZ + unpackZ(offset));
            if(blockClass.isAssignableFrom(lookBlock.getClass()) && condition.evaluate(lookBlock)) {
                block = lookBlock;
                break;
            }
        }
        if(block == null) {
            if(entity.getMemoryStorage().notEmpty(memory) && (blockClass.isAssignableFrom(entity.getMemoryStorage().get(memory).getClass()) || entity.getMemoryStorage().get(memory).isAir())) {
                entity.getMemoryStorage().clear(memory);
            } // We don't want to clear data from different sensors
        } else entity.getMemoryStorage().put(memory, block);
    }

    @Override
    public int getPeriod() {
        return period;
    }

    /**
     * The offsets of the search volume, ordered from the closest to the farthest one. They only depend on the
     * shape of the volume, so sensors sharing a shape - every mob of a kind does - share one table.
     */
    protected int[] searchOffsets() {
        return SEARCH_OFFSETS.computeIfAbsent(((long) range << 32) | (lookY & 0xffffffffL), shape -> buildSearchOffsets());
    }

    private int[] buildSearchOffsets() {
        int[] offsets = new int[(2 * range + 1) * (2 * range + 1) * (2 * lookY + 1)];
        int index = 0;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -lookY; y <= lookY; y++) {
                    offsets[index++] = pack(x, y, z);
                }
            }
        }
        //a stable sort keeps the original iteration order between offsets that are equally far away
        return IntStream.of(offsets)
                .boxed()
                .sorted(Comparator.comparingInt(BlockSensor::squaredLength))
                .mapToInt(Integer::intValue)
                .toArray();
    }

    private static int squaredLength(int offset) {
        int x = unpackX(offset);
        int y = unpackY(offset);
        int z = unpackZ(offset);
        return x * x + y * y + z * z;
    }

    private static int pack(int x, int y, int z) {
        return ((x + OFFSET_BIAS) << 20) | ((y + OFFSET_BIAS) << 10) | (z + OFFSET_BIAS);
    }

    private static int unpackX(int offset) {
        return (offset >>> 20) - OFFSET_BIAS;
    }

    private static int unpackY(int offset) {
        return ((offset >>> 10) & 0x3ff) - OFFSET_BIAS;
    }

    private static int unpackZ(int offset) {
        return (offset & 0x3ff) - OFFSET_BIAS;
    }
}
