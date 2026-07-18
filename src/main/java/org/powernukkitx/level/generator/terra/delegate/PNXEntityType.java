package org.powernukkitx.level.generator.terra.delegate;

import com.dfsek.terra.api.entity.EntityType;

public record PNXEntityType(String identifier) implements EntityType {
    @Override
    public String getHandle() {
        return identifier;
    }
}
