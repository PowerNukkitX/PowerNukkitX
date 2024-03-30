package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.hud.HudElement;
import cn.nukkit.network.protocol.types.hud.HudVisibility;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetHudPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SET_HUD;

    public final Set<HudElement> elements = new ObjectOpenHashSet<>();
    public HudVisibility visibility;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.elements.clear();
        byteBuf.readArray(this.elements, value -> HudElement.values()[byteBuf.readUnsignedVarInt()]);
        this.visibility = HudVisibility.values()[byteBuf.readByte()];
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(this.elements, (buf, element) -> byteBuf.writeUnsignedVarInt(element.ordinal()));
        byteBuf.writeByte((byte) this.visibility.ordinal());
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
