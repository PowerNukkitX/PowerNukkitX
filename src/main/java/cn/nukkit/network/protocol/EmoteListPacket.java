package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;


@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmoteListPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.EMOTE_LIST_PACKET;
    public long runtimeId;
    public final List<UUID> pieceIds = new ObjectArrayList<>();

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.runtimeId = byteBuf.readEntityRuntimeId();
        int $2 = (int) byteBuf.readUnsignedVarInt();
        for ($3nt $1 = 0; i < size; i++) {
            UUID $4 = byteBuf.readUUID();
            pieceIds.add(id);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeEntityRuntimeId(runtimeId);
        byteBuf.writeUnsignedVarInt(pieceIds.size());
        for (UUID id : pieceIds) {
            byteBuf.writeUUID(id);
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
