package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.potion.PotionCollideEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.SpellParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.PotionType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author xtypr
 */
public class EntitySplashPotion extends EntityProjectile {

    @Override
    @NotNull public String getIdentifier() {
        return SPLASH_POTION;
    }

    public static final int DATA_POTION_ID = 37;

    public int potionId;

    public EntitySplashPotion(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public EntitySplashPotion(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        potionId = this.namedTag.getShort("PotionId");

        this.entityDataMap.put(AUX_VALUE_DATA, this.potionId);

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

        int[] color = new int[3];
        int count = 0;

        for (Effect effect : potion.getEffects(true)) {
            Color effectColor = effect.getColor();
            color[0] += effectColor.getRed() * effect.getLevel();
            color[1] += effectColor.getGreen() * effect.getLevel();
            color[2] += effectColor.getBlue() * effect.getLevel();
            count += effect.getLevel();
        }

        int r = (color[0] / count) & 0xff;
        int g = (color[1] / count) & 0xff;
        int b = (color[2] / count) & 0xff;
        Particle particle = new SpellParticle(this, r, g, b);

        this.getLevel().addParticle(particle);
        this.getLevel().addSound(this, Sound.RANDOM_GLASS);

        Entity[] entities = this.getLevel().getNearbyEntities(this.getBoundingBox().grow(4.125, 2.125, 4.125));
        for (Entity anEntity : entities) {
            double distance = anEntity.distanceSquared(this);
            if (distance < 16) {
                double splashDistance = anEntity.equals(collidedWith) ? 1 : 1 - Math.sqrt(distance) / 4;
                potion.applyEffects(anEntity, true, splashDistance);
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
