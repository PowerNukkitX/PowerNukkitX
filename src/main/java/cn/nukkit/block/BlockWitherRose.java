package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.FlowerType;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.potion.Effect;
import org.jetbrains.annotations.NotNull;


public class BlockWitherRose extends BlockRedFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(WITHER_ROSE);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWitherRose() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWitherRose(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean canPlantOn(Block block) {
        return super.canPlantOn(block) || block.getId().equals(BlockID.NETHERRACK) || block.getId().equals(BlockID.SOUL_SAND);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        return false;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (level.getServer().getDifficulty() != 0 && entity instanceof EntityLiving living) {
            if (!living.invulnerable && !living.hasEffect(Effect.WITHER)
                    && (!(living instanceof Player) || !((Player) living).isCreative() && !((Player) living).isSpectator())) {
                Effect effect = Effect.getEffect(Effect.WITHER);
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
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void setFlowerType(FlowerType flowerType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlowerType getFlowerType() {
        throw new UnsupportedOperationException();
    }
}
