package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.LabTableReactionType;
import cn.nukkit.network.protocol.types.LabTableType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;
import org.apache.logging.log4j.core.net.Protocol;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LabTablePacket extends DataPacket {
    public LabTableType actionType;
    public BlockVector3 blockPosition;
    public LabTableReactionType reactionType;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) actionType.ordinal());
        byteBuf.writeBlockVector3(blockPosition);
        byteBuf.writeByte((byte) reactionType.ordinal());
    }

    @Override
    public int pid() {
        return ProtocolInfo.LAB_TABLE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
