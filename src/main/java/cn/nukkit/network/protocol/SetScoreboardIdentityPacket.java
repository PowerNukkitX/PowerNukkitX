package cn.nukkit.network.protocol;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.UUID;

//May be obsolete, this package is not used now
public class SetScoreboardIdentityPacket extends DataPacket {

    private final List<Entry> entries = new ObjectArrayList<>();
    private Action action;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_SCOREBOARD_IDENTITY_PACKET;
    }

    @Override
    public void decode() {
        //only server -> client
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.action.ordinal());

        for (Entry entry : this.entries) {
            this.putVarLong(entry.scoreboardId);
            this.putUUID(entry.uuid);
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
}
