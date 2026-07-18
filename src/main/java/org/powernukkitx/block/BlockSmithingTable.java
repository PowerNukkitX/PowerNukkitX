package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.inventory.BlockInventoryHolder;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.SmithingInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;


public class BlockSmithingTable extends BlockSolid implements BlockInventoryHolder {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMITHING_TABLE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2.5)
            .resistance(12.5)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(5)
            .canBeActivated(true)
            .canHarvestWithHand(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmithingTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmithingTable(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Smithing Table";
    }

    
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @org.jetbrains.annotations.Nullable Player player) {
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player == null) {
            return false;
        }

        player.addWindow(getInventory());
        return true;
    }

    
    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new SmithingInventory(this);
    }
}
