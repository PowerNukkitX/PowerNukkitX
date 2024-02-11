package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.hud.HudElement;
import cn.nukkit.network.protocol.types.hud.HudVisibility;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Set;

public class SetHudPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SET_HUD;

    public final Set<HudElement> elements = new ObjectOpenHashSet<>();
    public HudVisibility visibility;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.elements.clear();
        this.getArray(this.elements, value -> HudElement.values()[(int) this.getUnsignedVarInt()]);
        this.visibility = HudVisibility.values()[this.getByte()];
    }

    @Override
    public void encode() {
        this.reset();
        this.putArray(this.elements, (buf, element) -> this.putUnsignedVarInt(element.ordinal()));
        this.putByte((byte) this.visibility.ordinal());
    }
}
