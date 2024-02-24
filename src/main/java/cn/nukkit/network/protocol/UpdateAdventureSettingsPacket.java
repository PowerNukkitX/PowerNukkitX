package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;


@ToString
public class UpdateAdventureSettingsPacket extends DataPacket {
    public boolean noPvM;
    public boolean noMvP;
    public boolean immutableWorld;
    public boolean showNameTags;
    public boolean autoJump;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeBoolean(noPvM);
        byteBuf.writeBoolean(noMvP);
        byteBuf.writeBoolean(immutableWorld);
        byteBuf.writeBoolean(showNameTags);
        byteBuf.writeBoolean(autoJump);
    }

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_ADVENTURE_SETTINGS_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
