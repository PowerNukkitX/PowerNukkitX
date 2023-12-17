package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.TerracottaColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/11/24
 */
public class BlockTerracotta extends BlockSolidMeta {
    public BlockTerracotta() {
        this(0);
    }

    public BlockTerracotta(int meta) {
        super(0);
    }

    public BlockTerracotta(TerracottaColor dyeColor) {
        this(dyeColor.getTerracottaData());
    }

    @Override
    public int getId() {
        return TERRACOTTA;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public String getName() {
        return "Terracotta";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.25;
    }

    @Override
    public double getResistance() {
        return 7;
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    public TerracottaColor getDyeColor() {
        return TerracottaColor.getByTerracottaData(getDamage());
    }
}
