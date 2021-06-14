package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemRedstone;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "FUTURE", info = "Extends BlockOre instead of BlockSolid only in PowerNukkit")
public class BlockOreRedstone extends BlockOre {

    public BlockOreRedstone() {
        // Does nothing
    }

    @Override
    public int getId() {
        return REDSTONE_ORE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public String getName() {
        return "Redstone Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            int count = new Random().nextInt(2) + 4;

            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                count += new Random().nextInt(fortune.getLevel() + 1);
            }

            return new Item[]{
                    new ItemRedstone(0, count)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_TOUCH) { //type == Level.BLOCK_UPDATE_NORMAL ||
            this.getLevel().setBlock(this, getLitState().getBlock(), false, true);

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nullable
    @Override
    protected MinecraftItemID getRawMaterial() {
        return MinecraftItemID.REDSTONE;
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockState getLitState() {
        return BlockState.of(BlockID.LIT_REDSTONE_ORE);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockState getUnlitState() {
        return BlockState.of(BlockID.REDSTONE_ORE);
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(1, 6);
    }
}
