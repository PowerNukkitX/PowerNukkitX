package cn.nukkit.level.terra.delegate;

import cn.nukkit.entity.Entity;
import com.dfsek.terra.api.entity.EntityType;

public record PNXEntityType(Class<? extends Entity> entityClazz) implements EntityType {
    @Override
    public Class<? extends Entity> getHandle() {
        return entityClazz;
    }
}
