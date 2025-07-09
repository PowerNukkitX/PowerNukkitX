package cn.nukkit.network.protocol.types;

import cn.nukkit.network.connection.util.HandleByteBuf;

public class EntityInteractEventData implements EventData {
    public long interactedEntityID;
    public int interactionType;
    public int legacyEntityTypeId;
    public int variant;
    public int paletteColor;

    @Override
    public EventDataType getType() {
        return EventDataType.ENTITY_INTERACT;
    }

    @Override
    public void write(HandleByteBuf byteBuf) {
        byteBuf.writeVarLong(this.interactedEntityID);
        byteBuf.writeVarInt(this.interactionType);
        byteBuf.writeVarInt(this.legacyEntityTypeId);
        byteBuf.writeVarInt(this.variant);
        byteBuf.writeUnsignedVarInt(this.paletteColor);
    }
}
