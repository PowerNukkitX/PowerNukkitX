package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HurtArmorPacket extends DataPacket {
    public int cause;
    public int damage;
    public long armorSlots;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.cause = byteBuf.readVarInt();
        this.damage = byteBuf.readVarInt();
        this.armorSlots = byteBuf.readUnsignedVarLong();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.cause);
        byteBuf.writeVarInt(this.damage);
        byteBuf.writeUnsignedVarLong(this.armorSlots);
    }

    @Override
    public int pid() {
        return ProtocolInfo.HURT_ARMOR_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
