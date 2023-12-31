package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Erik Miller | EinBexiii
 */


public class EntityHoglin extends EntityMob implements EntityWalkable, EntityAgeable {

    public final static int NETWORK_ID = 124;

    public EntityHoglin(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(40);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 0.9f;
    }


    @Override
    public String getOriginalName() {
        return "Hoglin";
    }
}
