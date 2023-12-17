package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/1
 */
public class BlockLeaves2 extends BlockLeaves {


    public static final ArrayBlockProperty<WoodType> NEW_LEAF_TYPE = new ArrayBlockProperty<>("new_leaf_type", true, new WoodType[]{
            WoodType.ACACIA, WoodType.DARK_OAK
    }, 2);


    public static final BlockProperties NEW_LEAF_PROPERTIES = new BlockProperties(NEW_LEAF_TYPE, PERSISTENT, UPDATE);

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
    public static final int ACACIA = 0;

    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
    public static final int DARK_OAK = 1;

    public BlockLeaves2() {
        this(0);
    }

    public BlockLeaves2(int meta) {
        super(meta);
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return NEW_LEAF_PROPERTIES;
    }


    @Override
    public WoodType getType() {
        return getPropertyValue(NEW_LEAF_TYPE);
    }


    @Override
    public void setType(WoodType type) {
        setPropertyValue(NEW_LEAF_TYPE, type);
    }

    @Override
    public int getId() {
        return LEAVES2;
    }

    @Override
    protected boolean canDropApple() {
        return getType() == WoodType.DARK_OAK;
    }

    @Override
    protected Item getSapling() {
        return Item.get(BlockID.SAPLING, getIntValue(NEW_LEAF_TYPE) + 4);
    }
}
