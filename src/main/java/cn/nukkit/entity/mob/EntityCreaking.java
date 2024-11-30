package cn.nukkit.entity.mob;

import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class EntityCreaking extends EntityMob {
    @Override @NotNull public String getIdentifier() {
        return CREAKING;
    }

    public EntityCreaking(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(1);
        super.initEntity();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
            return super.attack(source);
        return !source.isCancelled();
    }

    @Override
    public float getHeight() {
        return 2.5F;
    }

    @Override
    public float getWidth() {
        return 1F;
    }
}
