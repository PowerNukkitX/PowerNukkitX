package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRedMushroom;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Position;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.registry.Registries;

public class ScatterRedMushroomFeature extends GroupedDiscFeature {

    private static final BlockState STATE = BlockRedMushroom.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_red_mushroom_feature";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public BlockState getSourceBlock() {
        return STATE;
    }

    @Override
    public int getMinRadius() {
        return 1;
    }

    @Override
    public int getMaxRadius() {
        return 2;
    }

    @Override
    public double getProbability() {
        return 0.1f;
    }

    @Override
    public int getBase() {
        return -7;
    }

    @Override
    public int getRandom() {
        return 8;
    }

    @Override
    public boolean isSupportValid(Block block) {
        return block.isSolid() &&
                (block.getLevel().getHeightMap(block.getFloorX(), block.getFloorZ()) != block.getFloorY()
                        || block.getLevel().getBiomeId(block.getFloorX(), block.getFloorY(), block.getFloorZ()) == BiomeID.MUSHROOM_ISLAND);
    }

    @Override
    public int getY(IChunk chunk, int x, int z) {
        int startY = super.getY(chunk, x, z);
        for(int y = startY; y > chunk.getLevel().getMinHeight(); y--) {
            Block block = Registries.BLOCK.get(chunk.getBlockState(x, y, z), (chunk.getX() << 4) + x, y, (chunk.getZ() << 4) + z, 0, chunk.getLevel());
            if(block.up().isAir()) {
                if(isSupportValid(block)) return y;
            }
        }
        return startY;
    }
}
