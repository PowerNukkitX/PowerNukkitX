package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */


public abstract class BlockRaw extends BlockSolidMeta {


    public static final BlockProperties PROPERTIES = CommonBlockProperties.EMPTY_PROPERTIES;


    public BlockRaw() {
        this(0);
    }


    public BlockRaw(int meta) {
        super(meta);
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
