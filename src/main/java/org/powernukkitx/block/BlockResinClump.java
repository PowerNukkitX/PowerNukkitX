package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockResinClump extends BlockLichen {
    public static final BlockProperties PROPERTIES = new BlockProperties(RESIN_CLUMP, CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);
    public static final BlockDefinition DEFINITION = BlockLichen.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_NONE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockResinClump() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockResinClump(BlockState blockstate) {
        super(blockstate, DEFINITION);
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