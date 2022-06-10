package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSculkShrieker extends BlockSolid{

    public static final BooleanBlockProperty ACTIVE = new BooleanBlockProperty("active",false);
    public static final BooleanBlockProperty CAN_SUMMON = new BooleanBlockProperty("can_summon",false);
    public static final BlockProperties PROPERTIES = new BlockProperties(ACTIVE,CAN_SUMMON);

    @Override
    public String getName() {
        return "Sculk Shrieker";
    }

    @Override
    public int getId() {
        return SCULK_SHRIEKER;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
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
