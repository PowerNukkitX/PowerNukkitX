package org.powernukkitx.block;

import org.powernukkitx.AdventureSettings;
import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockHoneyBlock extends BlockSolid {
    private static final Random RANDOM = new Random();

    public static final BlockProperties PROPERTIES = new BlockProperties(HONEY_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHoneyBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHoneyBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Honey Block";
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!entity.onGround && entity.motionY <= 0.08 &&
                (!(entity instanceof Player)
                        || !((Player) entity).getAdventureSettings().get(AdventureSettings.Type.FLYING))) {
            double ex = Math.abs(x + 0.5D - entity.x);
            double ez = Math.abs(z + 0.5D - entity.z);
            double width = 0.4375D + (double)(entity.getWidth() / 2.0F);
            if (ex + 1.0E-3D > width || ez + 1.0E-3D > width) {
                Vector3 motion = entity.getMotion();
                motion.y = -0.05;
                if (entity.motionY < -0.13) {
                    double m = -0.05 / entity.motionY;
                    motion.x *= m;
                    motion.z *= m;
                }

                if (!entity.getMotion().equals(motion)) {
                    entity.setMotion(motion);
                }
                entity.resetFallDistance();

                if (RANDOM.nextInt(10) == 0) {
                    level.addSound(entity, Sound.LAND_HONEY_BLOCK);
                }
            }
        }
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x+1, y+1, z+1);
    }

    @Override
    public double getMinX() {
        return x + 0.1;
    }

    @Override
    public double getMaxX() {
        return x + 0.9;
    }

    @Override
    public double getMinZ() {
        return z + 0.1;
    }

    @Override
    public double getMaxZ() {
        return z + 0.9;
    }

    @Override
    public int getLightFilter() {
        return 1;
    }

    @Override
    public boolean useDefaultFallDamage() {
        return false;
    }

    @Override
    public void onEntityFallOn(Entity entity, float fallDistance) {
        int jumpBoost = entity.hasEffect(EffectType.JUMP_BOOST)? Effect.get(EffectType.JUMP_BOOST).getLevel() : 0;
        float damage = (float) Math.floor(fallDistance - 3 - jumpBoost);

        damage *= 0.2F;

        if (damage > 0) {
            entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.FALL, damage));
        }
    }

    @Override
    public boolean canSticksBlock() {
        return true;
    }
}