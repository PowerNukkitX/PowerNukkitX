package cn.nukkit.entity.mob;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author joserobjr
 * @since 2020-11-20
 */


public class EntityPiglinBrute extends EntityMob implements EntityWalkable {


    @Override
    @NotNull public String getIdentifier() {
        return PIGLIN_BRUTE;
    }

    public EntityPiglinBrute(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    

    @Override
    protected void initEntity() {
        this.setMaxHealth(50);
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
    public boolean isPreventingSleep(Player player) {
        return true;
    }

    @Override
    public String getOriginalName() {
        return "Piglin Brute";
    }
}
