package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockDeny extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(DENY);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(-1)
            .resistance(18000000)
            .canBePushed(false)
            .canBePulled(false)
            .canHarvestWithHand(false)
            .build();

    public BlockDeny() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockDeny(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deny";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, BlockFace face, Item item, @Nullable Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }
        return super.isBreakable(vector, layer, face, item, player);
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
}
