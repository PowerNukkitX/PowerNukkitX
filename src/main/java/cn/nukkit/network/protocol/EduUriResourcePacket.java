package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.EduSharedUriResource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EduUriResourcePacket extends DataPacket {
    public EduSharedUriResource eduSharedUriResource;

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

    @Override
    public int pid() {
        return ProtocolInfo.EDU_URI_RESOURCE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
