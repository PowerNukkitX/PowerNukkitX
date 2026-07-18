package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityBeacon;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author Angelic47 (Nukkit Project)
 */

public class BlockBeacon extends BlockTransparent implements BlockEntityHolder<BlockEntityBeacon> {
    public static final BlockProperties PROPERTIES = new BlockProperties(BEACON);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(3)
            .resistance(15)
            .toolType(ItemTool.TYPE_PICKAXE)
            .lightEmission(15)
            .canBePushed(false)
            .canBePulled(false)
            .canBeActivated(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBeacon() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBeacon(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityBeacon> getBlockEntityClass() {
        return BlockEntityBeacon.class;
    }

    @Override
    @NotNull
    public String getBlockEntityType() {
        return BlockEntity.BEACON;
    }

    
    @Override
    public double calculateBreakTime(@NotNull Item item) {
        return calculateBreakTime(item, null);
    }

    @Override
    public double calculateBreakTime(@NotNull Item item, @org.jetbrains.annotations.Nullable Player player) {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Beacon";
    }

    
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        BlockEntityBeacon entity = getOrCreateBlockEntity();
        player.addWindow(entity.getInventory());
        return true;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    
    }
