package cn.nukkit.scoreboard.scorer;

import cn.nukkit.entity.Entity;
import cn.nukkit.scoreboard.IScoreboard;
import cn.nukkit.scoreboard.IScoreboardLine;
import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.ScoreInfo;

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
    public ScoreInfo.IdentityDefinitionType getScorerType() {
        return ScoreInfo.IdentityDefinitionType.ENTITY;
    }

    @Override
    public int hashCode() {
        return entityUuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityScorer entityScorer) {
            return entityUuid.equals(entityScorer.entityUuid);
        }
        return false;
    }

    @Override
    public String getName() {
        return entityUuid.toString();
    }

    @Override
    public ScoreInfo toNetworkInfo(IScoreboard scoreboard, IScoreboardLine line) {
        return new ScoreInfo(
                line.getLineId(),
                scoreboard.getObjectiveName(),
                line.getScore(),
                this.getScorerType(),
                entityUuid.getMostSignificantBits()
        );
    }
}