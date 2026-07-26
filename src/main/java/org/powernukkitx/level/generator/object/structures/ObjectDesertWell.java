package org.powernukkitx.level.generator.object.structures;

import org.powernukkitx.block.BlockSandstone;
import org.powernukkitx.block.BlockSandstoneSlab;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.level.generator.object.RuledObjectGenerator;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.tags.BlockTags;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

public class ObjectDesertWell extends ObjectGenerator implements RuledObjectGenerator {

    protected static final BlockState SANDSTONE = BlockSandstone.PROPERTIES.getDefaultState();
    protected static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();
    protected static final BlockState SANDSTONE_SLAB = BlockSandstoneSlab.PROPERTIES.getDefaultState();
    protected final Xoroshiro128 random = new Xoroshiro128();


    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {
        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();
        for (int dy = -1; dy <= 0; ++dy) {
            for (int dx = -2; dx <= 2; ++dx) {
                for (int dz = -2; dz <= 2; ++dz) {
                    level.setBlockStateAt(x + dx, y + dy, z + dz, SANDSTONE);
                }
            }
        }
        level.setBlockStateAt(x, y, z, WATER);
        level.setBlockStateAt(x - 1, y, z, WATER);
        level.setBlockStateAt(x + 1, y, z, WATER);
        level.setBlockStateAt(x, y, z - 1, WATER);
        level.setBlockStateAt(x, y, z + 1, WATER);
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                if (dx == -2 || dx == 2 || dz == -2 || dz == 2) {
                    level.setBlockStateAt(x + dx, y + 1, z + dz, SANDSTONE);
                }
            }
        }
        level.setBlockStateAt(x + 2, y + 1, z, SANDSTONE_SLAB);
        level.setBlockStateAt(x - 2, y + 1, z, SANDSTONE_SLAB);
        level.setBlockStateAt(x, y + 1, z + 2, SANDSTONE_SLAB);
        level.setBlockStateAt(x, y + 1, z - 2, SANDSTONE_SLAB);
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (dx == 0 && dz == 0) {
                    level.setBlockStateAt(x + dx, y + 4, z + dz, SANDSTONE);
                } else {
                    level.setBlockStateAt(x + dx, y + 4, z + dz, SANDSTONE_SLAB);
                }
            }
        }
        for (int dy = 1; dy <= 3; ++dy) {
            level.setBlockStateAt(x - 1, y + dy, z - 1, SANDSTONE);
            level.setBlockStateAt(x - 1, y + dy, z + 1, SANDSTONE);
            level.setBlockStateAt(x + 1, y + dy, z - 1, SANDSTONE);
            level.setBlockStateAt(x + 1, y + dy, z + 1, SANDSTONE);
        }
        return true;
    }

    @Override
    public String getName() {
        return "desert_well";
    }

    @Override
    public boolean canGenerateAt(Location location) {
        int x = location.getFloorX();
        int y = location.getFloorY();
        int z = location.getFloorZ();
        Level level = location.getLevel();
        random.setSeed(level.getSeed() ^ (x + y + z));

        int biome = level.getBiomeId(x, y, z);
        BiomeDefinitionData definition = Registries.BIOME.get(biome).second();
        if (!Registries.BIOME.containsTag(BiomeTags.DESERT, definition) || random.nextBoundedInt(500) != 0) {
            return false;
        }

        if (y > 128) {
            return false;
        }

        if (!level.getBlock(x, y, z).hasTag(BlockTags.SAND)) {
            return false;
        }

        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                if (level.getBlock(x + dx, y - 1, z + dz).isAir() && level.getBlock(x + dx, y - 2, z + dz).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }
}
