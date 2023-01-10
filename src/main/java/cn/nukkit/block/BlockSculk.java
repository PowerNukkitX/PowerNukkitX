package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Since("1.6.0.0-PNX")
@PowerNukkitOnly
public class BlockSculk extends BlockSolid {
    public BlockSculk() {
    }

    @Override
    public String getName() {
        return "Sculk";
    }

    @Override
    public int getId() {
        return SCULK;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @NotNull
    @Override
    public double calculateBreakTime(@NotNull Item item, @Nullable Player player) {
        if (canHarvest(item)) {
            return super.calculateBreakTime(item, player);
        } else {
            return 1;
        }
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }
}
