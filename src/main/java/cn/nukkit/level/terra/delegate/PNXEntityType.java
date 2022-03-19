package cn.nukkit.level.terra.delegate;

import cn.nukkit.entity.Entity;
import com.dfsek.terra.api.entity.EntityType;

public record PNXEntityType(Entity entity) implements EntityType {
    @Override
    public Entity getHandle() {
        return entity;
    }
}
