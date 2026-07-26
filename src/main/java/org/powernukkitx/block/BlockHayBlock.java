package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockHayBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(HAY_BLOCK, CommonBlockProperties.DEPRECATED, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHayBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHayBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        this.setPillarAxis(face.getAxis());
        this.getLevel().setBlock(block, this, true);
        return true;
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(CommonBlockProperties.PILLAR_AXIS);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(CommonBlockProperties.PILLAR_AXIS, axis);
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
}