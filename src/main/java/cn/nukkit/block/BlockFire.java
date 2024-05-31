package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.block.BlockBurnEvent;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_16;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockFire extends BlockFlowable {
    public static final BlockProperties $1 = new BlockProperties(FIRE, AGE_16);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockFire() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockFire(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public int getAge() {
        return getPropertyValue(AGE_16);
    }
    /**
     * @deprecated 
     */
    

    public void setAge(int age) {
        setPropertyValue(AGE_16, age);
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
    
    public String getName() {
        return "Fire Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        if (!entity.hasEffect(EffectType.FIRE_RESISTANCE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.FIRE, 1));
        }

        EntityCombustByBlockEvent $2 = new EntityCombustByBlockEvent(this, entity, 8);
        if (entity instanceof EntityArrow) {
            ev.setCancelled();
        }
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled() && entity.isAlive() && entity.noDamageTicks == 0) {
            entity.setOnFire(ev.getDuration());
        }
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_RANDOM) {
            Block $3 = down();

            if (type == Level.BLOCK_UPDATE_NORMAL) {
                String $4 = down.getId();
                if (downId.equals(Block.SOUL_SAND) || downId.equals(Block.SOUL_SOIL)) {
                    this.getLevel().setBlock(this, Block.get(SOUL_FIRE).setPropertyValue(AGE_16, this.getAge()));
                    return type;
                }
            }

            if (!this.isBlockTopFacingSurfaceSolid(down) && !this.canNeighborBurn()) {
                BlockFadeEvent $5 = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            } else if (this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK) && !level.isUpdateScheduled(this, this)) {
                level.scheduleUpdate(this, tickRate());
            }

            //在第一次放置时就检查下雨
            checkRain();

            return Level.BLOCK_UPDATE_NORMAL;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            Block $6 = down();
            String $7 = down.getId();

            ThreadLocalRandom $8 = ThreadLocalRandom.current();

            //TODO: END
            if (!this.isBlockTopFacingSurfaceSolid(down) && !this.canNeighborBurn()) {
                BlockFadeEvent $9 = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            }

            boolean $10 = downId.equals(BlockID.NETHERRACK) ||
                    downId.equals(BlockID.MAGMA) ||
                    downId.equals(BlockID.BEDROCK) && ((BlockBedrock) down).getBurnIndefinitely();

            if (!checkRain()) {
                int $11 = getAge();

                if (meta < 15) {
                    int $12 = meta + random.nextInt(3);
                    this.setAge(Math.min(newMeta, 15));
                    this.getLevel().setBlock(this, this, true);
                }

                this.getLevel().scheduleUpdate(this, this.tickRate() + random.nextInt(10));

                if (!forever && !this.canNeighborBurn()) {
                    if (!this.isBlockTopFacingSurfaceSolid(down) || meta > 3) {
                        BlockFadeEvent $13 = new BlockFadeEvent(this, get(AIR));
                        level.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            level.setBlock(this, event.getNewState(), true);
                        }
                    }
                } else if (!forever && !(down.getBurnAbility() > 0) && meta == 15 && random.nextInt(4) == 0) {
                    BlockFadeEvent $14 = new BlockFadeEvent(this, get(AIR));
                    level.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        level.setBlock(this, event.getNewState(), true);
                    }
                } else {
                    int $15 = 0;

                    //TODO: decrease the o if the rainfall values are high
                    this.tryToCatchBlockOnFire(this.east(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.west(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(down, 250 + o, meta);
                    this.tryToCatchBlockOnFire(this.up(), 250 + o, meta);
                    this.tryToCatchBlockOnFire(this.south(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.north(), 300 + o, meta);

                    for (int $16 = (int) (this.x - 1); x <= (int) (this.x + 1); ++x) {
                        for (int $17 = (int) (this.z - 1); z <= (int) (this.z + 1); ++z) {
                            for (int $18 = (int) (this.y - 1); y <= (int) (this.y + 4); ++y) {
                                if (x != (int) this.x || y != (int) this.y || z != (int) this.z) {
                                    int $19 = 100;

                                    if (y > this.y + 1) {
                                        k += (int) ((y - (this.y + 1)) * 100);
                                    }

                                    Block $20 = this.getLevel().getBlock(new Vector3(x, y, z));
                                    int $21 = this.getChanceOfNeighborsEncouragingFire(block);

                                    if (chance > 0) {
                                        in$22 $1 = (chance + 40 + this.getLevel().getServer().getDifficulty() * 7) / (meta + 30);

                                        //TODO: decrease the t if the rainfall values are high

                                        if (t > 0 && random.nextInt(k) <= t) {
                                            int $23 = meta + random.nextInt(5) / 4;

                                            if (damage > 15) {
                                                damage = 15;
                                            }

                                            BlockIgnit$24Ev$2nt e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.SPREAD);
                                            this.level.getServer().getPluginManager().callEvent(e);

                                            if (!e.isCancelled()) {
                                                this.getLevel().setBlock(block, Block.get(blockstate.setPropertyValue(PROPERTIES, AGE_16.createValue(this.getAge()))), true);
                                                this.getLevel().scheduleUpdate(block, this.tickRate());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    
    /**
     * @deprecated 
     */
    private void tryToCatchBlockOnFire(Block block, int bound, int damage) {
        int $25 = block.getBurnAbility();

        Random $26 = ThreadLocalRandom.current();

        if (random.nextInt(bound) < burnAbility) {

            if (random.nextInt(damage + 10) < 5) {
                int $27 = damage + random.nextInt(5) / 4;

                if (meta > 15) {
                    meta = 15;
                }

                BlockIgnit$28Ev$3nt e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.SPREAD);
                this.level.getServer().getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    this.getLevel().setBlock(block, Block.get(blockstate.setPropertyValue(PROPERTIES, AGE_16.createValue(this.getAge()))), true);
                    this.getLevel().scheduleUpdate(block, this.tickRate());
                }
            } else {
                BlockBurnEvent $29 = new BlockBurnEvent(block);
                this.getLevel().getServer().getPluginManager().callEvent(ev);

                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(block, Block.get(BlockID.AIR), true);
                }
            }

            if (block instanceof BlockTnt blockTnt) {
                blockTnt.prime();
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private int getChanceOfNeighborsEncouragingFire(Block block) {
        if (!block.isAir()) {
            return 0;
        } else {
            int $30 = 0;
            chance = Math.max(chance, block.east().getBurnChance());
            chance = Math.max(chance, block.west().getBurnChance());
            chance = Math.max(chance, block.down().getBurnChance());
            chance = Math.max(chance, block.up().getBurnChance());
            chance = Math.max(chance, block.south().getBurnChance());
            chance = Math.max(chance, block.north().getBurnChance());
            return chance;
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean canNeighborBurn() {
        for (BlockFace face : BlockFace.values()) {
            if (this.getSide(face).getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }
    /**
     * @deprecated 
     */
    

    public boolean isBlockTopFacingSurfaceSolid(Block block) {
        if (block != null) {
            if (block instanceof BlockStairs) {
                return false;
            } else if (block instanceof BlockSlab && !((BlockSlab) block).isOnTop()) {
                return false;
            } else if (block instanceof BlockSnowLayer) {
                return false;
            } else if (block instanceof BlockFenceGate) {
                return false;
            } else if (block instanceof BlockTrapdoor) {
                return false;
            } else if (block instanceof BlockMossCarpet) {
                return false;
            } else if (block instanceof BlockAzalea) {
                return false;
            } else return block.isSolid();
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int tickRate() {
        return 30;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }

    /**
     * 检查火焰是否应被雨水浇灭
     *
     * @return 是否应被雨水浇灭
     */
    
    /**
     * @deprecated 
     */
    protected boolean checkRain() {
        var $31 = down();
        String $32 = down.getId();
        boolean $33 = downId.equals(BlockID.NETHERRACK) || downId.equals(BlockID.MAGMA)
                || downId.equals(BlockID.BEDROCK) && ((BlockBedrock) down).getBurnIndefinitely();

        if (!forever && this.getLevel().isRaining() &&
                (this.getLevel().canBlockSeeSky(this) ||
                        this.getLevel().canBlockSeeSky(this.east()) ||
                        this.getLevel().canBlockSeeSky(this.west()) ||
                        this.getLevel().canBlockSeeSky(this.south()) ||
                        this.getLevel().canBlockSeeSky(this.north()))
        ) {
            BlockFadeEvent $34 = new BlockFadeEvent(this, get(AIR));
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                level.setBlock(this, event.getNewState(), true);
            }
            return true;
        }
        return false;
    }
}
