package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.inventory.BlockInventoryHolder;
import org.powernukkitx.inventory.CartographyTableInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;


public class BlockCartographyTable extends BlockSolid implements BlockInventoryHolder {
    public static final BlockProperties PROPERTIES = new BlockProperties(CARTOGRAPHY_TABLE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2.5)
            .resistance(12.5)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(5)
            .canBeActivated(true)
            .canHarvestWithHand(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCartographyTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCartographyTable(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Cartography Table";
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        player.addWindow(getInventory());
        return true;
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new CartographyTableInventory(this);
    }
}
