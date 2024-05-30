package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.BlockInventoryHolder;
import cn.nukkit.inventory.CraftingTableInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author xtypr
 * @since 2015/12/5
 */
public class BlockCraftingTable extends BlockSolid implements BlockInventoryHolder {

    public static final BlockProperties PROPERTIES = new BlockProperties(CRAFTING_TABLE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCraftingTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCraftingTable(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Crafting Table";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null) {
            Item itemInHand = player.getInventory().getItemInHand();
            if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
                return false;
            }
            player.addWindow(getOrCreateInventory());
        }
        return true;
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new CraftingTableInventory(this);
    }
}
