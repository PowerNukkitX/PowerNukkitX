package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockSlabWood extends BlockSlab {


    public static final BlockProperties PROPERTIES = new BlockProperties(
            WoodType.PROPERTY,
            CommonBlockProperties.VERTICAL_HALF
    );

    public BlockSlabWood() {
        this(0);
    }

    public BlockSlabWood(int meta) {
        super(meta, DOUBLE_WOODEN_SLAB);
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return (isOnTop()? "Upper " : "") + getSlabName() + " Wood Slab";
    }


    @Override
    public String getSlabName() {
        return getWoodType().getEnglishName();
    }

    @Override
    public int getId() {
        return WOOD_SLAB;
    }


    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId() && slab.getPropertyValue(WoodType.PROPERTY).equals(getWoodType());
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }


    public WoodType getWoodType() {
        return getPropertyValue(WoodType.PROPERTY);
    }


    public void setWoodType(WoodType type) {
        setPropertyValue(WoodType.PROPERTY, type);
    }

}
