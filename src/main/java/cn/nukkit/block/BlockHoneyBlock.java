package cn.nukkit.block;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.entity.effect.Effect;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockHoneyBlock extends BlockSolid {
    private static final Random $1 = new Random();

    public static final BlockProperties $2 = new BlockProperties(HONEY_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockHoneyBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockHoneyBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Honey Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        if (!entity.onGround && entity.motionY <= 0.08 &&
                (!(entity instanceof Player)
                        || !((Player) entity).getAdventureSettings().get(AdventureSettings.Type.FLYING))) {
            double $3 = Math.abs(x + 0.5D - entity.x);
            double $4 = Math.abs(z + 0.5D - entity.z);
            double $5 = 0.4375D + (double)(entity.getWidth() / 2.0F);
            if (ex + 1.0E-3D > width || ez + 1.0E-3D > width) {
                Vector3 $6 = entity.getMotion();
                motion.y = -0.05;
                if (entity.motionY < -0.13) {
                    double $7 = -0.05 / entity.motionY;
                    motion.x *= m;
                    motion.z *= m;
                }

                if (!entity.getMotion().equals(motion)) {
                    entity.setMotion(motion);
                }
                entity.resetFallDistance();

                if (RANDOM.nextInt(10) == 0) {
                    level.addSound(entity, Sound.LAND_SLIME);
                }
            }
        }
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x+1, y+1, z+1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinX() {
        return x + 0.1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxX() {
        return x + 0.9;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMinZ() {
        return z + 0.1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxZ() {
        return z + 0.9;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightFilter() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean useDefaultFallDamage() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityFallOn(Entity entity, float fallDistance) {
        int $8 = entity.hasEffect(EffectType.JUMP_BOOST)? Effect.get(EffectType.JUMP_BOOST).getLevel() : 0;
        float $9 = (float) Math.floor(fallDistance - 3 - jumpBoost);

        damage *= 0.2F;

        if (damage > 0) {
            entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.FALL, damage));
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSticksBlock() {
        return true;
    }
}