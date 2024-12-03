package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockPaleOakLeaves extends BlockLeaves {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_LEAVES, CommonBlockProperties.PERSISTENT_BIT, CommonBlockProperties.UPDATE_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakLeaves(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getType() {
        return WoodType.PALE_OAK;
    }

    @Override
    public Item toSapling() {
        return Item.get(PALE_OAK_SAPLING);
    }
}