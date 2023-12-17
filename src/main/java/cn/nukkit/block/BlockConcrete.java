package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockConcrete extends BlockSolidMeta {


    public static final BlockProperties PROPERTIES = CommonBlockProperties.COLOR_BLOCK_PROPERTIES;

    public BlockConcrete() {
        this(0);
    }

    public BlockConcrete(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CONCRETE;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getResistance() {
        return 9;
    }

    @Override
    public double getHardness() {
        return 1.8;
    }

    @Override
    public String getName() {
        return getDyeColor().getName() + " Concrete";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    public DyeColor getDyeColor() {
        return getPropertyValue(CommonBlockProperties.COLOR);
    }
}
