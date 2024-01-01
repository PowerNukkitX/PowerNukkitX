package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityTadpole extends EntityAnimal implements EntitySwimmable {
    

    public EntityTadpole(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getHeight() {
        return 0.8f;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(6);
        super.initEntity();
    }

    @Override
    public String getOriginalName() {
        return "Tadpole";
    }
}
