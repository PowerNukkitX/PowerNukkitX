package org.powernukkitx.level.generator.object.legacytree;

import org.powernukkitx.block.BlockChorusFlower;
import org.powernukkitx.block.BlockChorusPlant;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

import java.util.ArrayList;
import java.util.List;

import static org.powernukkitx.block.property.CommonBlockProperties.AGE_6;

/**
 * @author GoodLucky777
 */
public class LegacyChorusTree extends LegacyTreeGenerator {

    private static final BlockState STATE_CHORUS_FLOWER_FULLY_AGED = BlockChorusFlower.PROPERTIES.getBlockState(AGE_6.createMax());
    private static final BlockState STATE_CHORUS_PLANT = BlockChorusPlant.PROPERTIES.getDefaultState();

    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        return this.generate(level, rand, position, 8);
    }

    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position, int maxSize) {
        final List<BlockVector3> plants = new ArrayList<>();
        this.placePlant(level, plants, position.getFloorX(), position.getFloorY(), position.getFloorZ());
        this.grow(level, rand, position, maxSize, 0, plants);
        connect(level, plants);
        return true;
    }

    public void growImmediately(BlockManager level, RandomSourceProvider random, Vector3 position, int maxSize, int age) {
        final List<BlockVector3> plants = new ArrayList<>();
        this.grow(level, random, position, maxSize, age, plants);
        connect(level, plants);
    }

    private void placePlant(BlockManager level, List<BlockVector3> plants, int x, int y, int z) {
        level.setBlockStateAt(x, y, z, STATE_CHORUS_PLANT);
        plants.add(new BlockVector3(x, y, z));
    }

    /**
     * Plants are placed without block updates, so they would all keep the unconnected shape. Once
     * the whole tree stands, each one is pointed at the plants, flowers and end stone around it.
     */
    private static void connect(BlockManager level, List<BlockVector3> plants) {
        for (BlockVector3 plant : plants) {
            level.setBlockStateAt(plant, BlockChorusPlant.connectedState(face -> {
                final BlockVector3 side = plant.getSide(face);
                return level.getBlockIdAt(side.x, side.y, side.z);
            }));
        }
    }

    private void grow(BlockManager level, RandomSourceProvider random, Vector3 position, int maxSize, int age, List<BlockVector3> plants) {
        // Random height
        int height = 1 + random.nextInt(4);
        if (age == 0) {
            height++;
        }

        // Grow upward
        for (int y = 1; y <= height; y++) {
            if (!this.isHorizontalAir(level, position.up(y))) {
                return;
            }
            this.placePlant(level, plants, position.getFloorX(), position.getFloorY() + y, position.getFloorZ());
        }

        if (age < 4) {
            // Grow horizontally
            int attempt = random.nextInt(4);
            if (age == 0) {
                attempt++;
            }

            for (int i = 0; i < attempt; i++) {
                BlockFace face = BlockFace.Plane.HORIZONTAL.random(random);
                Vector3 check = position.up(height).getSide(face);
                if (level.getBlockIdAt(check.getFloorX(), check.getFloorY(), check.getFloorZ()) == AIR && level.getBlockIdAt(check.getFloorX(), check.getFloorY() - 1, check.getFloorZ()) == AIR) {
                    if (Math.abs(check.getFloorX() - position.getFloorX()) < maxSize && Math.abs(check.getFloorZ() - position.getFloorZ()) < maxSize && this.isHorizontalAirExcept(level, check, face.getOpposite())) {
                        this.placePlant(level, plants, check.getFloorX(), check.getFloorY(), check.getFloorZ());
                        this.grow(level, random, check, maxSize, age + 1, plants);
                    }
                }
            }
        } else {
            // Death
            level.setBlockStateAt(position.getFloorX(), position.getFloorY() + height, position.getFloorZ(), STATE_CHORUS_FLOWER_FULLY_AGED);
        }
    }

    private boolean isHorizontalAir(BlockManager level, Vector3 vector3) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Vector3 side = vector3.getSide(face);
            if (level.getBlockIdAt(side.getFloorX(), side.getFloorY(), side.getFloorZ()) != AIR) {
                return false;
            }
        }
        return true;
    }

    private boolean isHorizontalAirExcept(BlockManager level, Vector3 vector3, BlockFace except) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (face != except) {
                Vector3 side = vector3.getSide(face);
                if (level.getBlockIdAt(side.getFloorX(), side.getFloorY(), side.getFloorZ()) != AIR) {
                    return false;
                }
            }
        }
        return true;
    }
}
