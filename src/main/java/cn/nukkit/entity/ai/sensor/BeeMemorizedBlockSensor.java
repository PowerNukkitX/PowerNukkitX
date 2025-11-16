package cn.nukkit.entity.ai.sensor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityBee;
import cn.nukkit.level.Location;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Getter
public class BeeMemorizedBlockSensor implements ISensor {

    protected int range;
    protected int lookY;
    protected int period;

    public BeeMemorizedBlockSensor(int range, int lookY) {
        this(range, lookY, 1);
    }

    public BeeMemorizedBlockSensor(int range, int lookY, int period) {
        this.range = range;
        this.lookY = lookY;
        this.period = period;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        if (entity instanceof EntityBee bee && bee.isPollinating()) {
            entity.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_BLOCK);
            return;
        }

        Class<? extends Block> blockClass = entity.getMemoryStorage().get(CoreMemoryTypes.LOOKING_BLOCK);
        if (blockClass == null) {
            return;
        }

        int effectiveRange = this.range;

        List<Block> candidates = new ArrayList<>();

        for (int x = -effectiveRange; x <= effectiveRange; x++) {
            for (int z = -effectiveRange; z <= effectiveRange; z++) {
                for (int y = -lookY; y <= lookY; y++) {
                    Location lookLocation = entity.add(x, y, z);
                    Block lookBlock = lookLocation.getLevelBlock();

                    if (lookBlock.getId().equals(Block.DIRT)
                            || lookBlock.getId().equals(Block.GRASS_BLOCK)
                            || lookBlock.isAir()
                            || lookBlock.getId().equals(Block.BEDROCK)) {
                        continue;
                    }

                    if (blockClass.isAssignableFrom(lookBlock.getClass())) {
                        candidates.add(lookBlock);
                    }
                }
            }
        }

        if (!candidates.isEmpty()) {
            Block chosen = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
            entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_BLOCK, chosen);
        } else {
            entity.getMemoryStorage().clear(CoreMemoryTypes.NEAREST_BLOCK);
        }
    }

    @Override
    public int getPeriod() {
        return period;
    }
}
