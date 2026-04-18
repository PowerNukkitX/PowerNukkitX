package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.PotionApplicationMode;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.potion.PotionCollideEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.PotionSplashParticle;
import cn.nukkit.utils.BlockColor;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

/**
 * @author xtypr
 */
public class EntitySplashPotion extends EntityProjectile {

    @Override
    @NotNull
    public String getIdentifier() {
        return SPLASH_POTION;
    }

    public static final int DATA_POTION_ID = 37;

    public int potionId;

    public EntitySplashPotion(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    public EntitySplashPotion(IChunk chunk, NbtMap nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        potionId = this.namedTag.getShort("PotionId");

        this.entityDataMap.put(ActorDataTypes.AUX_VALUE_DATA, this.potionId);

        /*Effect effect = Potion.getEffect(potionId, true); TODO: potion color

        if(effect != null) {
            int count = 0;
            int[] c = effect.getColor();
            count += effect.getAmplifier() + 1;

            int r = ((c[0] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int g = ((c[1] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int b = ((c[2] * (effect.getAmplifier() + 1)) / count) & 0xff;

            this.setDataProperty(new IntEntityData(Entity.DATA_UNKNOWN, (r << 16) + (g << 8) + b));
        }*/
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
        return 0.05f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        this.splash(entity);
    }

    protected void splash(Entity collidedWith) {
        PotionType potion = PotionType.get(this.potionId);
        PotionCollideEvent event = new PotionCollideEvent(potion, this);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        this.close();

        potion = event.getPotion();
        if (potion == null) {
            return;
        }

        if (potion.equals(PotionType.WATER)) {
            if (collidedWith instanceof EntityBlaze blaze) {
                blaze.attack(new EntityDamageByEntityEvent(this, blaze, EntityDamageEvent.DamageCause.MAGIC, 1));
            }
        }

        int[] color = new int[3];
        int count = 0;

        List<Effect> splashEffects = potion.getEffects(PotionApplicationMode.SPLASH);
        if (!splashEffects.isEmpty()) {
            for (Effect effect : splashEffects) {
                Color effectColor = effect.getColor();
                color[0] += effectColor.getRed() * effect.getLevel();
                color[1] += effectColor.getGreen() * effect.getLevel();
                color[2] += effectColor.getBlue() * effect.getLevel();
                count += effect.getLevel();
            }
        } else {
            BlockColor water = BlockColor.WATER_BLOCK_COLOR;
            color[0] = water.getRed();
            color[1] = water.getGreen();
            color[2] = water.getBlue();
            count = 1;
        }

        int r = (color[0] / count) & 0xff;
        int g = (color[1] / count) & 0xff;
        int b = (color[2] / count) & 0xff;
        Particle particle = new PotionSplashParticle(this, r, g, b);

        this.getLevel().addParticle(particle);
        this.getLevel().addSound(this, Sound.RANDOM_GLASS);

        Entity[] entities = this.getLevel().getNearbyEntities(this.getBoundingBox().grow(4.125, 2.125, 4.125));
        for (Entity anEntity : entities) {
            double distance = anEntity.distanceSquared(this);
            if (distance < 16) {
                double splashDistance = anEntity.equals(collidedWith) ? 1 : 1 - Math.sqrt(distance) / 4;
                potion.applyEffects(anEntity, PotionApplicationMode.SPLASH, splashDistance);
            }
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200) {
            this.kill();
            hasUpdate = true;
        } else if (this.isCollided) {
            this.splash(null);
            hasUpdate = true;
        }
        return hasUpdate;
    }

    @Override
    public String getOriginalName() {
        return "Potion";
    }
}
