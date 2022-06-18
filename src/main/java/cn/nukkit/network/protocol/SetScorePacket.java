package cn.nukkit.network.protocol;

import cn.nukkit.scoreboard.data.ScorerType;

import java.util.ArrayList;
import java.util.List;

public class SetScorePacket extends DataPacket {
    public Action action;
    public List<ScoreInfo> infos = new ArrayList<>();

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
        this.putUnsignedVarInt(this.infos.size());

        for (ScoreInfo info : this.infos) {
            this.putVarLong(info.scoreboardId);
            this.putString(info.objectiveId);
            this.putLInt(info.score);
            if (this.action == Action.SET) {
                this.putByte((byte) info.type.ordinal());
                switch (info.type) {
                    case ENTITY:
                    case PLAYER:
                        this.putVarLong(info.entityId);
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
        public long scoreboardId;
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
            this.scoreboardId = scoreboardId;
            this.objectiveId = objectiveId;
            this.score = score;
            this.type = type;
            this.entityId = entityId;
            this.name = null;
        }
    }
}
