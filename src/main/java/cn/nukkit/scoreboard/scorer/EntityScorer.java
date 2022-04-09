package cn.nukkit.scoreboard.scorer;

import cn.nukkit.entity.Entity;
import cn.nukkit.scoreboard.data.ScorerType;
import cn.nukkit.scoreboard.interfaces.Scorer;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EntityScorer implements Scorer {

    private UUID entityUuid;

    public EntityScorer(UUID uuid) {
        this.entityUuid = uuid;
    }

    public EntityScorer(Entity entity){
        this.entityUuid = entity.getEntityUniqueId();
    }

    @Override
    public ScorerType getScorerType() {
        return ScorerType.ENTITY;
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
        return String.valueOf(entityUuid.getMostSignificantBits());
    }
}
