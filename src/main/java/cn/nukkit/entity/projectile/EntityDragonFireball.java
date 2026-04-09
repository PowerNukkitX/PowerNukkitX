package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.PotionApplicationMode;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.entity.item.EntityAreaEffectCloud;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


public class EntityDragonFireball extends EntityProjectile {

    @Override
    @NotNull
    public String getIdentifier() {
        return DRAGON_FIREBALL;
    }

    public EntityDragonFireball(IChunk chunk, NbtMap nbt) {
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
        if (!(entity instanceof EntityEnderDragon)) onCollide();
    }

    @Override
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        onCollide();
    }

    protected void onCollide() {
        this.close();
        EntityAreaEffectCloud entity = (EntityAreaEffectCloud) Entity.createEntity(Entity.AREA_EFFECT_CLOUD, getChunk(),
                NbtMap.builder()
                        .putList("Pos", NbtType.DOUBLE, Arrays.asList(x, y, z))
                        .putList("Motion", NbtType.DOUBLE, Arrays.asList(0.0, 0.0, 0.0))
                        .putList("Rotation", NbtType.FLOAT, Arrays.asList(0f, 0f))
                        .putInt("Duration", 2400)
                        .putFloat("InitialRadius", 6)
                        .putFloat("Radius", 6)
                        .putFloat("RadiusChangeOnPickup", 0)
                        .putFloat("RadiusPerTick", 0)
                        .build()
        );

        List<Effect> effects = PotionType.get(PotionType.HARMING_STRONG.id()).getEffects(PotionApplicationMode.DRINK);
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
