package cn.nukkit.level.terra.delegate;

import cn.nukkit.entity.Entity;
import com.dfsek.terra.api.entity.EntityType;

public record PNXEntityType(String identifier) implements EntityType {
    @Override
    public String getHandle() {
        return identifier;
    }
}
