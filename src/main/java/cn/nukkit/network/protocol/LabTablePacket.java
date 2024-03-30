package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.LabTableReactionType;
import cn.nukkit.network.protocol.types.LabTableType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LabTablePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.LAB_TABLE_PACKET;
    public LabTableType actionType;
    public BlockVector3 blockPosition;
    public LabTableReactionType reactionType;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeByte((byte) actionType.ordinal());
        byteBuf.writeBlockVector3(blockPosition);
        byteBuf.writeByte((byte) reactionType.ordinal());
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
