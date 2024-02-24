package cn.nukkit.network.protocol.types.inventory.transaction;

import cn.nukkit.item.Item;
import cn.nukkit.network.connection.util.HandleByteBuf;
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

    public NetworkInventoryAction read(InventoryTransactionPacket pk, HandleByteBuf byteBuf) {
        //read InventorySource
        InventorySource.Type type = InventorySource.Type.byId((int) byteBuf.readUnsignedVarInt());
        switch (type) {
            case UNTRACKED_INTERACTION_UI ->
                    inventorySource = InventorySource.fromUntrackedInteractionUI(byteBuf.readVarInt());
            case CONTAINER -> {
                inventorySource = InventorySource.fromContainerWindowId(byteBuf.readVarInt());
            }
            case GLOBAL -> inventorySource = InventorySource.fromGlobalInventory();
            case WORLD_INTERACTION -> {
                InventorySource.Flag flag = InventorySource.Flag.values()[(int) byteBuf.readUnsignedVarInt()];
                inventorySource = InventorySource.fromWorldInteraction(flag);
            }
            case CREATIVE -> inventorySource = InventorySource.fromCreativeInventory();
            case NON_IMPLEMENTED_TODO -> {
                int wid = byteBuf.readVarInt();
                switch (wid) {
                    case SOURCE_TYPE_CRAFTING_RESULT, SOURCE_TYPE_CRAFTING_USE_INGREDIENT ->
                            pk.isCraftingPart = true;
                    case SOURCE_TYPE_ENCHANT_INPUT, SOURCE_TYPE_ENCHANT_OUTPUT, SOURCE_TYPE_ENCHANT_MATERIAL ->
                            pk.isEnchantingPart = true;
                    case SOURCE_TYPE_ANVIL_INPUT, SOURCE_TYPE_ANVIL_MATERIAL, SOURCE_TYPE_ANVIL_RESULT ->
                            pk.isRepairItemPart = true;
                    case SOURCE_TYPE_TRADING_INPUT_1, SOURCE_TYPE_TRADING_INPUT_2, SOURCE_TYPE_TRADING_USE_INPUTS, SOURCE_TYPE_TRADING_OUTPUT ->
                            pk.isTradeItemPart = true;
                }
                inventorySource = InventorySource.fromNonImplementedTodo(wid);
            }
            default -> inventorySource = InventorySource.fromInvalid();
        }
        this.inventorySlot = (int) byteBuf.readUnsignedVarInt();
        this.oldItem = byteBuf.readSlot();
        this.newItem = byteBuf.readSlot();
        return this;
    }

    public void write(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(this.inventorySource.getType().id());
        switch (inventorySource.getType()) {
            case CONTAINER, UNTRACKED_INTERACTION_UI, NON_IMPLEMENTED_TODO ->
                    byteBuf.writeVarInt(inventorySource.getContainerId());
            case WORLD_INTERACTION -> byteBuf.writeUnsignedVarInt(inventorySource.getFlag().ordinal());
        }
        byteBuf.writeUnsignedVarInt(this.inventorySlot);
        byteBuf.writeSlot(this.oldItem);
        byteBuf.writeSlot(this.newItem);
    }
}
