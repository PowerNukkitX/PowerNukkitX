package org.powernukkitx.scoreboard.scorer;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ChangeEntityScore;
import org.cloudburstmc.protocol.bedrock.data.payload.scoreboard.ScorePacketEntryAction;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.scoreboard.IScoreboard;
import org.powernukkitx.scoreboard.IScoreboardLine;

import java.util.UUID;


@Getter
public class EntityScorer implements IScorer {

    private UUID entityUuid;

    public EntityScorer(UUID uuid) {
        this.entityUuid = uuid;
    }

    public EntityScorer(Entity entity) {
        this.entityUuid = entity.getUniqueId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityScorer entityScorer) {
            return entityUuid.equals(entityScorer.entityUuid);
        }
        return false;
    }

    @Override
    public ScorePacketEntryAction getScorerType() {
        return ScorePacketEntryAction.CHANGE_ENTITY;
    }

    @Override
    public String getName() {
        return entityUuid.toString();
    }

    @Override
    public ChangeEntityScore toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line) {
        final ChangeEntityScore score = new ChangeEntityScore();
        score.setScoreboardId(line.getLineId());
        score.setObjectiveName(scoreboard.getObjectiveName());
        score.setScoreValue(line.getScore());
        score.setActorId(this.entityUuid.getMostSignificantBits());
        return score;
    }
}
