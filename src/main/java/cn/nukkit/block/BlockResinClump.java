package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockResinClump extends BlockLichen {
    public static final BlockProperties PROPERTIES = new BlockProperties(RESIN_CLUMP, CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockResinClump() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockResinClump(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    @Override
    public Item[] getDrops(Item item) {
        Item drop = toItem();
        drop.setCount(getGrowthSides().length);
        return new Item[]{drop};
    }

    @Override
    public void witherAtSide(BlockFace side) {
        if (isGrowthToSide(side)) {
            setPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS, getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS) ^ (0b000001 << side.getDUSWNEIndex()));
            getLevel().setBlock(this, this, true, true);
            this.level.dropItem(this, toItem());
        }
    }
}