package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.BlockBeeNest;
import org.powernukkitx.block.BlockBeehive;
import org.powernukkitx.block.BlockFlower;
import org.powernukkitx.block.BlockFloweringAzalea;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntityBeehive;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

public final class BeeNestGenerator {
    private BeeNestGenerator() {
    }

    public static boolean place(BlockManager level, RandomSourceProvider random, Vector3 treePosition) {
        return place(level, treePosition, random.nextInt(2, 4));
    }

    public static boolean place(BlockManager level, Vector3 treePosition, int beeCount) {
        int x = treePosition.getFloorX();
        int minY = treePosition.getFloorY();
        int z = treePosition.getFloorZ();
        for (int leafY = minY + 1; leafY <= minY + 32; leafY++) {
            BlockVector3 nestPosition = new BlockVector3(x, leafY - 1, z + 1);
            if (level.getBlockIfCachedOrLoaded(x, leafY - 1, z).isAir()
                    || !level.getBlockIfCachedOrLoaded(nestPosition.asVector3()).isAir()
                    || level.getBlockIfCachedOrLoaded(nestPosition.up().asVector3()).isAir()) {
                continue;
            }
            placeAt(level, nestPosition, beeCount);
            return true;
        }
        return false;
    }

    public static void placeAt(BlockManager level, BlockVector3 nestPosition, int beeCount) {
        level.setBlockStateAt(nestPosition, BlockBeeNest.PROPERTIES.getBlockState(
                CommonBlockProperties.DIRECTION.createValue(BlockFace.SOUTH.getHorizontalIndex()),
                CommonBlockProperties.HONEY_LEVEL.createValue(0)
        ));
        level.addHook(() -> populate(level, nestPosition, beeCount));
    }

    public static boolean hasNearbyFlower(BlockManager manager, Vector3 position) {
        int centerX = position.getFloorX();
        int y = position.getFloorY();
        int centerZ = position.getFloorZ();
        for (int x = centerX - 2; x <= centerX + 2; x++) {
            for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                if (x == centerX && z == centerZ) {
                    continue;
                }
                if (manager.getBlockIfCachedOrLoaded(x, y, z) instanceof BlockFlower
                        || manager.getBlockIfCachedOrLoaded(x, y, z) instanceof BlockFloweringAzalea) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void populate(BlockManager manager, BlockVector3 position, int beeCount) {
        if (!(manager.getLevel().getBlock(position.x, position.y, position.z) instanceof BlockBeehive hive)) {
            return;
        }
        BlockEntityBeehive blockEntity = hive.getOrCreateBlockEntity();
        if (blockEntity == null) {
            return;
        }

        for (int i = 0; i < beeCount; i++) {
            Entity bee = Entity.createEntity(EntityID.BEE,
                    manager.getLevel().getChunk(position.x >> 4, position.z >> 4),
                    Entity.getDefaultNBT(new Vector3(position.x + 0.5, position.y, position.z + 0.5)));
            if (bee != null) {
                blockEntity.addOccupant(bee, 600, false, false);
            }
        }
        blockEntity.saveNBT();
    }
}
