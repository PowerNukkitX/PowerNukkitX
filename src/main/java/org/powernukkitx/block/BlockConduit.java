package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityConduit;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;


public class BlockConduit extends BlockTransparent implements BlockEntityHolder<BlockEntityConduit> {
    public static final BlockProperties PROPERTIES = new BlockProperties(CONDUIT);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(3)
            .resistance(15)
            .toolType(ItemTool.TYPE_PICKAXE)
            .lightEmission(15)
            .build();

    public BlockConduit() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockConduit(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Conduit";
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityConduit> getBlockEntityClass() {
        return BlockEntityConduit.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.CONDUIT;
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (item.isBlock() && Objects.equals(item.getBlockId(), CONDUIT) && target.getId().equals(CONDUIT)) {
            return false;
        }

        BlockEntityConduit conduit = BlockEntityHolder.setBlockAndCreateEntity(this, false, true,
                new CompoundTag().putBoolean("IsMovable", true));
        if (conduit != null) {
            conduit.scheduleUpdate();
            return true;
        }

        return false;
    }

    @Override
    public double getMinX() {
        return x + (5.0 / 16);
    }

    @Override
    public double getMinY() {
        return y + (5.0 / 16);
    }

    @Override
    public double getMinZ() {
        return z + (5.0 / 16);
    }

    @Override
    public double getMaxX() {
        return x + (11.0 / 16);
    }

    @Override
    public double getMaxY() {
        return y + (11.0 / 16);
    }

    @Override
    public double getMaxZ() {
        return z + (11.0 / 16);
    }
}
