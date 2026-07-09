package cn.nukkit.entity.effect;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EffectWeaving extends Effect {

    public EffectWeaving() {
        super(EffectType.WEAVING, "%effect.weaving", new Color(120, 105, 90), true);
    }

    @Override
    public void onDeath(Entity entity) {
        var level = entity.getLevel();
        var random = ThreadLocalRandom.current();

        List<Vector3> candidates = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Block block = level.getBlock(entity.getFloorX() + dx, entity.getFloorY() + dy, entity.getFloorZ() + dz);
                    if (block.isAir()) {
                        candidates.add(block);
                    }
                }
            }
        }

        int count = Math.min(2 + random.nextInt(2), candidates.size());
        for (int i = 0; i < count; i++) {
            Vector3 pos = candidates.remove(random.nextInt(candidates.size()));
            level.setBlock(pos, Block.get(BlockID.WEB));
        }
    }
}
