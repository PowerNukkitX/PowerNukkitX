package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author PikyCZ
 */
public class EntityEvoker extends EntityMob implements EntityWalkable {

    

    public EntityEvoker(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(24);
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
        return "Evoker";
    }


    @Override
    public boolean isPreventingSleep(Player player) {
        return true;
    }
}
