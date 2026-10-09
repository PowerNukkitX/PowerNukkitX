package org.powernukkitx.scoreboard.scorer;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ChangeEntityScore;
import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ScorePacketEntryAction;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.IScoreboardLine;


@Getter
public class EntityScorer implements IScorer {

    private final long uniqueId;

    public EntityScorer(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public EntityScorer(Entity entity) {
        this.uniqueId = entity.uniqueIdLong();
    }

    /**
     * Returns the scorer ActorUniqueID as its persisted string representation.
     *
     * @return ActorUniqueID string
     */
    public String uniqueId() {
        return Long.toString(this.uniqueId);
    }

    /**
     * Returns the scorer ActorUniqueID.
     *
     * @return ActorUniqueID
     */
    public long uniqueIdLong() {
        return this.uniqueId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.uniqueId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityScorer entityScorer) {
            return this.uniqueId == entityScorer.uniqueId;
        }
        return false;
    }

    @Override
    public ScorePacketEntryAction getScorerType() {
        return ScorePacketEntryAction.CHANGE_ENTITY;
    }

    @Override
    public String getName() {
        return Long.toString(this.uniqueId);
    }

    @Override
    public ChangeEntityScore toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line) {
        final ChangeEntityScore score = new ChangeEntityScore();
        score.setScoreboardId(line.getLineId());
        score.setObjectiveName(scoreboard.getObjectiveName());
        score.setScoreValue(line.getScore());
        score.setActorId(this.uniqueId);
        return score;
    }
}
