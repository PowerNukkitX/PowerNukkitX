package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.WoodType;
import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockJungleLeaves extends BlockLeaves {
     public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_LEAVES, CommonBlockProperties.PERSISTENT_BIT, CommonBlockProperties.UPDATE_BIT);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockJungleLeaves(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public WoodType getType() {
        return WoodType.JUNGLE;
    }

    @Override
    public Item toSapling() {
        return Item.get(JUNGLE_SAPLING);
    }
}