package org.powernukkitx.item.enchantment;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;

public class EnchantmentVanishingCurse extends Enchantment {
    protected EnchantmentVanishingCurse() {
        super(ID_VANISHING_CURSE, "curse.vanishing", Rarity.VERY_RARE, EnchantmentType.BREAKABLE);

        this.setObtainableFromEnchantingTable(false);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 25;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }

    @Override
    public boolean canEnchant(Item item) {
        return switch (item.getId()) {
            case BlockID.SKULL, ItemID.COMPASS -> true;
            default -> {
                if (item.isBlock() && item.getBlock().getId().equals(BlockID.CARVED_PUMPKIN)) {
                    yield true;
                }
                yield super.canEnchant(item);
            }
        };
    }
}
