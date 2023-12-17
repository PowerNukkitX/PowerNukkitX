package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author LoboMetalurgico
 * @since 08/06/2021
 */


@PowerNukkitXDifference(info = "not isLavaResistant")
public class BlockCalcite extends BlockSolid {

    public static final BlockProperties PROPERTIES = CommonBlockProperties.EMPTY_PROPERTIES;


    public BlockCalcite() {
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Calcite";
    }

    @Override
    public int getId() {
        return CALCITE;
    }

    @Override
    public double getHardness() {
        return 0.75;
    }

    @Override
    public double getResistance() {
        return 0.75;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
