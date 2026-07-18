package org.powernukkitx.entity.effect;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.entity.mob.EntityCreaking;
import org.powernukkitx.entity.mob.EntityEnderDragon;
import org.powernukkitx.entity.mob.EntityWither;
import org.powernukkitx.level.particle.GenericParticle;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.ParticleType;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;

import java.awt.*;

public class EffectWindCharged extends Effect {

    private static final double BURST_RADIUS = 2.5;
    private static final double KNOCKBACK_STRENGTH = 0.2;

    public EffectWindCharged() {
        super(EffectType.WIND_CHARGED, "%effect.windCharged", new Color(189, 201, 255), true);
    }

    @Override
    public boolean canBeApplied(Entity entity) {
        if (entity instanceof EntityWither || entity instanceof EntityEnderDragon) {
            return false;
        }
        return !(entity instanceof EntityCreaking creaking && creaking.getCreakingHeart() != null);
    }

    @Override
    public void onDeath(Entity entity) {
        var level = entity.getLevel();
        for (Entity nearby : level.getNearbyEntities(entity.getBoundingBox().grow(BURST_RADIUS, BURST_RADIUS, BURST_RADIUS), entity)) {
            if (!(nearby instanceof EntityLiving living) || living.distance(entity) > BURST_RADIUS) {
                continue;
            }
            Vector3 knockback = new Vector3(living.motionX / 2d, living.motionY / 2d, living.motionZ / 2d);
            knockback.x -= (entity.getX() - living.getX()) * KNOCKBACK_STRENGTH;
            knockback.y += 0.6;
            knockback.z -= (entity.getZ() - living.getZ()) * KNOCKBACK_STRENGTH;
            living.setMotion(knockback);
        }
        level.addLevelSoundEvent(entity.getPosition().add(0, 1), SoundEvent.WIND_CHARGE_BURST);
        level.addParticle(new GenericParticle(entity.getPosition(), ParticleType.WIND_EXPLOSION));
    }
}
