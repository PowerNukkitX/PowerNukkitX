package cn.nukkit.network.protocol.types.inventorytransaction;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CreeperFace
 */
@ToString
@Slf4j
public class NetworkInventoryAction {
    
    
    public static final NetworkInventoryAction[] EMPTY_ARRAY = new NetworkInventoryAction[0];
    public static final int SOURCE_CONTAINER = 0;
    public static final int SOURCE_WORLD = 2; //drop/pickup item entity
    public static final int SOURCE_CREATIVE = 3;
    public static final int SOURCE_TODO = 99999;
    public static final int SOURCE_CRAFT_SLOT = 100;
    /**
     * Fake window IDs for the SOURCE_TODO type (99999)
     * <p>
     * These identifiers are used for inventory source types which are not currently implemented server-side in MCPE.
     * As a general rule of thumb, anything that doesn't have a permanent inventory is client-side. These types are
     * to allow servers to track what is going on in client-side windows.
     * <p>
     * Expect these to change in the future.
     */
    public static final int SOURCE_TYPE_CRAFTING_ADD_INGREDIENT = -2;
    public static final int SOURCE_TYPE_CRAFTING_REMOVE_INGREDIENT = -3;
    public static final int SOURCE_TYPE_CRAFTING_RESULT = -4;
    public static final int SOURCE_TYPE_CRAFTING_USE_INGREDIENT = -5;
    public static final int SOURCE_TYPE_ANVIL_INPUT = -10;
    public static final int SOURCE_TYPE_ANVIL_MATERIAL = -11;
    public static final int SOURCE_TYPE_ANVIL_RESULT = -12;
    public static final int SOURCE_TYPE_ANVIL_OUTPUT = -13;
    public static final int SOURCE_TYPE_ENCHANT_INPUT = -15;
    public static final int SOURCE_TYPE_ENCHANT_MATERIAL = -16;
    public static final int SOURCE_TYPE_ENCHANT_OUTPUT = -17;
    public static final int SOURCE_TYPE_TRADING_OUTPUT = -30;//输出物品
    public static final int SOURCE_TYPE_TRADING_INPUT_1 = -31;//实际上不管是交易物品A还是交易物品B都是这个window id
    //下面这两个没啥用
    public static final int SOURCE_TYPE_TRADING_INPUT_2 = -32;
    public static final int SOURCE_TYPE_TRADING_USE_INPUTS = -33;
    public static final int SOURCE_TYPE_BEACON = -24;
    /**
     * Any client-side window dropping its contents when the player closes it
     */
    public static final int SOURCE_TYPE_CONTAINER_DROP_CONTENTS = -100;

    @Deprecated
    @DeprecationDetails(since = "1.19.80-r2", reason = "replace", replaceWith = "InventorySource")
    @ToString.Exclude
    public int sourceType;
    @Deprecated
    @DeprecationDetails(since = "1.19.80-r2", reason = "replace", replaceWith = "InventorySource")
    @ToString.Exclude
    public int windowId;
    @Deprecated
    @DeprecationDetails(since = "1.19.80-r2", reason = "replace", replaceWith = "InventorySource")
    @ToString.Exclude
    public long unknown;

    @Deprecated
    @ToString.Exclude
    public int stackNetworkId = 0;

    private InventorySource inventorySource;
    public int inventorySlot;
    public Item oldItem;
    public Item newItem;

    
    
    public InventorySource getInventorySource() {
        return inventorySource;
    }

    
    
    public void setInventorySource(InventorySource inventorySource) {
        this.inventorySource = inventorySource;
    }

    public NetworkInventoryAction read(InventoryTransactionPacket packet) {
        //read InventorySource
        InventorySource.Type type = InventorySource.Type.byId((int) packet.getUnsignedVarInt());
        switch (type) {
            case UNTRACKED_INTERACTION_UI ->
                    inventorySource = InventorySource.fromUntrackedInteractionUI(packet.getVarInt());
            case CONTAINER -> {
                this.windowId = packet.getVarInt();
                inventorySource = InventorySource.fromContainerWindowId(this.windowId);
            }
            case GLOBAL -> inventorySource = InventorySource.fromGlobalInventory();
            case WORLD_INTERACTION -> {
                this.unknown = packet.getUnsignedVarInt();
                InventorySource.Flag flag = InventorySource.Flag.values()[(int) this.unknown];
                inventorySource = InventorySource.fromWorldInteraction(flag);
            }
            case CREATIVE -> inventorySource = InventorySource.fromCreativeInventory();
            case NON_IMPLEMENTED_TODO -> {
                this.windowId = packet.getVarInt();
                switch (this.windowId) {
                    case SOURCE_TYPE_CRAFTING_RESULT, SOURCE_TYPE_CRAFTING_USE_INGREDIENT ->
                            packet.isCraftingPart = true;
                    case SOURCE_TYPE_ENCHANT_INPUT, SOURCE_TYPE_ENCHANT_OUTPUT, SOURCE_TYPE_ENCHANT_MATERIAL ->
                            packet.isEnchantingPart = true;
                    case SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_RESULT ->
                            packet.isRepairItemPart = true;
                    case SOURCE_TYPE_TRADING_INPUT_1, SOURCE_TYPE_TRADING_INPUT_2, SOURCE_TYPE_TRADING_USE_INPUTS, SOURCE_TYPE_TRADING_OUTPUT ->
                            packet.isTradeItemPart = true;
                }
                inventorySource = InventorySource.fromNonImplementedTodo(this.windowId);
            }
            default -> inventorySource = InventorySource.fromInvalid();
        }
        this.inventorySlot = (int) packet.getUnsignedVarInt();
        this.oldItem = packet.getSlot();
        this.newItem = packet.getSlot();
        return this;
    }

    public void write(InventoryTransactionPacket packet) {
        packet.putUnsignedVarInt(this.inventorySource.getType().id());
        switch (inventorySource.getType()) {
            case CONTAINER, UNTRACKED_INTERACTION_UI, NON_IMPLEMENTED_TODO ->
                    packet.putVarInt(inventorySource.getContainerId());
            case WORLD_INTERACTION -> packet.putUnsignedVarInt(inventorySource.getFlag().ordinal());
        }
        packet.putUnsignedVarInt(this.inventorySlot);
        packet.putSlot(this.oldItem);
        packet.putSlot(this.newItem);
    }
}
