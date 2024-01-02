package cn.nukkit.block;

import cn.nukkit.block.property.enums.NewLeafType;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;

/**
 * @author xtypr
 * @since 2015/12/1
 */
public class BlockLeaves2 extends BlockLeaves {
    public static final BlockProperties PROPERTIES = new BlockProperties(LEAVES2, NEW_LEAF_TYPE, PERSISTENT_BIT, UPDATE_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLeaves2() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLeaves2(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getType() {
        return WoodType.valueOf(getPropertyValue(NEW_LEAF_TYPE).name().toUpperCase());
    }

    @Override
    public void setType(WoodType type) {
        setPropertyValue(NEW_LEAF_TYPE, NewLeafType.valueOf(type.name().toUpperCase()));
    }

    @Override
    protected boolean canDropApple() {
        return getType() == WoodType.DARK_OAK;
    }

    @Override
    protected Item getSapling() {
        return Item.getItemBlock(BlockID.SAPLING, getPropertyValue(NEW_LEAF_TYPE).ordinal() + 4);
    }
}
