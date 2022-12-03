package cn.nukkit.level.terra.delegate;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import com.dfsek.terra.api.entity.EntityType;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public record PNXEntityType(String identifier) implements EntityType {
    @Override
    public String getHandle() {
        return identifier;
    }
}
