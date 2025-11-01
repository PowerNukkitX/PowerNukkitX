package cn.nukkit.level.generator.object.structures;

import cn.nukkit.block.BlockSandstone;
import cn.nukkit.block.BlockSandstoneSlab;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.RuledObjectGenerator;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectDesertWell extends RuledObjectGenerator {

    protected static final BlockState SANDSTONE = BlockSandstone.PROPERTIES.getDefaultState();
    protected static final BlockState WATER = BlockWater.PROPERTIES.getDefaultState();
    protected static final BlockState SANDSTONE_SLAB = BlockSandstoneSlab.PROPERTIES.getDefaultState();


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
        random.setSeed(level.getSeed() ^ (x+y+z));

        int biome = level.getBiomeId(x, y, z);
        BiomeDefinition definition = Registries.BIOME.get(biome);
        if (!definition.getTags().contains(BiomeTags.DESERT) || random.nextBoundedInt(500) != 0) {
            return false;
        }

        if (y > 128) {
            return false;
        }

        if (!level.getBlock(x, y, z).is(BlockTags.SAND)) {
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
