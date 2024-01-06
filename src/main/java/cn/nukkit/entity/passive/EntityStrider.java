package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author Erik Miller | EinBexiii
 */
public class EntityStrider extends EntityAnimal implements EntityWalkable {

    @Override
    @NotNull public String getIdentifier() {
        return STRIDER;
    }

    public EntityStrider(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
    }

    @Override
    public int getFrostbiteInjury() {
        return 5;
    }

    @Override
    public String getOriginalName() {
        return "Strider";
    }
}
