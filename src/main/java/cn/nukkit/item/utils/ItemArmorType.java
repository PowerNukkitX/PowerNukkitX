package cn.nukkit.item.utils;


public enum ItemArmorType {
    NONE(""),
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
        if (id == null || id.isBlank()) return NONE;
        for (ItemArmorType t : values()) {
            if (t.id.equals(id)) return t;
        }
        return NONE;
    }

    public static ItemArmorType fromEnchantSlot(ItemEnchantSlot slot) {
        if (slot == null || slot == ItemEnchantSlot.NONE) return NONE;
        return switch (slot) {
            case ARMOR_HEAD, HEAD, COSMETIC_HEAD -> HEAD;
            case ARMOR_TORSO, CHESTPLATE         -> CHEST;
            case ARMOR_LEGS,  LEGGINGS           -> LEGS;
            case ARMOR_FEET,  BOOTS              -> FEET;
            default                              -> NONE;
        };
    }

    @Override
    public String toString() {
        return id;
    }

    public static ItemArmorType get(String s) {
        if (s == null || s.isBlank()) return NONE;
        String t = s.trim().toLowerCase(java.util.Locale.ROOT);
        return switch (t) {
            case "slot.armor.head", "armor_head", "head", "helmet"                  -> HEAD;
            case "slot.armor.chest", "armor_torso", "chest", "torso", "chestplate"  -> CHEST;
            case "slot.armor.legs", "armor_legs", "legs", "leggings"                -> LEGS;
            case "slot.armor.feet", "armor_feet", "feet", "boots"                   -> FEET;
            default -> {
                try { yield ItemArmorType.valueOf(s.trim().toUpperCase(java.util.Locale.ROOT)); }
                catch (IllegalArgumentException ignore) { yield NONE; }
            }
        };
    }
}