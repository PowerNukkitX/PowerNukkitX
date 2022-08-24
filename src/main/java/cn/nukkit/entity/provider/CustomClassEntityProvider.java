package cn.nukkit.entity.provider;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntityDefinition;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class CustomClassEntityProvider extends CustomEntityProvider implements EntityProviderWithClass {
    private final Class<? extends Entity> clazz;

    public CustomClassEntityProvider(CustomEntityDefinition customEntityDefinition, Class<? extends Entity> customEntityClass) {
        super(customEntityDefinition);
        this.clazz = customEntityClass;
    }

    public Class<? extends Entity> getEntityClass() {
        return clazz;
    }
}
