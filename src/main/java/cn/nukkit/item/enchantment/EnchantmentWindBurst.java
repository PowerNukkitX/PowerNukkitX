package cn.nukkit.item.enchantment;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;

public class EnchantmentWindBurst extends Enchantment {

    protected EnchantmentWindBurst() {
        super(ID_WIND_BURST, NAME_WIND_BURST, Rarity.RARE, EnchantmentType.MACE);
        this.setObtainableFromEnchantingTable(false);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 + (level - 1) * 20;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void doAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        double fallDistance = Math.max(damager.fallDistance, damager.highestPosition - damager.y);
        if (fallDistance < 1.5d && damager.motionY < -0.08d) {
            fallDistance = Math.max(fallDistance, Math.min(2.5d, -damager.motionY * 4d));
        }

        if (fallDistance < 1.5d) {
            return;
        }

        int level = this.getLevel();
        double clampedFall = Math.min(fallDistance, 7.5d);

        double verticalBoost = 0.72d + clampedFall * 0.10d + switch (level) {
            case 2 -> 0.55d;
            case 3 -> 1.3d;
            default -> 0;
        };

        double yaw = Math.toRadians(damager.yaw);
        Vector3 forward = new Vector3(-Math.sin(yaw), 0, Math.cos(yaw)).normalize();

        double forwardBoost = 0.08d + 0.02d * level;
        Vector3 motion = forward.multiply(forwardBoost);
        motion.y = verticalBoost;

        damager.resetFallDistance();
        damager.setMotion(motion);

        if (damager.level != null) {
            AxisAlignedBB box = damager.boundingBox.grow(2.5d, 2.5d, 2.5d);
            for (Entity entity : damager.level.getNearbyEntities(box, damager)) {
                if (entity == damager || entity.closed) {
                    continue;
                }

                Vector3 direction = entity.subtract(damager).normalize();
                if (direction.lengthSquared() <= 0) {
                    continue;
                }

                Vector3 gust = direction.multiply(0.6d + 0.15d * level);
                Vector3 targetMotion = entity.getMotion().add(gust);
                targetMotion.y = Math.max(targetMotion.y, 0.4d + 0.1d * level);
                entity.setMotion(targetMotion);

                entity.level.addParticle(new GenericParticle(entity.add(0, entity.getEyeHeight() * 0.6, 0), Particle.TYPE_WIND_EXPLOSION));
            }
        }

        if (damager.level != null) {
            damager.level.addParticle(new GenericParticle(damager.add(0, damager.getEyeHeight() * 0.6, 0), Particle.TYPE_WIND_EXPLOSION));
            damager.level.addSound(damager, Sound.MACE_SMASH_AIR);
        }
    }
}