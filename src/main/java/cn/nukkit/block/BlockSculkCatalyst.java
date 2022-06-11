package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.ItemTool;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockSculkCatalyst extends BlockSolid {

    public static final BooleanBlockProperty BLOOM = new BooleanBlockProperty("bloom",false);
    public static final BlockProperties PROPERTIES = new BlockProperties(BLOOM);

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Sculk Catalyst";
    }

    @Override
    public int getId() {
        return SCULK_CATALYST;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public int getLightLevel() {
        return 6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 3.0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }
}
