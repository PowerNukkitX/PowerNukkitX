package cn.nukkit.item.enchantment.utils;

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

    HEAD("armor_head"),
    CHESTPLATE("armor_torso"),
    LEGGINGS("armor_legs"),
    BOOTS("armor_feet"),
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
        if (id == null) return null;
        for (ItemEnchantSlot s : values()) {
            if (s.id.equals(id)) return s;
        }
        return null;
    }
}
