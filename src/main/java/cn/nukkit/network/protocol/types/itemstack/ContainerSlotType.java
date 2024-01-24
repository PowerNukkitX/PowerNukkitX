package cn.nukkit.network.protocol.types.itemstack;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

public enum ContainerSlotType {
    ANVIL_INPUT(0),
    ANVIL_MATERIAL(1),
    ANVIL_RESULT(2),
    SMITHING_TABLE_INPUT(3),
    SMITHING_TABLE_MATERIAL(4),
    SMITHING_TABLE_RESULT(5),
    ARMOR(6),
    LEVEL_ENTITY(7),
    BEACON_PAYMENT(8),
    BREWING_INPUT(9),
    BREWING_RESULT(10),
    BREWING_FUEL(11),
    HOTBAR_AND_INVENTORY(12),
    CRAFTING_INPUT(13),
    CRAFTING_OUTPUT(14),
    RECIPE_CONSTRUCTION(15),
    RECIPE_NATURE(16),
    RECIPE_ITEMS(17),
    RECIPE_SEARCH(18),
    RECIPE_SEARCH_BAR(19),
    RECIPE_EQUIPMENT(20),
    RECIPE_BOOK(21),
    ENCHANTING_INPUT(22),
    ENCHANTING_MATERIAL(23),
    FURNACE_FUEL(24),
    FURNACE_INGREDIENT(25),
    FURNACE_RESULT(26),
    HORSE_EQUIP(27),
    HOTBAR(28),
    INVENTORY(29),
    SHULKER_BOX(30),
    TRADE_INGREDIENT_1(31),
    TRADE_INGREDIENT_2(32),
    TRADE_RESULT(33),
    OFFHAND(34),
    COMPOUND_CREATOR_INPUT(35),
    COMPOUND_CREATOR_OUTPUT(36),
    ELEMENT_CONSTRUCTOR_OUTPUT(37),
    MATERIAL_REDUCER_INPUT(38),
    MATERIAL_REDUCER_OUTPUT(39),
    LAB_TABLE_INPUT(40),
    LOOM_INPUT(41),
    LOOM_DYE(42),
    LOOM_MATERIAL(43),
    LOOM_RESULT(44),
    BLAST_FURNACE_INGREDIENT(45),
    SMOKER_INGREDIENT(46),
    TRADE2_INGREDIENT_1(47),
    TRADE2_INGREDIENT_2(48),
    TRADE2_RESULT(49),
    GRINDSTONE_INPUT(50),
    GRINDSTONE_ADDITIONAL(51),
    GRINDSTONE_RESULT(52),
    STONECUTTER_INPUT(53),
    STONECUTTER_RESULT(54),
    CARTOGRAPHY_INPUT(55),
    CARTOGRAPHY_ADDITIONAL(56),
    CARTOGRAPHY_RESULT(57),
    BARREL(58),
    CURSOR(59),
    CREATED_OUTPUT(60),
    SMITHING_TABLE_TEMPLATE(61),
    CRAFTER_BLOCK_CONTAINER(62);
    private final int id;
    private static final Int2ObjectArrayMap<ContainerSlotType> VALUES = new Int2ObjectArrayMap<>();

    static {
        for (var v : values()) {
            VALUES.put(v.getId(), v);
        }
    }

    ContainerSlotType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public static ContainerSlotType fromId(int id) {
        return VALUES.get(id);
    }
}
