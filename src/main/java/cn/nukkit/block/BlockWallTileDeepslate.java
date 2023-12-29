package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author GoodLucky777
 */


public class BlockWallTileDeepslate extends BlockWallBase {


    public BlockWallTileDeepslate() {
        this(0);
    }


    public BlockWallTileDeepslate(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return DEEPSLATE_TILE_WALL;
    }
    
    @Override
    public String getName() {
        return "Deepslate Tile Wall";
    }
    
    @Override
    public double getHardness() {
        return 3.5;
    }
    
    @Override
    public double getResistance() {
        return 6;
    }
    
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

}
