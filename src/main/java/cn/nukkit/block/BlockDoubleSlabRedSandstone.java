package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.StoneSlab2Type;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */

public class BlockDoubleSlabRedSandstone extends BlockDoubleSlabBase {

    public BlockDoubleSlabRedSandstone() {
        this(0);
    }

    public BlockDoubleSlabRedSandstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return DOUBLE_RED_SANDSTONE_SLAB;
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return BlockSlabRedSandstone.PROPERTIES;
    }

}
