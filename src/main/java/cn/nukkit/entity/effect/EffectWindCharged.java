package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.math.Vector3;
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
