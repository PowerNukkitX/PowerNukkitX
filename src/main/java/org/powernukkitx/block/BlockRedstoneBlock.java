package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockRedstoneBlock extends BlockSolid implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(REDSTONE_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(5)
            .resistance(10)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .isPowerSource(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedstoneBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedstoneBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Redstone Block";
    }

    
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (super.place(item, block, target, face, fx, fy, fz, player)) {
            updateAroundRedstone();

            return true;
        }
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        if (!super.onBreak(item)) {
            return false;
        }

        updateAroundRedstone();
        return true;
    }

    
    @Override
    public int getWeakPower(BlockFace face) {
        return 15;
    }

    }