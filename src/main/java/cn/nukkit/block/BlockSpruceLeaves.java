package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceLeaves extends BlockLeaves {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_LEAVES, CommonBlockProperties.PERSISTENT_BIT, CommonBlockProperties.UPDATE_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceLeaves(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getType() {
        return WoodType.SPRUCE;
    }

    @Override
    public Item toSapling() {
        return Item.get(SPRUCE_SAPLING);
    }
}