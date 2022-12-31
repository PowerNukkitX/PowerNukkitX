package cn.nukkit.entity.component.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.component.AbstractEntityComponent;

/**
 * 实体生气组件
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class EntityAngryComponent extends AbstractEntityComponent {

    protected boolean angry;

    public EntityAngryComponent(Entity entity) {
        super(entity);
    }

    @Override
    public void onInitEntity() {
        if (entity.namedTag.getBoolean("Angry"))
            setAngry(true);
    }

    @Override
    public void onSaveNBT() {
        entity.namedTag.putBoolean("Angry", this.angry);
    }

    public boolean isAngry() {
        return this.angry;
    }

    public void setAngry(boolean angry) {
        this.angry = angry;
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_ANGRY, angry);
    }
}
