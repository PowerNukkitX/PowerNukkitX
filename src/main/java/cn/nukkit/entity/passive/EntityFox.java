package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kaooot
 * @since 2020-08-14
 */

public class EntityFox extends EntityAnimal implements EntityWalkable {

    @Override
    @NotNull public String getIdentifier() {
        return FOX;
    }
    


    public EntityFox(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.7f;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();
    }

    @Override
    public String getOriginalName() {
        return "Fox";
    }
}
