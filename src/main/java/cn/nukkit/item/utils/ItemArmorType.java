package cn.nukkit.item.utils;

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
        if (slot == null) return null;
        switch (slot) {
            case ARMOR_HEAD:
            case HEAD:
            case COSMETIC_HEAD:
                return HEAD;
            case ARMOR_TORSO:
            case CHESTPLATE:
                return CHEST;
            case ARMOR_LEGS:
            case LEGGINGS:
                return LEGS;
            case ARMOR_FEET:
            case BOOTS:
                return FEET;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return id;
    }

    public static ItemArmorType get(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim().toLowerCase(java.util.Locale.ROOT);
        switch (t) {
            case "slot.armor.head": case "armor_head": case "head": case "helmet": return HEAD;
            case "slot.armor.chest": case "armor_torso": case "chest": case "torso": case "chestplate": return CHEST;
            case "slot.armor.legs": case "armor_legs": case "legs": case "leggings": return LEGS;
            case "slot.armor.feet": case "armor_feet": case "feet": case "boots": return FEET;
            default:
                try { return ItemArmorType.valueOf(s.toUpperCase(java.util.Locale.ROOT)); }
                catch (IllegalArgumentException ignore) { return null; }
        }
    }
}
