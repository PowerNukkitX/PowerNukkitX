package cn.nukkit.network.protocol;

import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class PlayerHotbarPacket extends DataPacket {

    public int selectedHotbarSlot;
    public int windowId = SpecialWindowId.PLAYER.getId();

    public boolean selectHotbarSlot = true;

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_HOTBAR_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.selectedHotbarSlot = (int) byteBuf.readUnsignedVarInt();
        this.windowId = byteBuf.readByte();
        this.selectHotbarSlot = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeUnsignedVarInt(this.selectedHotbarSlot);
        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeBoolean(this.selectHotbarSlot);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
