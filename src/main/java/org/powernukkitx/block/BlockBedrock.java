package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.INFINIBURN_BIT;

/**
 * @author Angelic47 (Nukkit Project)
 * @apiNote Extends BlockSolidMeta instead of BlockSolid only in PowerNukkit
 */

public class BlockBedrock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BEDROCK, INFINIBURN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBedrock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBedrock(BlockState blockstate) {
        super(blockstate);
    }

    public boolean getBurnIndefinitely() {
        return getPropertyValue(INFINIBURN_BIT);
    }

    public void setBurnIndefinitely(boolean infiniburn) {
        setPropertyValue(INFINIBURN_BIT, infiniburn);
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public String getName() {
        return "Bedrock";
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
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
    public boolean canHarvestWithHand() {
        return false;
    }
}
