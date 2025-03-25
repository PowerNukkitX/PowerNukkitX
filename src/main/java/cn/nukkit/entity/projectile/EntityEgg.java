package cn.nukkit.entity.projectile;

import cn.nukkit.entity.ClimateVariant;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.ItemEgg;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityEgg extends EntityProjectile implements ClimateVariant {

    @Override
    @NotNull public String getIdentifier() {
        return EGG;
    }

    public EntityEgg(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityEgg(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if(namedTag.containsString("variant")) {
            setVariant(Variant.get(namedTag.getString("variant")));
        } else setVariant(Variant.TEMPERATE);
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.03f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    protected void addHitEffect() {
        int particles = ThreadLocalRandom.current().nextInt(10) + 5;
        ItemEgg egg = new ItemEgg();
        for (int i = 0; i < particles; i++) {
            level.addParticle(new ItemBreakParticle(this, egg));
        }
    }

    @Override
    public String getOriginalName() {
        return "Egg";
    }
}
