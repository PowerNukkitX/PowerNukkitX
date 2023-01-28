package cn.nukkit.entity.component;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import lombok.Getter;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public abstract class AbstractEntityComponent implements EntityComponent {

    @Getter
    protected Entity entity;

    public AbstractEntityComponent(Entity entity) {
        this.entity = entity;
    }
}
