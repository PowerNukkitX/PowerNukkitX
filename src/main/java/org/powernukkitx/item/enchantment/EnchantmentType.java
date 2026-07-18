package org.powernukkitx.item.enchantment;

import org.powernukkitx.block.BlockCarvedPumpkin;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemArmor;
import org.powernukkitx.item.ItemEnchantedBook;
import org.powernukkitx.item.ItemFishingRod;
import org.powernukkitx.item.ItemHead;
import org.powernukkitx.item.customitem.CustomItem;

import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public enum EnchantmentType {
    ALL,
    ARMOR,
    ARMOR_HEAD,
    ARMOR_TORSO,
    ARMOR_LEGS,
    ARMOR_FEET,
    SWORD,
    DIGGER,
    FISHING_ROD,
    BREAKABLE,
    BOW,
    WEARABLE,
    TRIDENT,
    CROSSBOW,
    MACE,
    SPEAR;


    public boolean canEnchantItem(@NotNull Item item) {
        if (this == ALL || item instanceof ItemEnchantedBook) {
            return true;
        }
        if (this == BREAKABLE && item.getMaxDurability() >= 0) {
            return true;
        }

        if (item instanceof ItemArmor || (item instanceof CustomItem && item.isArmor())) {
            if (this == WEARABLE || this == ARMOR && item.isArmor()) {
                return true;
            }

            return switch (this) {
                case ARMOR_HEAD -> item.isHelmet();
                case ARMOR_TORSO -> item.isChestplate();
                case ARMOR_LEGS -> item.isLeggings();
                case ARMOR_FEET -> item.isBoots();
                default -> false;
            };
        }

        return switch (this) {
            case SWORD -> item.isSword() && !item.isTrident();
            case DIGGER -> item.isPickaxe() || item.isShovel() || item.isAxe() || item.isHoe();
            case BOW -> item.isBow();
            case FISHING_ROD -> item instanceof ItemFishingRod;
            case WEARABLE -> item instanceof ItemHead || item.getBlock() instanceof BlockCarvedPumpkin;
            case TRIDENT -> item.isTrident();
            case CROSSBOW -> item.isCrossbow();
            case MACE -> item.isMace();
            case SPEAR -> item.isSpear();
            default -> false;
        };
    }
}
