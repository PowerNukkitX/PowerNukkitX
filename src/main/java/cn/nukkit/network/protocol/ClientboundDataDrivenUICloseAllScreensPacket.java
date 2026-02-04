package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.LabTableReactionType;
import cn.nukkit.network.protocol.types.LabTableType;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ClientboundDataDrivenUICloseAllScreensPacket extends DataPacket {
    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_DDUI_CLOSE_ALL_SCREENS;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
