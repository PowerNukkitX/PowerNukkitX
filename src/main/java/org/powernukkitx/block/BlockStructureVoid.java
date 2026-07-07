package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author good777LUCKY
 */
public class BlockStructureVoid extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(STRUCTURE_VOID);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStructureVoid() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStructureVoid(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Structure Void";
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
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }
}
