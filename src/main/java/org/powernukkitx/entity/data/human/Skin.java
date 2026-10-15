package org.powernukkitx.entity.data.human;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.SerializedSkin;
import org.powernukkitx.event.server.PacketSendEvent;
import org.powernukkitx.utils.SkinConverter;

@Getter
public class Skin {

    private final org.cloudburstmc.protocol.bedrock.data.skin.Skin skin;

    @Setter
    private boolean trusted;

    @Getter(AccessLevel.NONE)
    private volatile Serialized serialized;

    private record Serialized(SerializedSkin skin, boolean trusted) {
    }

    public Skin(org.cloudburstmc.protocol.bedrock.data.skin.Skin skin) {
        this.skin = skin;
    }

    public Skin(org.cloudburstmc.protocol.bedrock.data.skin.Skin skin, boolean trusted) {
        this.skin = skin;
        this.trusted = trusted;
    }

    /**
     * This skin in the form player list and skin packets carry, as
     * {@link SkinConverter#toSerializedSkin} creates it.
     * <p>
     * The skin itself never changes, so the converted form is kept and handed to every packet that
     * needs it, until {@link #setTrusted(boolean) trusted} changes. While a plugin listens to
     * {@link PacketSendEvent}, which could change a packet's skin on its way out, every call
     * converts again instead, so no two packets share one.
     *
     * @return the serialized skin; shared between packets unless a plugin listens to packet sends
     */
    public SerializedSkin toSerializedSkin() {
        if (!PacketSendEvent.getHandlers().isEmpty()) {
            return SkinConverter.toSerializedSkin(this.skin, this.trusted);
        }
        final boolean currentlyTrusted = this.trusted;
        final Serialized cached = this.serialized;
        if (cached != null && cached.trusted() == currentlyTrusted) {
            return cached.skin();
        }
        final SerializedSkin converted = SkinConverter.toSerializedSkin(this.skin, currentlyTrusted);
        this.serialized = new Serialized(converted, currentlyTrusted);
        return converted;
    }
}
