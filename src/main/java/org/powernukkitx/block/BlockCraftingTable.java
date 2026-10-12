package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.inventory.BlockInventoryHolder;
import org.powernukkitx.inventory.CraftingTableInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author xtypr
 * @since 2015/12/5
 */
public class BlockCraftingTable extends BlockSolid implements BlockInventoryHolder {

    public static final BlockProperties PROPERTIES = new BlockProperties(CRAFTING_TABLE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2.5)
            .resistance(15)
            .toolType(ItemTool.TYPE_AXE)
            .canBeActivated(true)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCraftingTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCraftingTable(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Crafting Table";
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item itemInHand = player.getInventory().getItemInMainHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }
            player.addWindow(getInventory());
        }
        return true;
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new CraftingTableInventory(this);
    }
}
