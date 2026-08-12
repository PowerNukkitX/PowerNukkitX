package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockFire;
import org.powernukkitx.block.BlockIronBars;
import org.powernukkitx.block.BlockObsidian;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.random.RandomSourceProvider;
import java.util.concurrent.ThreadLocalRandom;

import static org.powernukkitx.block.property.CommonBlockProperties.INFINIBURN_BIT;

public class ObjectObsidianPillar extends ObjectGenerator {
    private final int radius;
    private final int height;
    private final boolean guarded;

    public ObjectObsidianPillar(int radius, int height, boolean guarded) {
        this.radius = radius;
        this.height = height;
        this.guarded = guarded;
    }

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        int x = position.getFloorX();
        int z = position.getFloorZ();

        for (int i = level.getLevel().getMinHeight(); i <= height + 10; i++) {
            for (int j = -radius; j <= radius; j++) {
                for (int k = -radius; k <= radius; k++) {
                    if (j * j + k * k <= radius * radius + 1 && i < height) {
                        level.setBlockStateAt(x + j, i, z + k, BlockObsidian.PROPERTIES.getDefaultState());
                    } else if (i > 65) {
                        level.setBlockStateAt(x + j, i, z + k, BlockAir.STATE);
                    }
                }
            }
        }

        if (guarded) {
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j <= 2; ++j) {
                    if (Math.abs(i) == 2 || Math.abs(j) == 2) {
                        for (int k = 0; k < 3; ++k) {
                            level.setBlockStateAt(x + i, height + k, z + j, BlockIronBars.PROPERTIES.getDefaultState());
                        }
                    }
                    level.setBlockStateAt(x + i, height + 3, z + j, BlockIronBars.PROPERTIES.getDefaultState());
                }
            }
        }

        level.setBlockStateAt(x, height, z, BlockBedrock.PROPERTIES.getBlockState(INFINIBURN_BIT.createValue(true)));
        level.setBlockStateAt(x, height + 1, z, BlockFire.PROPERTIES.getDefaultState());
        level.addHook(() -> {
            CompoundTag nbt = new CompoundTag()
                    .putList("Pos", new ListTag<DoubleTag>()
                            .add(new DoubleTag(x + 0.5))
                            .add(new DoubleTag(height + 1))
                            .add(new DoubleTag(z + 0.5)))
                    .putList("Motion", new ListTag<DoubleTag>()
                            .add(new DoubleTag(0))
                            .add(new DoubleTag(0))
                            .add(new DoubleTag(0)))
                    .putList("Rotation", new ListTag<FloatTag>()
                            .add(new FloatTag(ThreadLocalRandom.current().nextFloat() * 360))
                            .add(new FloatTag(0)));

            Entity entity = Entity.createEntity(Entity.ENDER_CRYSTAL, level.getChunk(position.getChunkX(), position.getChunkZ()), nbt);
            if (entity != null) {
                level.getLevel().addEntity(entity);
                entity.spawnToAll();
            }
        });
        return true;
    }
}
