package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.EduSharedUriResource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EduUriResourcePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.EDU_URI_RESOURCE_PACKET;
    public EduSharedUriResource eduSharedUriResource;


    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        String buttonName = byteBuf.readString();
        String linkUri = byteBuf.readString();
        this.eduSharedUriResource = new EduSharedUriResource(buttonName, linkUri);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeString(eduSharedUriResource.buttonName());
        byteBuf.writeString(eduSharedUriResource.linkUri());
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
