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
public class SetScoreboardIdentityPacket extends DataPacket {
    public final List<Entry> entries = new ObjectArrayList<>();
    public Action action;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) this.action.ordinal());

        for (Entry entry : this.entries) {
            byteBuf.writeVarLong(entry.scoreboardId);
            byteBuf.writeUUID(entry.uuid);
        }
    }

    public enum Action {
        ADD,
        REMOVE;
    }
    public static class Entry {
        public long scoreboardId;
        public UUID uuid;
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_SCOREBOARD_IDENTITY_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
