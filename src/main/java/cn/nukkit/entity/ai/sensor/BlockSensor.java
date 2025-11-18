package cn.nukkit.entity.ai.sensor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.level.Location;
import cn.nukkit.level.entity.condition.Condition;
import cn.nukkit.level.entity.condition.ConditionTrue;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class BlockSensor implements ISensor {

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
        this.blockClass = blockClass;
        this.memory = memory;
        this.range = range;
        this.lookY = lookY;
        this.period = period;
        this.condition = condition;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        double distance = Double.MAX_VALUE;
        Block block = null;
        for(int x = -range; x<=range; x++) {
            for(int z = -range; z<=range; z++) {
                for(int y = -lookY; y<=lookY; y++) {
                    Location lookLocation = entity.add(x, y, z);
                    double lookDist = lookLocation.distance(entity);
                    if(lookDist < distance) {
                        Block lookBlock = lookLocation.getLevelBlock();
                        if(blockClass.isAssignableFrom(lookBlock.getClass())) {
                            if(condition.evaluate(lookBlock)) {
                                block = lookBlock;
                                distance = lookDist;
                            }
                        }
                    }
                }
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
}
