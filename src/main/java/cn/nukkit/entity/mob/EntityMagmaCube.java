package cn.nukkit.entity.mob;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author PikyCZ
 */
public class EntityMagmaCube extends EntityMob implements EntityWalkable {

    @Override
    @NotNull public String getIdentifier() {
        return MAGMA_CUBE;
    }

    public EntityMagmaCube(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(16);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 2.04f;
    }

    @Override
    public float getHeight() {
        return 2.04f;
    }

    @Override
    public int getFrostbiteInjury() {
        return 5;
    }

    @Override
    public String getOriginalName() {
        return "Magma Cube";
    }
}
