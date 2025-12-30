package cn.nukkit.inventory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InventoryType {
    NONE(-9),
    INVENTORY(-1),
    CONTAINER(0),
    WORKBENCH(1),
    FURNACE(2),
    ENCHANTMENT(3),
    BREWING_STAND(4),
    ANVIL(5),
    DISPENSER(6),
    DROPPER(7),
    HOPPER(8),
    CAULDRON(9),
    MINECART_CHEST(10, CONTAINER),
    MINECART_HOPPER(11, HOPPER),
    HORSE(12),
    BEACON(13),
    STRUCTURE_EDITOR(14),
    TRADE(15),
    COMMAND_BLOCK(16),
    JUKEBOX(17),
    ARMOR(18),
    HAND(19),
    COMPOUND_CREATOR(20),
    ELEMENT_CONSTRUCTOR(21),
    MATERIAL_REDUCER(22),
    LAB_TABLE(23),
    LOOM(24),
    LECTERN(25),
    GRINDSTONE(26),
    BLAST_FURNACE(27),
    SMOKER(28),
    STONECUTTER(29),
    CARTOGRAPHY(30),
    HUD(31),
    JIGSAW_EDITOR(32),
    SMITHING_TABLE(33),
    CHEST_BOAT(34, CONTAINER),
    /**
     * @since v630
     */
    DECORATED_POT(35),
    /**
     * @since v630
     */
    CRAFTER(36);

    public static final InventoryType[] VALUES;

    static {
        InventoryType[] types = values();
        int arrayLength = types[types.length - 1].id + 9 + 1;
        VALUES = new InventoryType[arrayLength];

        for (InventoryType type : types) {
            VALUES[type.id + 9] = type;
        }
    }

    private final int id;
    private final int networkType;

    InventoryType(int id) {
        this(id, id);
    }

    InventoryType(int id, InventoryType inheritor) {
        this(id, inheritor.getNetworkType());
    }

    public int getNetworkType() {
        return networkType;
    }

    public static InventoryType from(int id) {
        InventoryType type = VALUES[id + 9];
        if (type == null) throw new IllegalArgumentException("Unknown InventoryType: " + id);
        return type;
    }
}
