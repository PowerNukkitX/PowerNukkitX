package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.BlockInventoryHolder;
import cn.nukkit.inventory.CartographyTableInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;


public class BlockCartographyTable extends BlockSolid implements BlockInventoryHolder {
    public static final BlockProperties PROPERTIES = new BlockProperties(CARTOGRAPHY_TABLE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCartographyTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCartographyTable(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cartography Table";
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        player.addWindow(getOrCreateInventory());
        return true;
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new CartographyTableInventory(this);
    }
}
