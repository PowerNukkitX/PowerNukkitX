package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.inventory.InventoryLayout;
import cn.nukkit.network.protocol.types.inventory.InventoryTabLeft;
import cn.nukkit.network.protocol.types.inventory.InventoryTabRight;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetPlayerInventoryOptionsPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.SET_PLAYER_INVENTORY_OPTIONS_PACKET;

    public InventoryTabLeft leftTab;
    public InventoryTabRight rightTab;
    public boolean filtering;
    public InventoryLayout layout;
    public InventoryLayout craftingLayout;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return (byte) NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.leftTab = InventoryTabLeft.VALUES[byteBuf.readVarInt()];
        this.rightTab = InventoryTabRight.VALUES[byteBuf.readVarInt()];
        this.filtering = byteBuf.readBoolean();
        this.layout = InventoryLayout.VALUES[byteBuf.readVarInt()];
        this.craftingLayout = InventoryLayout.VALUES[byteBuf.readVarInt()];
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVarInt(this.leftTab.ordinal());
        byteBuf.writeVarInt(this.rightTab.ordinal());
        byteBuf.writeBoolean(this.filtering);
        byteBuf.writeVarInt(this.layout.ordinal());
        byteBuf.writeVarInt(this.craftingLayout.ordinal());
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
