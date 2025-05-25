package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockTnt;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXpOrb;
import cn.nukkit.event.block.BlockExplodeEvent;
import cn.nukkit.event.block.BlockUpdateEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class Explosion {
    private final int RAYS = 16; //Rays
    private final double STEP_LEN = 0.3d;

    private final Level level;
    private final Position source;
    private final double size;
    private double fireChance;

    private Set<Block> affectedBlocks;
    private Set<Block> fireIgnitions;
    private boolean doesDamage = true;

    private final Object what;

    public Explosion(Position center, double size, Entity what) {
        this(center, size, (Object) what);
    }

    public Explosion(Position center, double size, Block what) {
        this(center, size, (Object) what);
    }

    /**
     * Creates explosion at given position with given power.
     *
     * @param center center position
     * @param size   the power of explosion
     * @param what   the source object, used for tracking damage
     */
    protected Explosion(Position center, double size, Object what) {
        this.level = center.getLevel();
        this.source = center;
        this.size = Math.max(size, 0);
        this.what = what;
    }

    public double getFireChance() {
        return fireChance;
    }

    public void setFireChance(double fireChance) {
        this.fireChance = fireChance;
    }

    public boolean isIncendiary() {
        return fireChance > 0;
    }

    public void setIncendiary(boolean incendiary) {
        if (!incendiary) {
            fireChance = 0;
        } else if (fireChance <= 0) {
            fireChance = 1.0 / 3.0;
        }
    }

    /**
     * @return bool
     */
    public boolean explode() {
        if (explodeA()) {
            return explodeB();
        }
        return false;
    }

    /**
     * Calculates which blocks will be destroyed by this explosion. If {@link #explodeB()} is called without calling this,
     * no blocks will be destroyed.
     *
     * @return {@code true} if success
     */
    public boolean explodeA() {
        if (what instanceof EntityExplosive) {
            Entity entity = (Entity) what;
            Block blockLayer0 = level.getBlock(entity.floor());
            Block blockLayer1 = level.getBlock(entity.floor(), 1);
            if (BlockID.FLOWING_WATER.equals(blockLayer0.getId())
                    || BlockID.WATER.equals(blockLayer0.getId())
                    || BlockID.FLOWING_WATER.equals(blockLayer1.getId())
                    || BlockID.WATER.equals(blockLayer1.getId())
            ) {
                this.doesDamage = false;
                return true;
            }
        }

        if (this.size < 0.1) {
            return false;
        }

        if (affectedBlocks == null) {
            affectedBlocks = new LinkedHashSet<>();
        }

        boolean incendiary = fireChance > 0;
        if (incendiary && fireIgnitions == null) {
            fireIgnitions = new LinkedHashSet<>();
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        Vector3 vector = new Vector3(0, 0, 0);
        Vector3 vBlock = new Vector3(0, 0, 0);

        int mRays = this.RAYS - 1;
        for (int i = 0; i < this.RAYS; ++i) {
            for (int j = 0; j < this.RAYS; ++j) {
                for (int k = 0; k < this.RAYS; ++k) {
                    if (i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents((double) i / (double) mRays * 2d - 1, (double) j / (double) mRays * 2d - 1, (double) k / (double) mRays * 2d - 1);
                        double len = vector.length();
                        vector.setComponents((vector.x / len) * this.STEP_LEN, (vector.y / len) * this.STEP_LEN, (vector.z / len) * this.STEP_LEN);
                        double pointerX = this.source.x;
                        double pointerY = this.source.y;
                        double pointerZ = this.source.z;

                        for (double blastForce = this.size * (random.nextInt(700, 1301)) / 1000d; blastForce > 0; blastForce -= this.STEP_LEN * 0.75d) {
                            int x = (int) pointerX;
                            int y = (int) pointerY;
                            int z = (int) pointerZ;
                            vBlock.x = pointerX >= x ? x : x - 1;
                            vBlock.y = pointerY >= y ? y : y - 1;
                            vBlock.z = pointerZ >= z ? z : z - 1;
                            if (!this.level.isYInRange((int) vBlock.y)) {
                                break;
                            }
                            Block block = this.level.getBlock(vBlock);

                            if (!block.isAir()) {
                                Block layer1 = block.getLevelBlockAtLayer(1);
                                double resistance = Math.max(block.getResistance(), layer1.getResistance());
                                blastForce -= (resistance / 5 + 0.3d) * this.STEP_LEN;
                                if (blastForce > 0) {
                                    if (this.affectedBlocks.add(block)) {
                                        if (incendiary && random.nextDouble() <= fireChance) {
                                            this.fireIgnitions.add(block);
                                        }
                                        if (!layer1.isAir()) {
                                            this.affectedBlocks.add(layer1);
                                        }
                                    }
                                }
                            }
                            pointerX += vector.x;
                            pointerY += vector.y;
                            pointerZ += vector.z;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Executes the explosion's effects on the world. This includes destroying blocks (if any),
     * harming and knocking back entities, and creating sounds and particles.
     *
     * @return {@code false} if explosion was canceled, otherwise {@code true}
     */
    public boolean explodeB() {

        LongArraySet updateBlocks = new LongArraySet();
        List<Vector3> send = new ArrayList<>();

        Vector3 source = (new Vector3(this.source.x, this.source.y, this.source.z)).floor();
        double yield = (1d / this.size) * 100d;

        if (affectedBlocks == null) {
            affectedBlocks = new LinkedHashSet<>();
        }

        if (this.what instanceof Entity) {
            List<Block> affectedBlocksList = new ArrayList<>(this.affectedBlocks);
            EntityExplodeEvent ev = new EntityExplodeEvent((Entity) this.what, this.source, affectedBlocksList, yield);
            ev.setIgnitions(fireIgnitions == null ? new LinkedHashSet<>(0) : fireIgnitions);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            } else {
                yield = ev.getYield();
                affectedBlocks.clear();
                affectedBlocks.addAll(ev.getBlockList());
                fireIgnitions = ev.getIgnitions();
            }
        } else if (this.what instanceof Block) {
            BlockExplodeEvent ev = new BlockExplodeEvent((Block) this.what, this.source, this.affectedBlocks,
                    fireIgnitions == null ? new LinkedHashSet<>(0) : fireIgnitions, yield, this.fireChance);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            } else {
                yield = ev.getYield();
                affectedBlocks = ev.getAffectedBlocks();
                fireIgnitions = ev.getIgnitions();
            }
        }

        double explosionSize = this.size * 2d;
        double minX = NukkitMath.floorDouble(this.source.x - explosionSize - 1);
        double maxX = NukkitMath.ceilDouble(this.source.x + explosionSize + 1);
        double minY = NukkitMath.floorDouble(this.source.y - explosionSize - 1);
        double maxY = NukkitMath.ceilDouble(this.source.y + explosionSize + 1);
        double minZ = NukkitMath.floorDouble(this.source.z - explosionSize - 1);
        double maxZ = NukkitMath.ceilDouble(this.source.z + explosionSize + 1);

        AxisAlignedBB explosionBB = new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        Entity[] list = this.level.getNearbyEntities(explosionBB, this.what instanceof Entity ? (Entity) this.what : null);
        for (Entity entity : list) {
            double distance = entity.distance(this.source) / explosionSize;

            if (distance <= 1) {
                Vector3 motion = entity.subtract(this.source).normalize();

                float blockDensity = level.getBlockDensity(this.source, entity.boundingBox);
                double force = this.size * 2.0F;
                double d = entity.distance(source) / force;
                double impact = (1.0D - d) * blockDensity;
                float entityDamageAmount = (float) ((float) (impact * impact + impact) / 2.0D * 7.0D * force + 1.0D);
                float damage = this.doesDamage ? entityDamageAmount : 0f;

                if (this.what instanceof Entity) {
                    entity.attack(new EntityDamageByEntityEvent((Entity) this.what, entity, DamageCause.ENTITY_EXPLOSION, damage));
                } else if (this.what instanceof Block) {
                    entity.attack(new EntityDamageByBlockEvent((Block) this.what, entity, DamageCause.BLOCK_EXPLOSION, damage));
                } else {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.BLOCK_EXPLOSION, damage));
                }

                if (!(entity instanceof EntityItem || entity instanceof EntityXpOrb)) {
                    var multipliedMotion = motion.multiply(impact);
                    entity.motionX += multipliedMotion.x;
                    entity.motionY += multipliedMotion.y;
                    entity.motionZ += multipliedMotion.z;
                }
            }
        }

        ItemBlock air = new ItemBlock(Block.get(BlockID.AIR));
        BlockEntity container;
        List<Vector3> smokePositions = this.affectedBlocks.isEmpty() ? Collections.emptyList() : new ObjectArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (Block block : this.affectedBlocks) {
            if (block instanceof BlockTnt tnt) {
                tnt.prime(random.nextInt(10, 31), this.what instanceof Entity ? (Entity) this.what : null);
            } else if ((container = block.getLevel().getBlockEntity(block)) instanceof InventoryHolder inventoryHolder) {
                if (container instanceof BlockEntityShulkerBox) {
                    this.level.dropItem(block.add(0.5, 0.5, 0.5), block.toItem());
                    inventoryHolder.getInventory().clearAll();
                } else {
                    for (Item drop : inventoryHolder.getInventory().getContents().values()) {
                        this.level.dropItem(block.add(0.5, 0.5, 0.5), drop);
                    }
                    inventoryHolder.getInventory().clearAll();
                }
            } else if (random.nextDouble() * 100 < yield) {
                for (Item drop : block.getDrops(air)) {
                    this.level.dropItem(block.add(0.5, 0.5, 0.5), drop);
                }
            }

            if (random.nextInt(8) == 0) {
                smokePositions.add(block);
            }

            this.level.setBlock(new Vector3((int) block.x, (int) block.y, (int) block.z),
                    block.layer, Block.get(BlockID.AIR));

            if (block.layer != 0) {
                continue;
            }

            Vector3 pos = new Vector3(block.x, block.y, block.z);

            for (BlockFace side : BlockFace.values()) {
                Vector3 sideBlock = pos.getSide(side);
                long index = Hash.hashBlock((int) sideBlock.x, (int) sideBlock.y, (int) sideBlock.z);
                if (!this.affectedBlocks.contains(sideBlock) && !updateBlocks.contains(index)) {
                    BlockUpdateEvent ev = new BlockUpdateEvent(this.level.getBlock(sideBlock));
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL);
                    }
                    Block layer1 = this.level.getBlock(sideBlock, 1);
                    if (!layer1.isAir()) {
                        ev = new BlockUpdateEvent(layer1);
                        this.level.getServer().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL);
                        }
                    }
                    updateBlocks.add(index);
                }
            }
            send.add(new Vector3(block.x - source.x, block.y - source.y, block.z - source.z));
        }

        if(fireIgnitions != null) {
            for (Vector3 remainingPos : fireIgnitions) {
                Block toIgnite = level.getBlock(remainingPos);
                if (toIgnite.isAir() && toIgnite.down().isSolid(BlockFace.UP)) {
                    level.setBlock(toIgnite, Block.get(BlockID.FIRE));
                }
            }
        }

        int count = smokePositions.size();
        CompoundTag data = new CompoundTag(new Object2ObjectOpenHashMap<>(count, 0.999999f))
                .putFloat("originX", (float) this.source.x)
                .putFloat("originY", (float) this.source.y)
                .putFloat("originZ", (float) this.source.z)
                .putFloat("radius", (float) this.size)
                .putInt("size", count);
        for (int i = 0; i < count; i++) {
            Vector3 pos = smokePositions.get(i);
            String prefix = "pos" + i;
            data.putFloat(prefix + "x", (float) pos.x);
            data.putFloat(prefix + "y", (float) pos.y);
            data.putFloat(prefix + "z", (float) pos.z);
        }
        this.level.addSound(this.source, Sound.RANDOM_EXPLODE);
        this.level.addLevelEvent(this.source, LevelEventPacket.EVENT_PARTICLE_EXPLOSION, Math.round((float) this.size));
        this.level.addLevelEvent(this.source, LevelEventPacket.EVENT_PARTICLE_BLOCK_EXPLOSION, data);

        return true;
    }

}
