package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.UUID;

//May be obsolete, this package is not used now
public class SetScoreboardIdentityPacket extends DataPacket {

    private final List<Entry> entries = new ObjectArrayList<>();
    private Action action;

    @Override
    public int pid() {
        return ProtocolInfo.SET_SCOREBOARD_IDENTITY_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        //only server -> client
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
        REMOVE
    }

    public static class Entry {
        public long scoreboardId;
        public UUID uuid;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
