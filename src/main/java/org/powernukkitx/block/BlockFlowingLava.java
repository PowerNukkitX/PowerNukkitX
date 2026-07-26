package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.item.EntityTnt;
import org.powernukkitx.event.block.BlockIgniteEvent;
import org.powernukkitx.event.entity.EntityCombustByBlockEvent;
import org.powernukkitx.event.entity.EntityDamageByBlockEvent;
import org.powernukkitx.event.entity.EntityDamageEvent.DamageCause;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockFlowingLava extends BlockLiquid {

    public static final BlockProperties PROPERTIES = new BlockProperties(FLOWING_LAVA, CommonBlockProperties.LIQUID_DEPTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFlowingLava() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFlowingLava(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public String getName() {
        return "Lava";
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.highestPosition -= (entity.highestPosition - entity.y) * 0.5;
        if (entity.isFireImmune()) {
            super.onEntityCollide(entity);
            return;
        }

        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 8);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled()
                // Making sure the entity is actually alive and not invulnerable.
                && entity.isAlive()
                && entity.noDamageTicks == 0) {
            entity.setOnFire(ev.getDuration());
        }

        if (!entity.hasEffect(EffectType.FIRE_RESISTANCE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.LAVA, 4));
        }

        super.onEntityCollide(entity);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }

    @Override
    public int onUpdate(int type) {
        int result = super.onUpdate(type);

        if (type == Level.BLOCK_UPDATE_RANDOM && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            Random random = ThreadLocalRandom.current();

            int i = random.nextInt(3);

            if (i > 0) {
                for (int k = 0; k < i; ++k) {
                    Vector3 v = this.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.isAir()) {
                        if (this.isSurroundingBlockFlammable(block)) {
                            BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.LAVA);
                            this.level.getServer().getPluginManager().callEvent(e);

                            if (!e.isCancelled()) {
                                Block fire = Block.get(BlockID.FIRE);
                                this.getLevel().setBlock(v, fire, true);
                                this.getLevel().scheduleUpdate(fire, fire.tickRate());
                                return Level.BLOCK_UPDATE_RANDOM;
                            }

                            return 0;
                        }
                    } else if (block.isSolid()) {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    Vector3 v = this.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                    Block block = this.getLevel().getBlock(v);

                    if (block.up().isAir() && block.getBurnChance() > 0) {
                        BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.LAVA);
                        this.level.getServer().getPluginManager().callEvent(e);

                        if (!e.isCancelled()) {
                            Block fire = Block.get(BlockID.FIRE);
                            this.getLevel().setBlock(v, fire, true);
                            this.getLevel().scheduleUpdate(fire, fire.tickRate());
                        }
                    }
                }
            }
        }

        return result;
    }

    protected boolean isSurroundingBlockFlammable(Block block) {
        for (final var face : BlockFace.values()) {
            final var b = block.getSide(face);
            if (b.getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockLiquid getLiquidWithNewDepth(int depth) {
        return new BlockFlowingLava(this.blockstate.setPropertyValue(PROPERTIES, CommonBlockProperties.LIQUID_DEPTH.createValue(depth)));
    }

    @Override
    public int tickRate() {
        if (this.level.getDimension() == Level.DIMENSION_NETHER) {
            return 10;
        }
        return 30;
    }

    @Override
    public int getFlowDecayPerBlock() {
        if (this.level.getDimension() == Level.DIMENSION_NETHER) {
            return 1;
        }
        return 2;
    }

    @Override
    protected void checkForMixing() {
        Block colliding = null;
        Block down = this.getSide(BlockFace.DOWN);
        for (int side = 1; side < 6; ++side) { //don't check downwards side
            Block blockSide = this.getSide(BlockFace.fromIndex(side));
            if (blockSide instanceof BlockFlowingWater || blockSide.getLevelBlockAtLayer(1) instanceof BlockFlowingWater) {
                colliding = blockSide;
                break;
            }
            if (down instanceof BlockSoulSoil) {
                if (blockSide instanceof BlockBlueIce) {
                    liquidCollide(this, Block.get(BlockID.BASALT));
                    return;
                }
            }
        }
        if (colliding != null) {
            if (this.getLiquidDepth() == 0) {
                this.liquidCollide(colliding, Block.get(BlockID.OBSIDIAN));
            } else if (this.getLiquidDepth() <= 4) {
                this.liquidCollide(colliding, Block.get(BlockID.COBBLESTONE));
            }
        }
    }

    @Override
    protected void flowIntoBlock(Block block, BlockFace blockFace, int newFlowDecay) {
        if (block instanceof BlockFlowingWater) {
            ((BlockLiquid) block).liquidCollide(this, Block.get(BlockID.STONE));
        } else {
            super.flowIntoBlock(block, blockFace, newFlowDecay);
        }
    }

    @Override
    public void addVelocityToEntity(Entity entity, Vector3 vector) {
        if (!(entity instanceof EntityTnt)) {
            super.addVelocityToEntity(entity, vector);
        }
    }

    @Override
    public double getPassableBlockFrictionFactor() {
        return 0.3;
    }
}
