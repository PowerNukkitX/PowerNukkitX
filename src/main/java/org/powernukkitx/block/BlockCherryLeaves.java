package org.powernukkitx.block;

import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.item.Item;
import org.powernukkitx.utils.BlockColor;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.PERSISTENT_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.UPDATE_BIT;


public class BlockCherryLeaves extends BlockLeaves {

    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_LEAVES,PERSISTENT_BIT, UPDATE_BIT);

    public BlockCherryLeaves() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryLeaves(BlockState blockState) {
        super(blockState);
    }


    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Cherry Leaves";
    }

    /*这里写木质类型为BIRCH只是为了获取凋落物时的概率正确，并不代表真的就是白桦木*/

    @Override
    public WoodType getType() {
        return WoodType.CHERRY;
    }

    @Override
    public Item toSapling() {
        return Item.get(CHERRY_SAPLING);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }
}
