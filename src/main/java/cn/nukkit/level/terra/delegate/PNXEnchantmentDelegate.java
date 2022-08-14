package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.dfsek.terra.api.inventory.ItemStack;
import com.dfsek.terra.api.inventory.item.Enchantment;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public record PNXEnchantmentDelegate(cn.nukkit.item.enchantment.Enchantment innerEnchantment) implements Enchantment {
    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return innerEnchantment.canEnchant(((PNXItemStack) itemStack).innerItem());
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return !innerEnchantment.isCompatibleWith(((PNXEnchantmentDelegate) enchantment).innerEnchantment);
    }

    @Override
    public String getID() {
        return switch (innerEnchantment.getId()) {
            case 0 -> "protection";
            case 1 -> "fire_protection";
            case 2 -> "feather_falling";
            case 3 -> "blast_protection";
            case 4 -> "projectile_protection";
            case 5 -> "thorns";
            case 6 -> "respiration";
            case 7 -> "depth_strider";
            case 8 -> "aqua_affinity";
            case 9 -> "sharpness";
            case 10 -> "smite";
            case 11 -> "bane_of_arthropods";
            case 12 -> "knockback";
            case 13 -> "fire_aspect";
            case 14 -> "looting";
            case 15 -> "efficiency";
            case 16 -> "silk_touch";
            case 17 -> "unbreaking";
            case 18 -> "fortune";
            case 19 -> "power";
            case 20 -> "punch";
            case 21 -> "flame";
            case 22 -> "infinity";
            case 23 -> "luck_of_the_sea";
            case 24 -> "lure";
            case 25 -> "frost_walker";
            case 26 -> "mending";
            case 27 -> "binding_curse";
            case 28 -> "vanishing_curse";
            case 29 -> "impaling";
            case 30 -> "riptide";
            case 31 -> "loyalty";
            case 32 -> "channeling";
            case 33 -> "multishot";
            case 34 -> "piercing";
            case 35 -> "quick_charge";
            case 36 -> "soul_speed";
            default -> throw new IllegalStateException("Unexpected enchantment id: " + innerEnchantment.getId());
        };
    }

    @Override
    public int getMaxLevel() {
        return innerEnchantment.getMaxLevel();
    }

    @Override
    public cn.nukkit.item.enchantment.Enchantment getHandle() {
        return innerEnchantment;
    }
}
