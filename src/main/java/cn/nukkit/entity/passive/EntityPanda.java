package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityPanda extends EntityAnimal implements EntityWalkable {

    

    public EntityPanda(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getWidth() {
        return 1.7f;
    }

    @Override
    public float getHeight() {
        return 1.5f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }


    @Override
    public String getOriginalName() {
        return "Panda";
    }
}
