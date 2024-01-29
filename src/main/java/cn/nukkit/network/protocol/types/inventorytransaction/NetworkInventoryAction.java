package cn.nukkit.network.protocol.types.inventorytransaction;

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
    public static final int SOURCE_TYPE_CRAFTING_RESULT = -4;
    public static final int SOURCE_TYPE_CRAFTING_USE_INGREDIENT = -5;
    public static final int SOURCE_TYPE_ANVIL_INPUT = -10;
    public static final int SOURCE_TYPE_ANVIL_MATERIAL = -11;
    public static final int SOURCE_TYPE_ANVIL_RESULT = -12;
    public static final int SOURCE_TYPE_ENCHANT_INPUT = -15;
    public static final int SOURCE_TYPE_ENCHANT_MATERIAL = -16;
    public static final int SOURCE_TYPE_ENCHANT_OUTPUT = -17;
    public static final int SOURCE_TYPE_TRADING_OUTPUT = -30;
    public static final int SOURCE_TYPE_TRADING_INPUT_1 = -31;
    public static final int SOURCE_TYPE_TRADING_INPUT_2 = -32;
    public static final int SOURCE_TYPE_TRADING_USE_INPUTS = -33;

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
                inventorySource = InventorySource.fromContainerWindowId(packet.getVarInt());
            }
            case GLOBAL -> inventorySource = InventorySource.fromGlobalInventory();
            case WORLD_INTERACTION -> {
                InventorySource.Flag flag = InventorySource.Flag.values()[(int) packet.getUnsignedVarInt()];
                inventorySource = InventorySource.fromWorldInteraction(flag);
            }
            case CREATIVE -> inventorySource = InventorySource.fromCreativeInventory();
            case NON_IMPLEMENTED_TODO -> {
                int wid = packet.getVarInt();
                switch (wid) {
                    case SOURCE_TYPE_CRAFTING_RESULT, SOURCE_TYPE_CRAFTING_USE_INGREDIENT ->
                            packet.isCraftingPart = true;
                    case SOURCE_TYPE_ENCHANT_INPUT, SOURCE_TYPE_ENCHANT_OUTPUT, SOURCE_TYPE_ENCHANT_MATERIAL ->
                            packet.isEnchantingPart = true;
                    case SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_RESULT ->
                            packet.isRepairItemPart = true;
                    case SOURCE_TYPE_TRADING_INPUT_1, SOURCE_TYPE_TRADING_INPUT_2, SOURCE_TYPE_TRADING_USE_INPUTS, SOURCE_TYPE_TRADING_OUTPUT ->
                            packet.isTradeItemPart = true;
                }
                inventorySource = InventorySource.fromNonImplementedTodo(wid);
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
