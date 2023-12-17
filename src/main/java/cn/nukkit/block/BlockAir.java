package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockAir extends BlockTransparent {

    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:air");

    @Override
    public String getName() {
        return "Air";
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canBePlaced() {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }


    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
