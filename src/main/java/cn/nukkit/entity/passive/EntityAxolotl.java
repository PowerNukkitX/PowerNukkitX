package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityAxolotl extends EntityAnimal implements EntitySwimmable {
    

    public EntityAxolotl(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getHeight() {
        return 0.42f;
    }

    @Override
    public float getWidth() {
        return 0.75f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(14);
        super.initEntity();
    }


    @Override
    public String getOriginalName() {
        return "Axolotl";
    }
}
