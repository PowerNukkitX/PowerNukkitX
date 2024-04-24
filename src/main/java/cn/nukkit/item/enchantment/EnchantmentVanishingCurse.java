package cn.nukkit.item.enchantment;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class EnchantmentVanishingCurse extends Enchantment {
    protected EnchantmentVanishingCurse() {
        super(ID_VANISHING_CURSE, "curse.vanishing", Rarity.VERY_RARE, EnchantmentType.BREAKABLE);
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
