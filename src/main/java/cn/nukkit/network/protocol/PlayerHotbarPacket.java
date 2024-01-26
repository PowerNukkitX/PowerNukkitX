package cn.nukkit.network.protocol;

import cn.nukkit.inventory.SpecialWindowId;
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
    public void decode() {
        this.selectedHotbarSlot = (int) this.getUnsignedVarInt();
        this.windowId = this.getByte();
        this.selectHotbarSlot = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.selectedHotbarSlot);
        this.putByte((byte) this.windowId);
        this.putBoolean(this.selectHotbarSlot);
    }
}
