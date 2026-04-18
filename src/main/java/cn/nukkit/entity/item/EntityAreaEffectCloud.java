package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.PotionApplicationMode;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.IChunk;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class EntityAreaEffectCloud extends Entity {

    @Override
    @NotNull
    public String getIdentifier() {
        return AREA_EFFECT_CLOUD;
    }

    public List<Effect> cloudEffects;

    protected int reapplicationDelay;

    protected int durationOnUse;

    protected float initialRadius;

    protected float radiusOnUse;

    protected float height;

    protected int nextApply;
    private int lastAge;

    public EntityAreaEffectCloud(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    public int getWaitTime() {
        return this.getDataProperty(ActorDataTypes.DATA_WAITING, 0);
    }

    public void setWaitTime(int waitTime) {
        setWaitTime(waitTime, true);
    }

    public void setWaitTime(int waitTime, boolean send) {
        this.setDataProperty(ActorDataTypes.DATA_WAITING, waitTime, send);
    }

    public int getPotionId() {
        return this.getDataProperty(ActorDataTypes.AUX_VALUE_DATA);
    }

    public void setPotionId(int potionId) {
        setPotionId(potionId, true);
    }

    public void setPotionId(int potionId, boolean send) {
        this.setDataProperty(ActorDataTypes.AUX_VALUE_DATA, potionId & 0xFFFF, send);
    }

    public void recalculatePotionColor() {
        recalculatePotionColor(true);
    }

    public void recalculatePotionColor(boolean send) {
        int[] color = new int[4];
        int count = 0;

        if (namedTag.containsKey("ParticleColor")) {
            int effectColor = namedTag.getInt("ParticleColor");
            color[0] = (effectColor & 0xFF000000) >> 24;
            color[1] = (effectColor & 0x00FF0000) >> 16;
            color[2] = (effectColor & 0x0000FF00) >> 8;
            color[3] = effectColor & 0x000000FF;
            count = 1;
        } else {
            color[0] = 255;

            PotionType potion = PotionType.get(getPotionId());
            for (Effect effect : potion.getEffects(PotionApplicationMode.SPLASH)) {
                Color effectColor = effect.getColor();
                color[1] += effectColor.getRed() * effect.getLevel();
                color[2] += effectColor.getGreen() * effect.getLevel();
                color[3] += effectColor.getBlue() * effect.getLevel();
                count += effect.getLevel();
            }
        }

        if (count <= 0) {
            setPotionColor(0xFF385DC6, send);
            return;
        }

        int a = (color[0] / count) & 0xff;
        int r = (color[1] / count) & 0xff;
        int g = (color[2] / count) & 0xff;
        int b = (color[3] / count) & 0xff;

        setPotionColor(a, r, g, b, send);
    }

    public int getPotionColor() {
        return this.getDataProperty(ActorDataTypes.EFFECT_COLOR);
    }

    public void setPotionColor(int argp) {
        setPotionColor(argp, true);
    }

    public void setPotionColor(int alpha, int red, int green, int blue, boolean send) {
        setPotionColor(((alpha & 0xff) << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff), send);
    }

    public void setPotionColor(int argp, boolean send) {
        this.setDataProperty(ActorDataTypes.EFFECT_COLOR, argp, send);
    }

    public int getPickupCount() {
        return this.getDataProperty(ActorDataTypes.DATA_PICKUP_COUNT);
    }

    public void setPickupCount(int pickupCount) {
        setPickupCount(pickupCount, true);
    }

    public void setPickupCount(int pickupCount, boolean send) {
        this.setDataProperty(ActorDataTypes.DATA_PICKUP_COUNT, pickupCount, send);
    }

    public float getRadiusChangeOnPickup() {
        return this.getDataProperty(ActorDataTypes.DATA_CHANGE_ON_PICKUP);
    }

    public void setRadiusChangeOnPickup(float radiusChangeOnPickup) {
        setRadiusChangeOnPickup(radiusChangeOnPickup, true);
    }

    public void setRadiusChangeOnPickup(float radiusChangeOnPickup, boolean send) {
        this.setDataProperty(ActorDataTypes.DATA_CHANGE_ON_PICKUP, radiusChangeOnPickup, send);
    }

    public float getRadiusPerTick() {
        return this.getDataProperty(ActorDataTypes.DATA_CHANGE_RATE);
    }

    public void setRadiusPerTick(float radiusPerTick) {
        setRadiusPerTick(radiusPerTick, true);
    }

    public void setRadiusPerTick(float radiusPerTick, boolean send) {
        this.setDataProperty(ActorDataTypes.DATA_CHANGE_RATE, radiusPerTick, send);
    }

    public long getSpawnTime() {
        return this.getDataProperty(ActorDataTypes.DATA_SPAWN_TIME_deprecated);
    }

    public void setSpawnTime(long spawnTime) {
        setSpawnTime(spawnTime, true);
    }

    public void setSpawnTime(long spawnTime, boolean send) {
        this.setDataProperty(ActorDataTypes.DATA_SPAWN_TIME_deprecated, spawnTime, send);
    }

    public int getDuration() {
        return this.getDataProperty(ActorDataTypes.DATA_DURATION);
    }

    public void setDuration(int duration) {
        setDuration(duration, true);
    }

    public void setDuration(int duration, boolean send) {
        this.setDataProperty(ActorDataTypes.DATA_DURATION, duration, send);
    }

    public float getRadius() {
        return this.getDataProperty(ActorDataTypes.DATA_RADIUS);
    }

    public void setRadius(float radius) {
        setRadius(radius, true);
    }

    public void setRadius(float radius, boolean send) {
        this.setDataProperty(ActorDataTypes.DATA_RADIUS, radius, send);
    }

    public void setHeight(float height) {
        this.height = height;
    }


    public ParticleType getParticleId() {
        return this.getDataProperty(ActorDataTypes.DATA_PARTICLE);
    }

    public void setParticleId(int particleId) {
        setParticleId(particleId, true);
    }

    public void setParticleId(int particleId, boolean send) {
        this.setDataProperty(ActorDataTypes.DATA_PARTICLE, particleId, send);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.invulnerable = true;
        this.setDataFlag(ActorFlags.FIRE_IMMUNE, true);
        this.setDataFlag(ActorFlags.NO_AI, true);
        this.setSpawnTime(this.level.getCurrentTick(), false);
        this.setPickupCount(0, false);

        cloudEffects = new ArrayList<>(1);
        for (NbtMap effectTag : namedTag.getList("mobEffects", NbtType.COMPOUND)) {
            Effect effect = Effect.get(effectTag.getByte("Id"))
                    .setAmbient(effectTag.getBoolean("Ambient"))
                    .setAmplifier(effectTag.getByte("Amplifier"))
                    .setVisible(effectTag.getBoolean("DisplayOnScreenTextureAnimation"))
                    .setDuration(effectTag.getInt("Duration"));
            cloudEffects.add(effect);
        }
        if (namedTag.containsKey("PotionId")) {
            this.setParticleId(32, false);
            int displayedPotionId = namedTag.getShort("PotionId");
            setPotionId(displayedPotionId, false);
            recalculatePotionColor();
        } else {
            setDragonBreath();
        }

        if (namedTag.containsKey("Duration")) {
            setDuration(namedTag.getInt("Duration"), false);
        } else {
            setDuration(600, false);
        }
        if (namedTag.containsKey("DurationOnUse")) {
            durationOnUse = namedTag.getInt("DurationOnUse");
        } else {
            durationOnUse = 0;
        }
        if (namedTag.containsKey("ReapplicationDelay")) {
            reapplicationDelay = namedTag.getInt("ReapplicationDelay");
        } else {
            reapplicationDelay = 0;
        }
        if (namedTag.containsKey("InitialRadius")) {
            initialRadius = namedTag.getFloat("InitialRadius");
        } else {
            initialRadius = 3.0F;
        }
        if (namedTag.containsKey("Radius")) {
            setRadius(namedTag.getFloat("Radius"), false);
        } else {
            setRadius(initialRadius, false);
        }
        if (namedTag.containsKey("RadiusChangeOnPickup")) {
            setRadiusChangeOnPickup(namedTag.getFloat("RadiusChangeOnPickup"), false);
        } else {
            setRadiusChangeOnPickup(-0.5F, false);
        }
        if (namedTag.containsKey("RadiusOnUse")) {
            radiusOnUse = namedTag.getFloat("RadiusOnUse");
        } else {
            radiusOnUse = -0.5F;
        }
        if (namedTag.containsKey("RadiusPerTick")) {
            setRadiusPerTick(namedTag.getFloat("RadiusPerTick"), false);
        } else {
            setRadiusPerTick(-0.005F, false);
        }
        if (namedTag.containsKey("WaitTime")) {
            setWaitTime(namedTag.getInt("WaitTime"), false);
        } else {
            setWaitTime(10, false);
        }
        if (namedTag.containsKey("Height")) {
            setHeight(namedTag.getFloat("Height"));
        } else {
            setHeight(0.3F + (getRadius() / 2F));
        }

        setHealthMax(1);
        setHealthCurrent(1);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        List<NbtMap> effectsTag = new ObjectArrayList<>();
        for (Effect effect : cloudEffects) {
            effectsTag.add(NbtMap.builder().putByte("Id", effect.getId().byteValue())
                    .putBoolean("Ambient", effect.isAmbient())
                    .putByte("Amplifier", (byte) effect.getAmplifier())
                    .putBoolean("DisplayOnScreenTextureAnimation", effect.isVisible())
                    .putInt("Duration", effect.getDuration())
                    .build()
            );
        }
        this.namedTag = this.namedTag.toBuilder()
                .putList("mobEffects", NbtType.COMPOUND, effectsTag)
                .putInt("ParticleColor", getPotionColor())
                .putInt("Duration", getDuration())
                .putInt("DurationOnUse", durationOnUse)
                .putInt("ReapplicationDelay", reapplicationDelay)
                .putFloat("Radius", getRadius())
                .putFloat("RadiusChangeOnPickup", getRadiusChangeOnPickup())
                .putFloat("RadiusOnUse", radiusOnUse)
                .putFloat("RadiusPerTick", getRadiusPerTick())
                .putInt("WaitTime", getWaitTime())
                .putFloat("InitialRadius", initialRadius)
                .putInt("PotionId", getPotionId())
                .build();
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        super.onUpdate(currentTick);

        boolean sendRadius = age % 10 == 0;

        int age = this.age;
        float radius = getRadius();
        int waitTime = getWaitTime();
        if (age < waitTime) {
            radius = initialRadius;
        } else if (age > waitTime + getDuration()) {
            kill();
        } else {
            int tickDiff = age - lastAge;
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

        float height = getHeight();
        boundingBox.setBounds(x - radius, y - height, z - radius, x + radius, y + height, z + radius);
        this.setDataProperty(ActorDataTypes.HEIGHT, height, false);
        this.setDataProperty(ActorDataTypes.WIDTH, radius, false);

        return true;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity instanceof EntityLiving;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getWidth() {
        return getRadius();
    }

    @Override
    public float getLength() {
        return getRadius();
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Override
    protected float getDrag() {
        return 0;
    }

    @Override
    public String getOriginalName() {
        return "Area Effect Cloud";
    }

    public void setDragonBreath() {
        setParticleId(49, false);
        setPotionColor(-5675670, false);
    }

    public boolean isDragonBreath() {
        return getParticleId().equals(ParticleType.DRAGON_BREATH);
    }

}
