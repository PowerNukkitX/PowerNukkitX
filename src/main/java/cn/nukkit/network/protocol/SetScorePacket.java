package cn.nukkit.network.protocol;

import com.nukkitx.network.util.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class SetScorePacket extends DataPacket {
    private Action action;
    private List<ScoreInfo> infos = new ObjectArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.SET_SCORE_PACKET;
    }

    @Override
    public void decode() {
        //only server -> client
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.action.ordinal());

        for (ScoreInfo info : this.infos) {
            this.putVarLong(info.scoreboardId);
            this.putString(info.objectiveId);
            this.putLInt(info.score);
            if (this.action == Action.SET) {
                this.putByte((byte) info.type.ordinal());
                switch (info.type) {
                    case ENTITY:
                    case PLAYER:
                        this.putUnsignedVarLong(info.entityId);
                        break;
                    case FAKE:
                        this.putString(info.name);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid score info received");
                }
            }
        }
    }

    public enum Action {
        SET,
        REMOVE
    }

    public static class ScoreInfo {
        public long scoreboardId;//index
        public String objectiveId;
        public int score;
        public ScorerType type;
        public String name;
        public long entityId;

        public ScoreInfo(long scoreboardId, String objectiveId, int score) {
            this.scoreboardId = scoreboardId;
            this.objectiveId = objectiveId;
            this.score = score;
            this.type = ScorerType.INVALID;
            this.name = null;
            this.entityId = -1;
        }

        public ScoreInfo(long scoreboardId, String objectiveId, int score, String name) {
            this.scoreboardId = scoreboardId;
            this.objectiveId = objectiveId;
            this.score = score;
            this.type = ScorerType.FAKE;
            this.name = name;
            this.entityId = -1;
        }

        public ScoreInfo(long scoreboardId, String objectiveId, int score, ScorerType type, long entityId) {
            Preconditions.checkArgument(type == ScorerType.ENTITY || type == ScorerType.PLAYER, "Must be player or entity");
            this.scoreboardId = scoreboardId;
            this.objectiveId = objectiveId;
            this.score = score;
            this.type = type;
            this.entityId = entityId;
            this.name = null;
        }

        public enum ScorerType {
            INVALID,
            PLAYER,
            ENTITY,
            FAKE
        }
    }
}
