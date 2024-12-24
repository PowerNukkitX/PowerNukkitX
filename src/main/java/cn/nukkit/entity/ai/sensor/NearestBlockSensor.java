package cn.nukkit.entity.ai.sensor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.level.Location;
import lombok.Getter;

//存储最近的玩家的Memory


@Getter
public class NearestBlockSensor implements ISensor {

    protected int range;

    protected int lookY;

    protected int period;

    public NearestBlockSensor(int range, int lookY) {
        this(range, lookY, 1);
    }

    public NearestBlockSensor(int range, int lookY, int period) {
        this.range = range;
        this.lookY = lookY;
        this.period = period;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        Class<? extends Block> blockClass = entity.getMemoryStorage().get(CoreMemoryTypes.LOOKING_BLOCK);
        if(blockClass == null) return;
        Block block = null;
        for(int x = -range; x<=range; x++) {
            for(int z = -range; z<=range; z++) {
                for(int y = -lookY; y<=lookY; y++) {
                    Location lookLocation = entity.add(x, y, z);
                    Block lookBlock = lookLocation.getLevelBlock();
                    if(lookBlock.getId().equals(Block.DIRT) || lookBlock.getId().equals(Block.GRASS_BLOCK) || lookBlock.isAir() || lookBlock.getId().equals(Block.BEDROCK)) continue;
                    if(blockClass.isAssignableFrom(lookBlock.getClass())) {
                        block = lookBlock;
                        break;
                    }
                }
            }
        }
        entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_BLOCK, block);
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
