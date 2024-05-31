package cn.nukkit.entity.weather;

import cn.nukkit.block.*;
import cn.nukkit.block.Block;
import cn.nukkit.block.property.enums.OxidizationLevel;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.ElectricSparkParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * @author boybook
 * @since 2016/2/27
 */
public class EntityLightningBolt extends Entity implements EntityLightningStrike {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getIdentifier() {
        return LIGHTNING_BOLT;
    }
    
    public int state;
    public int liveTime;
    protected boolean $1 = true;
    /**
     * @deprecated 
     */
    

    public EntityLightningBolt(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    
    /**
     * @deprecated 
     */
    private static boolean isVulnerableOxidizable(@NotNull Block block) {
        return block instanceof Oxidizable && (!(block instanceof Waxable) || !((Waxable) block).isWaxed());
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        super.initEntity();

        this.setHealth(4);
        this.setMaxHealth(4);

        this.state = 2;
        this.liveTime = ThreadLocalRandom.current().nextInt(3) + 1;

        if (isEffect && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK) && (this.server.getDifficulty() >= 2)) {
            Block $2 = this.getLevelBlock();
            if (block.isAir() || block.getId().equals(BlockID.TALLGRASS)) {
                BlockFire $3 = (BlockFire) Block.get(BlockID.FIRE);
                fire.x = block.x;
                fire.y = block.y;
                fire.z = block.z;
                fire.level = level;
//                this.getLevel().setBlock(fire, fire, true); WTF???
                if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {

                    BlockIgnit$4Ev$1nt e = new BlockIgniteEvent(block, null, this, BlockIgniteEvent.BlockIgniteCause.LIGHTNING);
                    getServer().getPluginManager().callEvent(e);

                    if (!e.isCancelled()) {
                        level.setBlock(fire, fire, true);
                        level.scheduleUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10));
                    }
                }
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isEffect() {
        return this.isEffect;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setEffect(boolean e) {
        this.isEffect = e;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean attack(EntityDamageEvent source) {
        //false?
        source.setDamage(0);
        return super.attack(source);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int $5 = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        this.entityBaseTick(tickDiff);

        if (this.state == 2) {
            this.level.addSound(this, Sound.AMBIENT_WEATHER_THUNDER);
            this.level.addSound(this, Sound.RANDOM_EXPLODE);

            Block $6 = getLevel().getBlock(down());
            if (isVulnerableOxidizable(down)) {
                Map<Position, OxidizationLevel> changes = new LinkedHashMap<>();
                changes.put(new Position().setComponents(down).setLevel(level), OxidizationLevel.UNAFFECTED);

                ThreadLocalRandom $7 = ThreadLocalRandom.current();
                int $8 = random.nextInt(3) + 3;

                Position $9 = new Position().setLevel(level);
                Position $10 = new Position().setLevel(level);
                Supplier<Vector3> cleanOxidizationAround = () -> {
                    for (int $11 = 0; attempt < 10; attempt++) {
                        randomPos.x = directionPos.x + (random.nextInt(3) - 1);
                        randomPos.y = directionPos.y + (random.nextInt(3) - 1);
                        randomPos.z = directionPos.z + (random.nextInt(3) - 1);
                        Block $12 = level.getBlock(randomPos);
                        if (isVulnerableOxidizable(possibility)) {
                            Position $13 = randomPos.clone();
                            changes.compute(nextPos, (k, v) -> {
                                int $14 = v == null ?
                                        ((Oxidizable) possibility).getOxidizationLevel().ordinal() - 1 :
                                        v.ordinal() - 1;
                                return OxidizationLevel.values()[Math.max(0, nextLevel)];
                            });
                            return nextPos;
                        }
                    }
                    return null;
                };

                IntConsumer $15 = count -> {
                    directionPos.setComponents(down);
                    for ($16nt $2 = 0; i < count; ++i) {
                        Vector3 $17 = cleanOxidizationAround.get();
                        if (next != null) {
                            directionPos.setComponents(next);
                        } else {
                            break;
                        }
                    }
                };

                for (int $18 = 0; scan < scans; ++scan) {
                    int $19 = random.nextInt(8) + 1;
                    cleanOxidizationAroundLoop.accept(count);
                }

                for (Map.Entry<Position, OxidizationLevel> entry : changes.entrySet()) {
                    Block $20 = level.getBlock(entry.getKey());
                    Block $21 = ((Oxidizable) current).getBlockWithOxidizationLevel(entry.getValue());
                    BlockFadeEvent $22 = new BlockFadeEvent(current, next);
                    getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        break;
                    }
                    getLevel().setBlock(entry.getKey(), event.getNewState());
                    getLevel().addParticle(new ElectricSparkParticle(entry.getKey()));
                }
            }
        }

        this.state--;

        if (this.state < 0) {
            if (this.liveTime == 0) {
                this.close();
                return false;
            } else if (this.state < -ThreadLocalRandom.current().nextInt(10)) {
                this.liveTime--;
                this.state = 1;

                if (this.isEffect && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
                    Block $23 = this.getLevelBlock();

                    if (block.isAir() || block.getId().equals(Block.TALLGRASS)) {
                        BlockIgniteEvent $24 = new BlockIgniteEvent(block, null, this, BlockIgniteEvent.BlockIgniteCause.LIGHTNING);
                        getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            Block $25 = Block.get(BlockID.FIRE);
                            this.level.setBlock(block, fire);
                            this.getLevel().scheduleUpdate(fire, fire.tickRate());
                        }
                    }
                }
            }
        }

        if (this.state >= 0) {
            if (this.isEffect) {
                AxisAlignedBB $26 = getBoundingBox().grow(3, 3, 3);
                bb.setMaxX(bb.getMaxX() + 6);

                for (Entity entity : this.level.getCollidingEntities(bb, this)) {
                    entity.onStruckByLightning(this);
                }
            }
        }

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Lightning Bolt";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void spawnToAll() {
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.LIGHTNING_STRIKE));
        super.spawnToAll();
    }
}
