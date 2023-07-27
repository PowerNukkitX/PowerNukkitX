package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.player.Player;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
public class BlockCartographyTable extends BlockSolid {

    @PowerNukkitOnly
    public BlockCartographyTable() {}

    @Override
    public int getId() {
        return CARTOGRAPHY_TABLE;
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
    public boolean onActivate(@NotNull Item item, @Nullable Player player) {
        // if (player != null) {
        //    player.craftingType = Player.CRAFTING_CARTOGRAPHY;
        // }
        return false;
    }
}
