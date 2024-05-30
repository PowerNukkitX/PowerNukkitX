package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.BlockInventoryHolder;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SmithingInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;


public class BlockSmithingTable extends BlockSolid implements BlockInventoryHolder {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMITHING_TABLE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmithingTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmithingTable(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Smithing Table";
    }

    @Override
    public boolean canBeActivated() {
        return true;
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

        player.addWindow(getOrCreateInventory());
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new SmithingInventory(this);
    }
}
