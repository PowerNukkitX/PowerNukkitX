package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;


public class BlockWitherRose extends BlockFlower {
    public static final BlockProperties $1 = new BlockProperties(WITHER_ROSE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWitherRose() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWitherRose(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPlantOn(Block block) {
        return super.canPlantOn(block) || block.getId().equals(BlockID.NETHERRACK) || block.getId().equals(BlockID.SOUL_SAND);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        if (level.getServer().getDifficulty() != 0 && entity instanceof EntityLiving living) {
            if (!living.invulnerable && !living.hasEffect(EffectType.WITHER)
                    && (!(living instanceof Player) || !((Player) living).isCreative() && !((Player) living).isSpectator())) {
                Effect $2 = Effect.get(EffectType.WITHER);
                effect.setDuration(40);
                effect.setAmplifier(1);
                living.addEffect(effect);
            }
        }
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }
}
