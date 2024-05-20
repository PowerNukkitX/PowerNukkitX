package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.LegacySetItemSlotData;
import cn.nukkit.network.protocol.types.inventory.transaction.NetworkInventoryAction;
import cn.nukkit.network.protocol.types.inventory.transaction.ReleaseItemData;
import cn.nukkit.network.protocol.types.inventory.transaction.TransactionData;
import cn.nukkit.network.protocol.types.inventory.transaction.UseItemData;
import cn.nukkit.network.protocol.types.inventory.transaction.UseItemOnEntityData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionPacket extends DataPacket {
    //InventoryTransactionType 0-5
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_MISMATCH = 1;
    public static final int TYPE_USE_ITEM = 2;
    public static final int TYPE_USE_ITEM_ON_ENTITY = 3;
    public static final int TYPE_RELEASE_ITEM = 4;

    public static final int USE_ITEM_ACTION_CLICK_BLOCK = 0;
    public static final int USE_ITEM_ACTION_CLICK_AIR = 1;
    public static final int USE_ITEM_ACTION_BREAK_BLOCK = 2;

    public static final int RELEASE_ITEM_ACTION_RELEASE = 0; //bow shoot
    public static final int RELEASE_ITEM_ACTION_CONSUME = 1; //eat food, drink potion

    public static final int USE_ITEM_ON_ENTITY_ACTION_INTERACT = 0;
    public static final int USE_ITEM_ON_ENTITY_ACTION_ATTACK = 1;


    public static final int ACTION_MAGIC_SLOT_DROP_ITEM = 0;
    public static final int ACTION_MAGIC_SLOT_PICKUP_ITEM = 1;

    public static final int ACTION_MAGIC_SLOT_CREATIVE_DELETE_ITEM = 0;
    public static final int ACTION_MAGIC_SLOT_CREATIVE_CREATE_ITEM = 1;

    public int transactionType;
    public NetworkInventoryAction[] actions;
    public TransactionData transactionData;
    public final List<LegacySetItemSlotData> legacySlots = new ObjectArrayList<>();

    public int legacyRequestId;

    /**
     * NOTE: THESE FIELDS DO NOT EXIST IN THE PROTOCOL, it's merely used for convenience for us to easily
     * determine whether we're doing a crafting or enchanting transaction.
     */
    public boolean isCraftingPart = false;
    public boolean isEnchantingPart = false;
    public boolean isRepairItemPart = false;
    public boolean isTradeItemPart = false;

    @Override
    public int pid() {
        return ProtocolInfo.INVENTORY_TRANSACTION_PACKET;
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVarInt(this.legacyRequestId);
        byteBuf.writeUnsignedVarInt(this.transactionType);

        //slots array
        if (legacyRequestId != 0) {
            byteBuf.writeUnsignedVarInt(this.legacySlots.size());
            for (var slot : legacySlots) {
                byteBuf.writeByte((byte) slot.getContainerId());
                byteBuf.writeByteArray(slot.getSlots());
            }
        }

        byteBuf.writeUnsignedVarInt(this.actions.length);
        for (NetworkInventoryAction action : this.actions) {
            action.write(byteBuf);
        }

        switch (this.transactionType) {
            case TYPE_NORMAL:
            case TYPE_MISMATCH:
                break;
            case TYPE_USE_ITEM:
                UseItemData useItemData = (UseItemData) this.transactionData;
                byteBuf.writeUnsignedVarInt(useItemData.actionType);
                byteBuf.writeBlockVector3(useItemData.blockPos);
                byteBuf.writeBlockFace(useItemData.face);
                byteBuf.writeVarInt(useItemData.hotbarSlot);
                byteBuf.writeSlot(useItemData.itemInHand);
                byteBuf.writeVector3f(useItemData.playerPos.asVector3f());
                byteBuf.writeVector3f(useItemData.clickPos);
                byteBuf.writeUnsignedVarInt(useItemData.blockRuntimeId);
                break;
            case TYPE_USE_ITEM_ON_ENTITY:
                UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) this.transactionData;

                byteBuf.writeEntityRuntimeId(useItemOnEntityData.entityRuntimeId);
                byteBuf.writeUnsignedVarInt(useItemOnEntityData.actionType);
                byteBuf.writeVarInt(useItemOnEntityData.hotbarSlot);
                byteBuf.writeSlot(useItemOnEntityData.itemInHand);
                byteBuf.writeVector3f(useItemOnEntityData.playerPos.asVector3f());
                byteBuf.writeVector3f(useItemOnEntityData.clickPos.asVector3f());
                break;
            case TYPE_RELEASE_ITEM:
                ReleaseItemData releaseItemData = (ReleaseItemData) this.transactionData;

                byteBuf.writeUnsignedVarInt(releaseItemData.actionType);
                byteBuf.writeVarInt(releaseItemData.hotbarSlot);
                byteBuf.writeSlot(releaseItemData.itemInHand);
                byteBuf.writeVector3f(releaseItemData.headRot.asVector3f());
                break;
            default:
                throw new RuntimeException("Unknown transaction type " + this.transactionType);
        }
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.legacyRequestId = byteBuf.readVarInt();
        if (legacyRequestId != 0) {
            int length = byteBuf.readUnsignedVarInt();
            for (int i = 0; i < length; i++) {
                byte containerId = byteBuf.readByte();
                byte[] slots = byteBuf.readByteArray();
                this.legacySlots.add(new LegacySetItemSlotData(containerId, slots));
            }
        }
        //InventoryTransactionType
        this.transactionType = byteBuf.readUnsignedVarInt();

        int length = byteBuf.readUnsignedVarInt();
        Collection<NetworkInventoryAction> actions = new ArrayDeque<>();
        for (int i = 0; i < length; i++) {
            actions.add(new NetworkInventoryAction().read(this, byteBuf));
        }
        this.actions = actions.toArray(NetworkInventoryAction.EMPTY_ARRAY);

        switch (this.transactionType) {
            case TYPE_NORMAL:
            case TYPE_MISMATCH:
                //Regular ComplexInventoryTransaction doesn't read any extra data
                break;
            case TYPE_USE_ITEM:
                UseItemData itemData = new UseItemData();

                itemData.actionType = byteBuf.readUnsignedVarInt();
                itemData.blockPos = byteBuf.readBlockVector3();
                itemData.face = byteBuf.readBlockFace();
                itemData.hotbarSlot = byteBuf.readVarInt();
                itemData.itemInHand = byteBuf.readSlot();
                itemData.playerPos = byteBuf.readVector3f().asVector3();
                itemData.clickPos = byteBuf.readVector3f();
                itemData.blockRuntimeId = byteBuf.readUnsignedVarInt();

                this.transactionData = itemData;
                break;
            case TYPE_USE_ITEM_ON_ENTITY:
                UseItemOnEntityData useItemOnEntityData = new UseItemOnEntityData();

                useItemOnEntityData.entityRuntimeId = byteBuf.readEntityRuntimeId();
                useItemOnEntityData.actionType = byteBuf.readUnsignedVarInt();
                useItemOnEntityData.hotbarSlot = byteBuf.readVarInt();
                useItemOnEntityData.itemInHand = byteBuf.readSlot();
                useItemOnEntityData.playerPos = byteBuf.readVector3f().asVector3();
                useItemOnEntityData.clickPos = byteBuf.readVector3f().asVector3();

                this.transactionData = useItemOnEntityData;
                break;
            case TYPE_RELEASE_ITEM:
                ReleaseItemData releaseItemData = new ReleaseItemData();

                releaseItemData.actionType = byteBuf.readUnsignedVarInt();
                releaseItemData.hotbarSlot = byteBuf.readVarInt();
                releaseItemData.itemInHand = byteBuf.readSlot();
                releaseItemData.headRot = byteBuf.readVector3f().asVector3();

                this.transactionData = releaseItemData;
                break;
            default:
                throw new RuntimeException("Unknown transaction type " + this.transactionType);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
