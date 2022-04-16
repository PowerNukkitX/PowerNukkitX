package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockWallTileDeepslate extends BlockWallBase {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockWallTileDeepslate() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockWallTileDeepslate(int meta) {
        super(meta);
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

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }
}
