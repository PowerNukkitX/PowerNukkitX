package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockRedMushroom;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;

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
    public int getBase() {
        return -7;
    }

    @Override
    public int getRandom() {
        return 8;
    }

    @Override
    public boolean isSupportValid(Block block) {
        return super.isSupportValid(block) && block.getLevel().getHeightMap(block.getFloorX(), block.getFloorZ()) != block.getFloorY();
    }

    @Override
    public int getY(IChunk chunk, int x, int z) {
        int startY = super.getY(chunk, x, z);
        for(int y = startY; y > chunk.getLevel().getMinHeight(); y--) {
            Block block = chunk.getBlockState(x, y, z).toBlock(new Position(x, y, z, chunk.getLevel()));
            if(block.up().isAir()) {
                if(isSupportValid(block)) return y;
            }
        }
        return startY;
    }
}
