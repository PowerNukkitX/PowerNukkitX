package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.projectile.EntityArrow;
import org.powernukkitx.event.block.BlockBurnEvent;
import org.powernukkitx.event.block.BlockFadeEvent;
import org.powernukkitx.event.block.BlockIgniteEvent;
import org.powernukkitx.event.entity.EntityCombustByBlockEvent;
import org.powernukkitx.event.entity.EntityDamageByBlockEvent;
import org.powernukkitx.event.entity.EntityDamageEvent.DamageCause;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.LevelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.powernukkitx.block.property.CommonBlockProperties.AGE_16;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_EAST;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_NORTH;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_SOUTH;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_UP;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_WEST;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockFire extends BlockFlowable implements StateDeriving {
    public static final BlockProperties PROPERTIES = new BlockProperties(FIRE, AGE_16, CONNECTION_EAST, CONNECTION_NORTH,
        CONNECTION_SOUTH, CONNECTION_UP, CONNECTION_WEST);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFire() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFire(BlockState blockstate) {
        super(blockstate);
    }

    public int getAge() {
        return getPropertyValue(AGE_16);
    }

    public void setAge(int age) {
        setPropertyValue(AGE_16, age);
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public String getName() {
        return "Fire Block";
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!entity.hasEffect(EffectType.FIRE_RESISTANCE)) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.FIRE, 1));
        }

        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 8);
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
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_RANDOM) {
            Block down = down();

            if (type == Level.BLOCK_UPDATE_NORMAL) {
                String downId = down.getId();
                if (downId.equals(Block.SOUL_SAND) || downId.equals(Block.SOUL_SOIL)) {
                    this.getLevel().setBlock(this, Block.get(SOUL_FIRE).setPropertyValue(AGE_16, this.getAge()));
                    return type;
                }
            }

            if (!this.isBlockTopFacingSurfaceSolid(down) && !this.canNeighborBurn()) {
                BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            } else {
                if (this.autoConfigureState()) {
                    // only the drawn shape changed, the neighbors have nothing to react to
                    level.setBlock(this, this, true, false);
                }
                if (this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK) && !level.isUpdateScheduled(this, this)) {
                    level.scheduleUpdate(this, tickRate());
                }
            }

            //check for rain on the first placement
            checkRain();

            return Level.BLOCK_UPDATE_NORMAL;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            Block down = down();
            String downId = down.getId();

            ThreadLocalRandom random = ThreadLocalRandom.current();

            //TODO: END
            if (!this.isBlockTopFacingSurfaceSolid(down) && !this.canNeighborBurn()) {
                BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
            }

            boolean forever = downId.equals(BlockID.NETHERRACK) ||
                    downId.equals(BlockID.MAGMA) ||
                    downId.equals(BlockID.BEDROCK) && ((BlockBedrock) down).getBurnIndefinitely();

            if (!checkRain()) {
                int meta = getAge();

                if (meta < 15) {
                    int newMeta = meta + random.nextInt(3);
                    this.setAge(Math.min(newMeta, 15));
                    this.getLevel().setBlock(this, this, true);
                }

                this.getLevel().scheduleUpdate(this, this.tickRate() + random.nextInt(10));

                if (!forever && !this.canNeighborBurn()) {
                    if (!this.isBlockTopFacingSurfaceSolid(down) || meta > 3) {
                        BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                        level.getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            level.setBlock(this, event.getNewState(), true);
                        }
                    }
                } else if (!forever && down.getBurnAbility() == 0 && meta == 15 && random.nextInt(4) == 0) {
                    BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
                    level.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        level.setBlock(this, event.getNewState(), true);
                    }
                } else {
                    int o = 0;

                    //TODO: decrease the o if the rainfall values are high
                    this.tryToCatchBlockOnFire(this.east(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.west(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(down, 250 + o, meta);
                    this.tryToCatchBlockOnFire(this.up(), 250 + o, meta);
                    this.tryToCatchBlockOnFire(this.south(), 300 + o, meta);
                    this.tryToCatchBlockOnFire(this.north(), 300 + o, meta);

                    for (int x = (int) (this.x - 1); x <= (int) (this.x + 1); ++x) {
                        for (int z = (int) (this.z - 1); z <= (int) (this.z + 1); ++z) {
                            for (int y = (int) (this.y - 1); y <= (int) (this.y + 4); ++y) {
                                if (x != (int) this.x || y != (int) this.y || z != (int) this.z) {
                                    int k = 100;

                                    if (y > this.y + 1) {
                                        k += (int) ((y - (this.y + 1)) * 100);
                                    }

                                    Block block = this.getLevel().getBlock(new Vector3(x, y, z));
                                    int chance = this.getChanceOfNeighborsEncouragingFire(block);

                                    if (chance > 0) {
                                        int t = (chance + 40 + this.getLevel().getServer().getDifficulty() * 7) / (meta + 30);

                                        //TODO: decrease the t if the rainfall values are high

                                        if (t > 0 && random.nextInt(k) <= t) {
                                            int damage = meta + random.nextInt(5) / 4;

                                            if (damage > 15) {
                                                damage = 15;
                                            }

                                            BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.SPREAD);
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

    private void tryToCatchBlockOnFire(Block block, int bound, int damage) {
        int burnAbility = block.getBurnAbility();

        Random random = ThreadLocalRandom.current();

        if (random.nextInt(bound) < burnAbility) {

            if (random.nextInt(damage + 10) < 5) {
                int meta = damage + random.nextInt(5) / 4;

                if (meta > 15) {
                    meta = 15;
                }

                BlockIgniteEvent e = new BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.SPREAD);
                this.level.getServer().getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    this.getLevel().setBlock(block, Block.get(blockstate.setPropertyValue(PROPERTIES, AGE_16.createValue(this.getAge()))), true);
                    this.getLevel().scheduleUpdate(block, this.tickRate());
                }
            } else {
                BlockBurnEvent ev = new BlockBurnEvent(block);
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

    private int getChanceOfNeighborsEncouragingFire(Block block) {
        if (!block.isAir()) {
            return 0;
        } else {
            int chance = 0;
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
     * Fire standing on something it can sit on draws as a plain flame. Otherwise it clings to every
     * flammable neighbor, the way it does on the side of a wooden wall.
     * <p>
     * Soul fire has no shape, so it is left alone.
     */
    @Override
    public boolean autoConfigureState() {
        if (!this.getProperties().containProperty(CONNECTION_UP)) {
            return false;
        }
        final boolean up, north, south, west, east;
        try {
            final Block down = this.down();
            final boolean grounded = this.isBlockTopFacingSurfaceSolid(down) || down.getBurnChance() > 0;
            up = !grounded && this.up().getBurnChance() > 0;
            north = !grounded && this.north().getBurnChance() > 0;
            south = !grounded && this.south().getBurnChance() > 0;
            west = !grounded && this.west().getBurnChance() > 0;
            east = !grounded && this.east().getBurnChance() > 0;
        } catch (LevelException e) {
            // a neighbor is outside a loaded chunk, try again on the next update
            return false;
        }
        final short previous = this.blockstate.specialValue();
        this.setPropertyValue(CONNECTION_UP, up);
        this.setPropertyValue(CONNECTION_NORTH, north);
        this.setPropertyValue(CONNECTION_SOUTH, south);
        this.setPropertyValue(CONNECTION_WEST, west);
        this.setPropertyValue(CONNECTION_EAST, east);
        return this.blockstate.specialValue() != previous;
    }

    public boolean canNeighborBurn() {
        for (BlockFace face : BlockFace.values()) {
            if (this.getSide(face).getBurnChance() > 0) {
                return true;
            }
        }

        return false;
    }

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
     * Check whether the fire should be extinguished by rain
     *
     * @return whether it should be extinguished by rain
     */
    protected boolean checkRain() {
        var down = down();
        String downId = down.getId();
        boolean forever = downId.equals(BlockID.NETHERRACK) || downId.equals(BlockID.MAGMA)
                || downId.equals(BlockID.BEDROCK) && ((BlockBedrock) down).getBurnIndefinitely();

        if (!forever && this.getLevel().isRaining() &&
                (this.getLevel().canBlockSeeSky(this) ||
                        this.getLevel().canBlockSeeSky(this.east()) ||
                        this.getLevel().canBlockSeeSky(this.west()) ||
                        this.getLevel().canBlockSeeSky(this.south()) ||
                        this.getLevel().canBlockSeeSky(this.north()))
        ) {
            BlockFadeEvent event = new BlockFadeEvent(this, get(AIR));
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                level.setBlock(this, event.getNewState(), true);
            }
            return true;
        }
        return false;
    }
}
