package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockOakLeaves extends BlockLeaves {
     public static final BlockProperties PROPERTIES = new BlockProperties(OAK_LEAVES, CommonBlockProperties.PERSISTENT_BIT, CommonBlockProperties.UPDATE_BIT);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockOakLeaves(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getType() {
        return WoodType.OAK;
    }

    @Override
    public Item toSapling() {
        return Item.get(OAK_SAPLING);
    }
}