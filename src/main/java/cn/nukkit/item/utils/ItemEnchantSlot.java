package cn.nukkit.item.utils;

import java.util.Locale;

public enum ItemEnchantSlot {
    NONE("none"),
    ALL("all"),
    SWORD("sword"),
    SPEAR("spear"),
    BOW("bow"),
    CROSSBOW("crossbow"),
    G_ARMOR("g_armor"),
    ARMOR_HEAD("armor_head"),
    COSMETIC_HEAD("cosmetic_head"),
    ARMOR_TORSO("armor_torso"),
    ARMOR_LEGS("armor_legs"),
    ARMOR_FEET("armor_feet"),
    ELYTRA("elytra"),
    SHIELD("shield"),
    G_TOOL("g_tool"),
    G_DIGGING("g_digging"),
    PICKAXE("pickaxe"),
    SHOVEL("shovel"),
    AXE("axe"),
    HOE("hoe"),
    SHEARS("shears"),
    FLINTSTEEL("flintsteel"),
    FISHING_ROD("fishing_rod"),
    CARROT_STICK("carrot_stick"),

    // aliases for readability
    HEAD("armor_head"),
    HELMET("armor_head"),
    CHESTPLATE("armor_torso"),
    CHEST("armor_torso"),
    LEGGINGS("armor_legs"),
    LEGS("armor_legs"),
    BOOTS("armor_feet"),
    FEET("armor_feet"),
    TRIDENT("spear"),
    FLINT_AND_STEEL("flintsteel"),
    CARROT_ON_A_STICK("carrot_stick");

    private final String id;

    ItemEnchantSlot(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static ItemEnchantSlot fromId(String id) {
        if (id == null || id.isBlank()) return NONE;
        String key = id.trim().toLowerCase(Locale.ROOT);
        for (ItemEnchantSlot s : values()) {
            if (s.id.equals(key)) return s;
        }
        return NONE;
    }
}
