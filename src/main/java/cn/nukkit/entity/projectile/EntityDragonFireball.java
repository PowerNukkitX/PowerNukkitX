package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.entity.item.EntityAreaEffectCloud;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class EntityDragonFireball extends EntityProjectile {

    @Override
    @NotNull public String getIdentifier() {
        return DRAGON_FIREBALL;
    }

    public EntityDragonFireball(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    @Override
    public float getHeight() {
        return 0.3125f;
    }

    @Override
    public float getWidth() {
        return 0.3125f;
    }

    @Override
    public float getLength() {
        return 0.3125f;
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
    public void onCollideWithEntity(Entity entity) {
        if(!(entity instanceof EntityEnderDragon)) onCollide();
    }

    @Override
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        onCollide();
    }

    protected void onCollide() {
        this.close();
        EntityAreaEffectCloud entity = (EntityAreaEffectCloud) Entity.createEntity(Entity.AREA_EFFECT_CLOUD, getChunk(),
                new CompoundTag().putList("Pos", new ListTag<>()
                                .add(new DoubleTag(x))
                                .add(new DoubleTag(y))
                                .add(new DoubleTag(z))
                        )
                        .putList("Rotation", new ListTag<>()
                                .add(new FloatTag(0))
                                .add(new FloatTag(0))
                        )
                        .putList("Motion", new ListTag<>()
                                .add(new DoubleTag(0))
                                .add(new DoubleTag(0))
                                .add(new DoubleTag(0))
                        )
                        .putInt("Duration", 2400)
                        .putFloat("InitialRadius", 6)
                        .putFloat("Radius", 6)
                        .putFloat("RadiusChangeOnPickup", 0)
                        .putFloat("RadiusPerTick", 0)
        );

        List<Effect> effects = PotionType.get(PotionType.HARMING_STRONG.id()).getEffects(false);
        for (Effect effect : effects) {
            if (effect != null && entity != null) {
                entity.cloudEffects.add(effect.setVisible(false).setAmbient(false));
                entity.spawnToAll();
            }
        }
        entity.spawnToAll();
    }

    @Override
    public String getOriginalName() {
        return "Dragon FireBall";
    }
}
