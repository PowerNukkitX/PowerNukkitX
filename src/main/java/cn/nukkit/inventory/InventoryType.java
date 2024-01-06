package cn.nukkit.inventory;


import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public final class InventoryType {
    public static final InventoryType NONE = new InventoryType(-1, "None", -9);

    /**
     * 27 slots
     */
    public static final InventoryType CHEST = new InventoryType(27, "Chest", 0);

    /**
     * 27 slots
     */
    public static final InventoryType ENDER_CHEST = new InventoryType(27, "Ender Chest", 0);

    /**
     * 27 slots
     */
    public static final InventoryType SHULKER_BOX = new InventoryType(27, "Shulker Box", 0);

    /**
     * 27 slots
     */
    public static final InventoryType BARREL = new InventoryType(27, "Barrel", 0);

    /**
     * 54 3*9 left chest 3*9 right chest
     */
    public static final InventoryType DOUBLE_CHEST = new InventoryType(27 + 27, "Double Chest", 0);

    /**
     * 1*9 PLAYER HOTBAR + 3*9 PLAYER INVENTORY + 4 ARMOR
     */
    public static final InventoryType PLAYER_INVENTORY = new InventoryType(9 + 27 + 4, "Player", -1);

    /**
     * 1 slot
     */
    public static final InventoryType OFFHAND = new InventoryType(1, "Offhand", NONE.getNetworkType());

    /**
     * 4 CRAFTING slots, 1 RESULT
     */
    public static final InventoryType CRAFTING = new InventoryType(4 + 1, "Crafting", 1);

    /**
     * 9 CRAFTING slots, 1 RESULT
     */
    public static final InventoryType WORKBENCH = new InventoryType(9 + 1, "Crafting", 1);

    /**
     * 2 slots, 1 result
     */
    public static final InventoryType FURNACE = new InventoryType(3, "Furnace", 2);

    /**
     * 2 slots, 1 lapis
     */
    public static final InventoryType ENCHANT_TABLE = new InventoryType(2, "Enchant", 3);

    /**
     * 1 slots, 3 potion , 1 fuel
     */
    public static final InventoryType BREWING_STAND = new InventoryType(5, "Brewing", 4);

    /**
     * 1 slots, 1 material ,1 output
     */
    public static final InventoryType ANVIL = new InventoryType(3, "Anvil", 5);

    /**
     * 9 slots
     */
    public static final InventoryType DISPENSER = new InventoryType(9, "Dispenser", 6);

    /**
     * 9 slots
     */
    public static final InventoryType DROPPER = new InventoryType(9, "Dropper", 7);

    /**
     * 5 slots
     */
    public static final InventoryType HOPPER = new InventoryType(5, "Hopper", 8);

    /**
     * 1 slot
     */
    public static final InventoryType CURSOR = new InventoryType(1, "Cursor", -1);

    /**
     * 1 slot
     */
    public static final InventoryType CAULDRON = new InventoryType(1, "Cauldron", 9);

    /**
     * 27 slots
     */
    public static final InventoryType MINECART_CHEST = new InventoryType(27, "Minecart with Chest", 10);

    /**
     * 5 slots
     */
    public static final InventoryType MINECART_HOPPER = new InventoryType(5, "Minecart with Hopper", 11);

    /**
     * 2 slots
     */
    public static final InventoryType HORSE = new InventoryType(2, "Horse", 12);

    /**
     * 1 slot
     */
    public static final InventoryType BEACON = new InventoryType(1, "Beacon", 13);

    /**
     * -1 slots
     */
    public static final InventoryType STRUCTURE_BLOCK = new InventoryType(-1, "StructureBlock", 14);

    /**
     * 3 slots
     */
    public static final InventoryType TRADING = new InventoryType(3, "Villager Trade", 15);

    /**
     * -1 slots
     */
    public static final InventoryType COMMAND_BLOCK = new InventoryType(-1, "CommandBlock", 16);

    /**
     * 1 slot
     */
    public static final InventoryType JUKEBOX = new InventoryType(1, "JukeBox Block", 17);

    /**
     * 3 slots
     */
    public static final InventoryType GRINDSTONE = new InventoryType(3, "Grindstone", 26);

    /**
     * 3 slots
     */
    public static final InventoryType BLAST_FURNACE = new InventoryType(3, "Blast Furnace", 27);

    /**
     * 3 slots
     */
    public static final InventoryType SMOKER = new InventoryType(3, "Smoker", 28);

    /**
     * 2 slots
     */
    public static final InventoryType STONECUTTER = new InventoryType(2, "Stonecutter", 29);

    /**
     * 3 slots
     */
    public static final InventoryType CARTOGRAPHY = new InventoryType(3, "Cartography Table", 30);

    /**
     * 9 slots
     */
    public static final InventoryType HUD = new InventoryType(9, "Cartography Table", 31);

    /**
     * 2 slots
     */
    public static final InventoryType SMITHING_TABLE = new InventoryType(2, "Smithing Table", 33);

    /**
     * 27 slots
     */
    public static final InventoryType CHEST_BOAT = new InventoryType(27, "Chest Boat", 34);

    /**
     * 4 slots
     */
    public static final InventoryType CAMPFIRE = new InventoryType(4, "Campfire", NONE.getNetworkType());

    /**
     * 36 slots
     */
    public static final InventoryType ENTITY_EQUIPMENT = new InventoryType(36, "Entity Equipment", NONE.getNetworkType());

    /**
     * 4 slots
     */
    public static final InventoryType ENTITY_ARMOR = new InventoryType(4, "Entity Armor", NONE.getNetworkType());


    private final int size;
    private final String name;
    private final int typeId;
    private final ContainerSlotType[] slotTypeTable;
    private final Set<ContainerSlotType> heldSlotTypes;
    // 原版对于某些容器提供的slot范围与allay的设计不符，例如工作台：原版为32-39，而allay为0-8
    // 此双向映射表用于解决上诉问题
    // The slot range provided by vanilla for some containers does not match the design of the allay. For example, crafting table: 32-39 in vanilla but 0-8 in allay
    // This bidirectional mapping table is used to resolve appeals
    // network slot <-> slot that allay used
    private final BiMap<Integer, Integer> networkSlotIndexMapper;

    InventoryType(int size, String name, int typeId) {
        this.size = size;
        this.name = name;
        this.typeId = typeId;
        this.slotTypeTable = new ContainerSlotType[size];
        this.heldSlotTypes = new HashSet<>();
        this.networkSlotIndexMapper = HashBiMap.create();
        for (int i = 0; i < size; i++) {
            networkSlotIndexMapper.put(i, i);
        }
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public int getNetworkType() {
        return typeId;
    }

    public BiMap<Integer, Integer> getNetworkSlotIndexMapper() {
        return networkSlotIndexMapper;
    }

    public ContainerSlotType[] getSlotTypeTable() {
        return slotTypeTable;
    }

    public Set<ContainerSlotType> getHeldSlotTypes() {
        return heldSlotTypes;
    }

    public void mapRangedSlotToType(int l, int r, ContainerSlotType type) {
        if (l > r) throw new IllegalArgumentException("Left must smaller than right!");
        if (l > slotTypeTable.length || r > slotTypeTable.length)
            throw new IllegalArgumentException("Left or right bigger than size!");
        heldSlotTypes.add(type);
        for (int i = l; i <= r; i++) {
            slotTypeTable[i] = type;
        }
    }

    public void mapAllSlotToType(ContainerSlotType type) {
        heldSlotTypes.add(type);
        Arrays.fill(slotTypeTable, type);
    }

    public void holdSlotType(ContainerSlotType type) {
        heldSlotTypes.add(type);
    }

    public void mapNetworkSlotIndex(int networkSlotIndex, int slot) {
        networkSlotIndexMapper.forcePut(networkSlotIndex, slot);
    }

    public void mapRangedNetworkSlotIndex(int l, int r, int slot) {
        if (l > r) throw new IllegalArgumentException("Left must smaller than right!");
        for (int i = l, j = 0; i <= r; i++, j++) {
            networkSlotIndexMapper.forcePut(i, slot + j);
        }
    }
}
