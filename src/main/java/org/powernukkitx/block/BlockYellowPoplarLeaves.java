package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.item.Item;
import org.powernukkitx.utils.BlockColor;
import org.jetbrains.annotations.NotNull;

public class BlockYellowPoplarLeaves extends BlockLeaves {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_POPLAR_LEAVES, CommonBlockProperties.PERSISTENT_BIT, CommonBlockProperties.UPDATE_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowPoplarLeaves(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getType() {
        return WoodType.POPLAR;
    }

    @Override
    public String getName() {
        return "Yellow Poplar Leaves";
    }

    @Override
    public Item toSapling() {
        return Item.get(POPLAR_SAPLING);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
