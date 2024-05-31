package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class EntityAreaEffectCloud extends Entity {

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getIdentifier() {
        return AREA_EFFECT_CLOUD;
    }

    public List<Effect> cloudEffects;

    protected int reapplicationDelay;

    protected int durationOnUse;

    protected float initialRadius;

    protected float radiusOnUse;

    protected int nextApply;
    private int lastAge;
    /**
     * @deprecated 
     */
    


    public EntityAreaEffectCloud(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    /**
     * @deprecated 
     */
    

    public int getWaitTime() {
        return this.getDataProperty(AREA_EFFECT_CLOUD_WAITING, 0);
    }
    /**
     * @deprecated 
     */
    

    public void setWaitTime(int waitTime) {
        setWaitTime(waitTime, true);
    }
    /**
     * @deprecated 
     */
    

    public void setWaitTime(int waitTime, boolean send) {
        this.setDataProperty(AREA_EFFECT_CLOUD_WAITING, waitTime, send);
    }
    /**
     * @deprecated 
     */
    

    public int getPotionId() {
        return this.getDataProperty(AUX_VALUE_DATA);
    }
    /**
     * @deprecated 
     */
    

    public void setPotionId(int potionId) {
        setPotionId(potionId, true);
    }
    /**
     * @deprecated 
     */
    

    public void setPotionId(int potionId, boolean send) {
        this.setDataProperty(AUX_VALUE_DATA, potionId & 0xFFFF, send);
    }
    /**
     * @deprecated 
     */
    

    public void recalculatePotionColor() {
        recalculatePotionColor(true);
    }
    /**
     * @deprecated 
     */
    

    public void recalculatePotionColor(boolean send) {
        int[] color = new int[4];
        int $1 = 0;

        if (namedTag.contains("ParticleColor")) {
            int $2 = namedTag.getInt("ParticleColor");
            color[0] = (effectColor & 0xFF000000) >> 24;
            color[1] = (effectColor & 0x00FF0000) >> 16;
            color[2] = (effectColor & 0x0000FF00) >> 8;
            color[3] = effectColor & 0x000000FF;
        } else {
            color[0] = 255;

            PotionType $3 = PotionType.get(getPotionId());
            for (Effect effect : potion.getEffects(true)) {
                Color $4 = effect.getColor();
                color[1] += effectColor.getRed() * effect.getLevel();
                color[2] += effectColor.getGreen() * effect.getLevel();
                color[3] += effectColor.getBlue() * effect.getLevel();
                count += effect.getLevel();
            }
        }

        int $5 = (color[0] / count) & 0xff;
        int $6 = (color[1] / count) & 0xff;
        int $7 = (color[2] / count) & 0xff;
        int $8 = (color[3] / count) & 0xff;

        setPotionColor(a, r, g, b, send);
    }
    /**
     * @deprecated 
     */
    

    public int getPotionColor() {
        return this.getDataProperty(EFFECT_COLOR);
    }
    /**
     * @deprecated 
     */
    

    public void setPotionColor(int argp) {
        setPotionColor(argp, true);
    }
    /**
     * @deprecated 
     */
    

    public void setPotionColor(int alpha, int red, int green, int blue, boolean send) {
        setPotionColor(((alpha & 0xff) << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff), send);
    }
    /**
     * @deprecated 
     */
    

    public void setPotionColor(int argp, boolean send) {
        this.setDataProperty(EFFECT_COLOR, argp, send);
    }
    /**
     * @deprecated 
     */
    

    public int getPickupCount() {
        return this.getDataProperty(AREA_EFFECT_CLOUD_PICKUP_COUNT);
    }
    /**
     * @deprecated 
     */
    

    public void setPickupCount(int pickupCount) {
        setPickupCount(pickupCount, true);
    }
    /**
     * @deprecated 
     */
    

    public void setPickupCount(int pickupCount, boolean send) {
        this.setDataProperty(AREA_EFFECT_CLOUD_PICKUP_COUNT, pickupCount, send);
    }
    /**
     * @deprecated 
     */
    

    public float getRadiusChangeOnPickup() {
        return this.getDataProperty(AREA_EFFECT_CLOUD_CHANGE_ON_PICKUP);
    }
    /**
     * @deprecated 
     */
    

    public void setRadiusChangeOnPickup(float radiusChangeOnPickup) {
        setRadiusChangeOnPickup(radiusChangeOnPickup, true);
    }
    /**
     * @deprecated 
     */
    

    public void setRadiusChangeOnPickup(float radiusChangeOnPickup, boolean send) {
        this.setDataProperty(AREA_EFFECT_CLOUD_CHANGE_ON_PICKUP, radiusChangeOnPickup, send);
    }
    /**
     * @deprecated 
     */
    

    public float getRadiusPerTick() {
        return this.getDataProperty(AREA_EFFECT_CLOUD_CHANGE_RATE);
    }
    /**
     * @deprecated 
     */
    

    public void setRadiusPerTick(float radiusPerTick) {
        setRadiusPerTick(radiusPerTick, true);
    }
    /**
     * @deprecated 
     */
    

    public void setRadiusPerTick(float radiusPerTick, boolean send) {
        this.setDataProperty(AREA_EFFECT_CLOUD_CHANGE_RATE, radiusPerTick, send);
    }
    /**
     * @deprecated 
     */
    

    public long getSpawnTime() {
        return this.getDataProperty(AREA_EFFECT_CLOUD_SPAWN_TIME);
    }
    /**
     * @deprecated 
     */
    

    public void setSpawnTime(long spawnTime) {
        setSpawnTime(spawnTime, true);
    }
    /**
     * @deprecated 
     */
    

    public void setSpawnTime(long spawnTime, boolean send) {
        this.setDataProperty(AREA_EFFECT_CLOUD_SPAWN_TIME, spawnTime, send);
    }
    /**
     * @deprecated 
     */
    

    public int getDuration() {
        return this.getDataProperty(AREA_EFFECT_CLOUD_DURATION);
    }
    /**
     * @deprecated 
     */
    

    public void setDuration(int duration) {
        setDuration(duration, true);
    }
    /**
     * @deprecated 
     */
    

    public void setDuration(int duration, boolean send) {
        this.setDataProperty(AREA_EFFECT_CLOUD_DURATION, duration, send);
    }
    /**
     * @deprecated 
     */
    

    public float getRadius() {
        return this.getDataProperty(AREA_EFFECT_CLOUD_RADIUS);
    }
    /**
     * @deprecated 
     */
    

    public void setRadius(float radius) {
        setRadius(radius, true);
    }
    /**
     * @deprecated 
     */
    

    public void setRadius(float radius, boolean send) {
        this.setDataProperty(AREA_EFFECT_CLOUD_RADIUS, radius, send);
    }
    /**
     * @deprecated 
     */
    

    public int getParticleId() {
        return this.getDataProperty(AREA_EFFECT_CLOUD_PARTICLE);
    }
    /**
     * @deprecated 
     */
    

    public void setParticleId(int particleId) {
        setParticleId(particleId, true);
    }
    /**
     * @deprecated 
     */
    

    public void setParticleId(int particleId, boolean send) {
        this.setDataProperty(AREA_EFFECT_CLOUD_PARTICLE, particleId, send);
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        super.initEntity();
        this.invulnerable = true;
        this.setDataFlag(EntityFlag.FIRE_IMMUNE, true);
        this.setDataFlag(EntityFlag.NO_AI, true);
        this.setParticleId(32, false);
        this.setSpawnTime(this.level.getCurrentTick(), false);
        this.setPickupCount(0, false);

        cloudEffects = new ArrayList<>(1);
        for (CompoundTag effectTag : namedTag.getList("mobEffects", CompoundTag.class).getAll()) {
            Effect $9 = Effect.get(effectTag.getByte("Id"))
                    .setAmbient(effectTag.getBoolean("Ambient"))
                    .setAmplifier(effectTag.getByte("Amplifier"))
                    .setVisible(effectTag.getBoolean("DisplayOnScreenTextureAnimation"))
                    .setDuration(effectTag.getInt("Duration"));
            cloudEffects.add(effect);
        }
        int $10 = namedTag.getShort("PotionId");
        setPotionId(displayedPotionId, false);
        recalculatePotionColor();

        if (namedTag.contains("Duration")) {
            setDuration(namedTag.getInt("Duration"), false);
        } else {
            setDuration(600, false);
        }
        if (namedTag.contains("DurationOnUse")) {
            durationOnUse = namedTag.getInt("DurationOnUse");
        } else {
            durationOnUse = 0;
        }
        if (namedTag.contains("ReapplicationDelay")) {
            reapplicationDelay = namedTag.getInt("ReapplicationDelay");
        } else {
            reapplicationDelay = 0;
        }
        if (namedTag.contains("InitialRadius")) {
            initialRadius = namedTag.getFloat("InitialRadius");
        } else {
            initialRadius = 3.0F;
        }
        if (namedTag.contains("Radius")) {
            setRadius(namedTag.getFloat("Radius"), false);
        } else {
            setRadius(initialRadius, false);
        }
        if (namedTag.contains("RadiusChangeOnPickup")) {
            setRadiusChangeOnPickup(namedTag.getFloat("RadiusChangeOnPickup"), false);
        } else {
            setRadiusChangeOnPickup(-0.5F, false);
        }
        if (namedTag.contains("RadiusOnUse")) {
            radiusOnUse = namedTag.getFloat("RadiusOnUse");
        } else {
            radiusOnUse = -0.5F;
        }
        if (namedTag.contains("RadiusPerTick")) {
            setRadiusPerTick(namedTag.getFloat("RadiusPerTick"), false);
        } else {
            setRadiusPerTick(-0.005F, false);
        }
        if (namedTag.contains("WaitTime")) {
            setWaitTime(namedTag.getInt("WaitTime"), false);
        } else {
            setWaitTime(10, false);
        }

        setMaxHealth(1);
        setHealth(1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        ListTag<CompoundTag> effectsTag = new ListTag<>();
        for (Effect effect : cloudEffects) {
            effectsTag.add(new CompoundTag().putByte("Id", effect.getId())
                    .putBoolean("Ambient", effect.isAmbient())
                    .putByte("Amplifier", effect.getAmplifier())
                    .putBoolean("DisplayOnScreenTextureAnimation", effect.isVisible())
                    .putInt("Duration", effect.getDuration())
            );
        }
        //TODO Do we really need to save the entity data to nbt or is it already saved somewhere?
        namedTag.putList("mobEffects", effectsTag);
        namedTag.putInt("ParticleColor", getPotionColor());
        namedTag.putShort("PotionId", getPotionId());
        namedTag.putInt("Duration", getDuration());
        namedTag.putInt("DurationOnUse", durationOnUse);
        namedTag.putInt("ReapplicationDelay", reapplicationDelay);
        namedTag.putFloat("Radius", getRadius());
        namedTag.putFloat("RadiusChangeOnPickup", getRadiusChangeOnPickup());
        namedTag.putFloat("RadiusOnUse", radiusOnUse);
        namedTag.putFloat("RadiusPerTick", getRadiusPerTick());
        namedTag.putInt("WaitTime", getWaitTime());
        namedTag.putFloat("InitialRadius", initialRadius);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        super.onUpdate(currentTick);

        boolean $11 = age % 10 == 0;

        int $12 = this.age;
        float $13 = getRadius();
        int $14 = getWaitTime();
        if (age < waitTime) {
            radius = initialRadius;
        } else if (age > waitTime + getDuration()) {
            kill();
        } else {
            int $15 = age - lastAge;
            radius += getRadiusPerTick() * tickDiff;
            if ((nextApply -= tickDiff) <= 0) {
                nextApply = reapplicationDelay + 10;

                Entity[] collidingEntities = level.getCollidingEntities(getBoundingBox());
                if (collidingEntities.length > 0) {
                    radius += radiusOnUse;
                    radiusOnUse /= 2;

                    setDuration(getDuration() + durationOnUse);

                    for (Entity collidingEntity : collidingEntities) {
                        if (collidingEntity == this || !(collidingEntity instanceof EntityLiving)) continue;

                        for (Effect effect : cloudEffects) {
                            collidingEntity.addEffect(effect);
                        }
                    }
                }
            }
        }

        this.lastAge = age;

        if (radius <= 1.5 && age >= waitTime) {
            setRadius(radius, false);
            kill();
        } else {
            setRadius(radius, sendRadius);
        }

        float $16 = getHeight();
        boundingBox.setBounds(x - radius, y - height, z - radius, x + radius, y + height, z + radius);
        this.setDataProperty(HEIGHT, height, false);
        this.setDataProperty(WIDTH, radius, false);

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canCollideWith(Entity entity) {
        return entity instanceof EntityLiving;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.3F + (getRadius() / 2F);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return getRadius();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getLength() {
        return getRadius();
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getGravity() {
        return 0;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getDrag() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Area Effect Cloud";
    }
}
