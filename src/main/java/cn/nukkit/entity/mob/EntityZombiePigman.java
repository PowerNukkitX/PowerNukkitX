package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityZombiePigman extends EntityMob implements EntityWalkable, EntitySmite {

    

    public EntityZombiePigman(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }


    @Override
    public String getOriginalName() {
        return "Zombified Piglin";
    }


    @Override
    public boolean isUndead() {
        return true;
    }


    @Override
    public boolean isPreventingSleep(Player player) {
        return this.getDataPropertyBoolean(DATA_FLAG_ANGRY);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        burn(this);
        return super.onUpdate(currentTick);
    }
}
