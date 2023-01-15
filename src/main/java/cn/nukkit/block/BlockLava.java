package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockLava extends BlockLiquid {

    public BlockLava() {
        this(0);
    }

    public BlockLava(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FLOWING_LAVA;
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

        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 8);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled()
                // Making sure the entity is actually alive and not invulnerable.
                && entity.isAlive()
                && entity.noDamageTicks == 0) {
            entity.setOnFire(ev.getDuration());
        }

        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.LAVA, 4));
        }

        super.onEntityCollide(entity);
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
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

                    if (block.getId() == AIR) {
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

                    if (block.up().getId() == AIR && block.getBurnChance() > 0 && isNetherSpreadNotAllowed(block)) {
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

    /**
     * 用于检验在地狱中岩浆是否可以引燃下界自然方块
     * @param spreadTarget 目标火焰点燃方块
     * @return 是否可以被点燃
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    private boolean isNetherSpreadNotAllowed(Block spreadTarget) {
        if(this.getLevel().isNether()) {
            final var id = spreadTarget.getId();
            if(id >= CRIMSON_ROOTS && id <= NETHER_SPROUTS_BLOCK) {
                return false;
            }
            if(id >= STRIPPED_CRIMSON_STEM && id <= WARPED_DOUBLE_SLAB) {
                return false;
            }
            return id < WARPED_HYPHAE || id > STRIPPED_WARPED_HYPHAE;
        }
        return true;
    }

    protected boolean isSurroundingBlockFlammable(Block block) {
        for (final var face : BlockFace.values()) {
            final var b = block.getSide(face);
            if (b.getBurnChance() > 0 && isNetherSpreadNotAllowed(b)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LAVA_BLOCK_COLOR;
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return (BlockLiquid) Block.get(BlockID.FLOWING_LAVA, meta);
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
    protected void checkForHarden(){ 
        Block colliding = null;
        Block down = this.getSide(BlockFace.DOWN);
        for(int side = 1; side < 6; ++side){ //don't check downwards side
            Block blockSide = this.getSide(BlockFace.fromIndex(side));
            if(blockSide instanceof BlockWater || blockSide.getLevelBlockAtLayer(1) instanceof BlockWater){
                colliding = blockSide;
                break;
            }
            if(down instanceof BlockSoulSoil) {
                if (blockSide instanceof BlockBlueIce) {
                    liquidCollide(this, Block.get(BlockID.BASALT));
                    return;
                }
            }
        }
        if(colliding != null){
            if(this.getDamage() == 0){
                this.liquidCollide(colliding, Block.get(BlockID.OBSIDIAN));
            }else if(this.getDamage() <= 4){
                this.liquidCollide(colliding, Block.get(BlockID.COBBLESTONE));
            }
        }
    }

    @Override
    protected void flowIntoBlock(Block block, int newFlowDecay){
        if(block instanceof BlockWater){
            ((BlockLiquid) block).liquidCollide(this, Block.get(BlockID.STONE));
        } else {
            super.flowIntoBlock(block, newFlowDecay);
        }
    }

    @Override
    public void addVelocityToEntity(Entity entity, Vector3 vector) {
        if (!(entity instanceof EntityPrimedTNT)) {
            super.addVelocityToEntity(entity, vector);
        }
    }

    @Since("1.19.50-r4")
    @Override
    public double getPassableBlockFrictionFactor() {
        return 0.3;
    }
}
