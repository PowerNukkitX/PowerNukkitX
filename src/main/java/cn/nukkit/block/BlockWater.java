package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.WaterFrostEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * The alias is Still water.
 */
public class BlockWater extends BlockFlowingWater {

    public static final BlockProperties PROPERTIES = new BlockProperties(WATER, CommonBlockProperties.LIQUID_DEPTH);

    public BlockWater() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWater(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        return this.getLevel().setBlock(this, this, true, false);
    }

    @Override
    public String getName() {
        return "Still Water";
    }

    @Override
    public BlockLiquid getLiquidWithNewDepth(int depth) {
        return new BlockWater(this.blockstate.setPropertyValue(PROPERTIES, CommonBlockProperties.LIQUID_DEPTH.createValue(depth)));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            IChunk chunk = getChunk();
            int x = this.getFloorX();
            int y = this.getFloorY();
            int z = this.getFloorZ();

            if (canFreeze(chunk.getBiomeId(x & 0x0f, y, z & 0x0f))
                    && chunk.getBlockLight(x & 0x0f, y, z & 0x0f) < 10
                    && chunk.getHeightMap(x & 0x0f, z & 0x0f) == y
                    && hasFreezingEdge()) {
                WaterFrostEvent ev = new WaterFrostEvent(this, null);
                level.getServer().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    level.setBlock(this, Block.get(Block.ICE), true, true);
                }
            }
            return Level.BLOCK_UPDATE_RANDOM;
        }
        return super.onUpdate(type);
    }

    private boolean canFreeze(int biomeId) {
        if (biomeId == BiomeID.COLD_OCEAN
                || biomeId == BiomeID.DEEP_COLD_OCEAN
                || biomeId == BiomeID.DEEP_FROZEN_OCEAN) {
            return false;
        }

        BiomeDefinition biome = Registries.BIOME.get(biomeId);
        if (biome == null) {
            return false;
        }

        if (biome.data.temperature <= 0.05f || biome.getTags().contains(BiomeTags.FROZEN)) {
            return isColdEnoughForSnowfall(biome.data.temperature);
        }

        return false;
    }

    private boolean isColdEnoughForSnowfall(float baseTemperature) {
        float adjustedTemperature = baseTemperature;
        int y = this.getFloorY();
        if (y > 64) {
            adjustedTemperature -= (y - 64) / 600f;
        }
        return adjustedTemperature <= 0.15f;
    }

    private boolean hasFreezingEdge() {
        int x = this.getFloorX();
        int y = this.getFloorY();
        int z = this.getFloorZ();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                Block adjacent = level.getBlock(x + dx, y, z + dz);
                if (adjacent instanceof BlockFlowingWater) {
                    continue;
                }
                if (adjacent.isWaterLogged()) {
                    continue;
                }
                return true;
            }
        }

        return false;
    }
}
