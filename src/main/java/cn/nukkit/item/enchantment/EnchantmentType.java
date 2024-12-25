package cn.nukkit.item.enchantment;

import cn.nukkit.block.BlockCarvedPumpkin;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.ItemCrossbow;
import cn.nukkit.item.ItemFishingRod;
import cn.nukkit.item.ItemHead;
import cn.nukkit.item.ItemMace;
import cn.nukkit.item.ItemTrident;
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
    MACE;


    public boolean canEnchantItem(@NotNull Item item) {
        if (this == ALL) {
            return true;

        }
        if (this == BREAKABLE && item.getMaxDurability() >= 0) {
            return true;
        }

        if (item instanceof ItemArmor) {
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
            case SWORD -> item.isSword() && !(item instanceof ItemTrident);
            case DIGGER -> item.isPickaxe() || item.isShovel() || item.isAxe() || item.isHoe();
            case BOW -> item instanceof ItemBow;
            case FISHING_ROD -> item instanceof ItemFishingRod;
            case WEARABLE -> item instanceof ItemHead || item.getBlock() instanceof BlockCarvedPumpkin;
            case TRIDENT -> item instanceof ItemTrident;
            case CROSSBOW -> item instanceof ItemCrossbow;
            case MACE -> item instanceof ItemMace;
            default -> false;
        };
    }
}
