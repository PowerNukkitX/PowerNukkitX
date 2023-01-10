package cn.nukkit.entity.component.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.component.AbstractEntityComponent;

/**
 * 实体坐下组件
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EntitySittingComponent extends AbstractEntityComponent {

    protected boolean sitting;

    public EntitySittingComponent(Entity entity) {
        super(entity);
    }

    @Override
    public void onInitEntity() {
        if (entity.namedTag.contains("Sitting") && entity.namedTag.getBoolean("Sitting")) {
            setSitting(true);
        }
    }

    @Override
    public void onSaveNBT() {
        entity.namedTag.putBoolean("Sitting", sitting);
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SITTING, sitting);
    }
}
