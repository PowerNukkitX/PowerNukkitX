package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmoteListPacket extends DataPacket {
    public long runtimeId;
    public final List<UUID> pieceIds = new ObjectArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.runtimeId = byteBuf.readEntityRuntimeId();
        int size = (int) byteBuf.readUnsignedVarInt();
        for (int i = 0; i < size; i++) {
            UUID id = byteBuf.readUUID();
            pieceIds.add(id);
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(runtimeId);
        byteBuf.writeUnsignedVarInt(pieceIds.size());
        for (UUID id : pieceIds) {
            byteBuf.writeUUID(id);
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.EMOTE_LIST_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
