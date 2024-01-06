package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author xtypr
 * @since 2015/12/5
 */
public class BlockCraftingTable extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(CRAFTING_TABLE);

    @Override
    @NotNull public BlockProperties getProperties() {
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
    public boolean onActivate(@NotNull Item item, @Nullable Player player) {
        if (player != null) {
            player.craftingType = Player.CRAFTING_BIG;
            player.setCraftingGrid(player.getUIInventory().getBigCraftingGrid());
            ContainerOpenPacket pk = new ContainerOpenPacket();
            pk.windowId = -1;
            pk.type = 1;
            pk.x = (int) x;
            pk.y = (int) y;
            pk.z = (int) z;
            pk.entityId = player.getId();
            player.dataPacket(pk);
        }
        return true;
    }
}
