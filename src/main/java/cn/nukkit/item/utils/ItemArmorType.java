package cn.nukkit.item.utils;

import java.util.Locale;

public enum ItemArmorType {
    HEAD("slot.armor.head"),
    CHEST("slot.armor.chest"),
    LEGS("slot.armor.legs"),
    FEET("slot.armor.feet");

    private final String id;

    ItemArmorType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static ItemArmorType fromId(String id) {
        if (id == null) return null;
        for (ItemArmorType t : values()) {
            if (t.id.equals(id)) return t;
        }
        return null;
    }

    public static ItemArmorType fromEnchantSlot(ItemEnchantSlot slot) {
        if (slot == null || slot == ItemEnchantSlot.NONE) return null;
        return switch (slot) {
            case ARMOR_HEAD, HEAD, COSMETIC_HEAD -> HEAD;
            case ARMOR_TORSO, CHESTPLATE         -> CHEST;
            case ARMOR_LEGS,  LEGGINGS           -> LEGS;
            case ARMOR_FEET,  BOOTS              -> FEET;
            default                              -> null;
        };
    }

    @Override
    public String toString() {
        return id;
    }

    public static ItemArmorType get(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim().toLowerCase(Locale.ROOT);
        return switch (t) {
            case "slot.armor.head", "armor_head", "head", "helmet"                  -> HEAD;
            case "slot.armor.chest", "armor_torso", "chest", "torso", "chestplate"  -> CHEST;
            case "slot.armor.legs", "armor_legs", "legs", "leggings"                -> LEGS;
            case "slot.armor.feet", "armor_feet", "feet", "boots"                   -> FEET;
            default ->  {
            try {
                    yield ItemArmorType.valueOf(s.trim().toUpperCase(java.util.Locale.ROOT));
                } catch (IllegalArgumentException ignore) {
                    yield null;
                }
            }
        };
    }
}