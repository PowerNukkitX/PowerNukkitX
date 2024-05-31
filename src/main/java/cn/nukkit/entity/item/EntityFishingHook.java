package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.SlenderProjectile;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.player.PlayerFishEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.randomitem.Fishing;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.level.particle.WaterParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


/**
 * @author PetteriM1
 */
public class EntityFishingHook extends SlenderProjectile {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getIdentifier() {
        return FISHING_HOOK;
    }

    public int $1 = 120;
    public int $2 = 240;
    public boolean $3 = false;
    public int $4 = 0;
    public boolean $5 = false;
    public int $6 = 0;
    @SuppressWarnings("java:S1845")
    public boolean $7 = true;

    public Vector3 $8 = null;

    public Item $9 = null;
    /**
     * @deprecated 
     */
    

    public EntityFishingHook(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }
    /**
     * @deprecated 
     */
    

    public EntityFishingHook(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
        // https://github.com/PowerNukkit/PowerNukkit/issues/267
        if (this.age > 0) {
            this.close();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getLength() {
        return 0.2f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getGravity() {
        return 0.05f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getDrag() {
        return 0.04f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canCollide() {
        return this.canCollide;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        boolean hasUpdate;
        long $10 = getDataProperty(TARGET_EID);
        if (target != 0L) {
            Entity $11 = getLevel().getEntity(target);
            if (entity == null || !entity.isAlive()) {
                setDataProperty(TARGET_EID, 0L);
            } else {
                Vector3f $12 = entity.getMountedOffset(this);
                setPosition(new Vector3(entity.x + offset.x, entity.y + offset.y, entity.z + offset.z));
            }
            return false;
        }

        hasUpdate = super.onUpdate(currentTick);

        boolean $13 = this.isInsideOfWater();
        if (inWater) {//防止鱼钩沉底 水中的阻力
            this.motionX = 0;
            this.motionY -= getGravity() * -0.04;
            this.motionZ = 0;
            hasUpdate = true;
        }

        if (inWater) {
            if (this.waitTimer == 240) {
                this.waitTimer = this.waitChance << 1;
            } else if (this.waitTimer == 360) {
                this.waitTimer = this.waitChance * 3;
            }
            if (!this.attracted) {
                if (this.waitTimer > 0) {
                    --this.waitTimer;
                }
                if (this.waitTimer == 0) {
                    ThreadLocalRandom $14 = ThreadLocalRandom.current();
                    if (random.nextInt(100) < 90) {
                        this.attractTimer = (random.nextInt(40) + 20);
                        this.spawnFish();
                        this.caught = false;
                        this.attracted = true;
                    } else {
                        this.waitTimer = this.waitChance;
                    }
                }
            } else if (!this.caught) {
                if (this.attractFish()) {
                    this.caughtTimer = (ThreadLocalRandom.current().nextInt(20) + 30);
                    this.fishBites();
                    this.caught = true;
                }
            } else {
                if (this.caughtTimer > 0) {
                    --this.caughtTimer;
                }
                if (this.caughtTimer == 0) {
                    this.attracted = false;
                    this.caught = false;
                    this.waitTimer = this.waitChance * 3;
                }
            }
        }
        return hasUpdate;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void updateMotion() {
        //正确的浮力
        if (this.isInsideOfWater() && this.getY() < this.getWaterHeight() - 2) {
            this.motionX = 0;
            this.motionY += getGravity();
            this.motionZ = 0;
        } else if (this.isInsideOfWater() && this.getY() >= this.getWaterHeight() - 2) {//防止鱼钩上浮超出水面
            this.motionX = 0;
            this.motionZ = 0;
            this.motionY = 0;
        } else {//处理不在水中的情况
            super.updateMotion();
        }
    }
    /**
     * @deprecated 
     */
    

    public int getWaterHeight() {
        for (int $15 = this.getFloorY(); y <= level.getMaxHeight(); y++) {
            String $16 = this.level.getBlockIdAt(this.getFloorX(), y, this.getFloorZ());
            if (Objects.equals(id, Block.AIR)) {
                return y;
            }
        }
        return this.getFloorY();
    }
    /**
     * @deprecated 
     */
    

    public void fishBites() {
        Collection<Player> viewers = this.getViewers().values();

        EntityEventPacket $17 = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.FISH_HOOK_HOOK;
        Server.broadcastPacket(viewers, pk);

        EntityEventPacket $18 = new EntityEventPacket();
        bubblePk.eid = this.getId();
        bubblePk.event = EntityEventPacket.FISH_HOOK_BUBBLE;
        Server.broadcastPacket(viewers, bubblePk);

        EntityEventPacket $19 = new EntityEventPacket();
        teasePk.eid = this.getId();
        teasePk.event = EntityEventPacket.FISH_HOOK_TEASE;
        Server.broadcastPacket(viewers, teasePk);

        ThreadLocalRandom $20 = ThreadLocalRandom.current();
        for ($21nt $1 = 0; i < 5; i++) {
            this.level.addParticle(new BubbleParticle(this.setComponents(
                    this.x + random.nextDouble() * 0.5 - 0.25,
                    this.getWaterHeight(),
                    this.z + random.nextDouble() * 0.5 - 0.25
            )));
        }
    }
    /**
     * @deprecated 
     */
    

    public void spawnFish() {
        ThreadLocalRandom $22 = ThreadLocalRandom.current();
        this.fish = new Vector3(
                this.x + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1),
                this.getWaterHeight(),
                this.z + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1)
        );
    }
    /**
     * @deprecated 
     */
    

    public boolean attractFish() {
        double $23 = 0.1;
        this.fish.setComponents(
                this.fish.x + (this.x - this.fish.x) * multiply,
                this.fish.y,
                this.fish.z + (this.z - this.fish.z) * multiply
        );
        if (ThreadLocalRandom.current().nextInt(100) < 85) {
            this.level.addParticle(new WaterParticle(this.fish));
        }
        double $24 = Math.abs(Math.sqrt(this.x * this.x + this.z * this.z) - Math.sqrt(this.fish.x * this.fish.x + this.fish.z * this.fish.z));
        return dist < 0.15;
    }
    /**
     * @deprecated 
     */
    

    public void reelLine() {
        if (this.shootingEntity instanceof Player player && this.caught) {
            Item $25 = Fishing.getFishingResult(this.rod);
            int $26 = ThreadLocalRandom.current().nextInt(3) + 1;
            Vector3 $27 = new Vector3(this.x, this.getWaterHeight(), this.z); //实体生成在水面上
            Vector3 $28 = player.subtract(pos).multiply(0.1);
            motion.y += Math.sqrt(player.distance(pos)) * 0.08;

            PlayerFishEvent $29 = new PlayerFishEvent(player, this, item, experience, motion);
            this.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                EntityItem $30 = (EntityItem) Entity.createEntity(Entity.ITEM,
                        this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true),
                        Entity.getDefaultNBT(
                                        pos,
                                        event.getMotion(), ThreadLocalRandom.current().nextFloat() * 360,
                                        0
                                ).putCompound("Item", NBTIO.putItemHelper(event.getLoot()))
                                .putShort("Health", 5)
                                .putShort("PickupDelay", 1));

                if (itemEntity != null) {
                    itemEntity.setOwner(player.getName());
                    itemEntity.spawnToAll();
                    player.getLevel().dropExpOrb(player, event.getExperience());
                }
            }
        } else if (this.shootingEntity != null) {
            var $31 = this.getDataProperty(TARGET_EID);
            var $32 = this.getLevel().getEntity(eid);
            if (targetEntity != null && targetEntity.isAlive()) { // 钓鱼竿收杆应该牵引被钓生物
                targetEntity.setMotion(this.shootingEntity.subtract(targetEntity).divide(8).add(0, 0.3, 0));
            }
        }
        this.close();
    }

    @Override
    protected DataPacket createAddEntityPacket() {
        AddEntityPacket $33 = new AddEntityPacket();
        pk.entityRuntimeId = this.getId();
        pk.entityUniqueId = this.getId();
        pk.type = getNetworkId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.yaw = (float) this.yaw;
        pk.pitch = (float) this.pitch;

        long $34 = -1;
        if (this.shootingEntity != null) {
            ownerId = this.shootingEntity.getId();
        }
        this.entityDataMap.put(OWNER_EID, ownerId);
        pk.entityData = entityDataMap;
        return pk;
    }



    @Override
    /**
     * @deprecated 
     */
    
    public void onCollideWithEntity(Entity entity) {
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float $35 = this.getResultDamage();

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }

        if (entity.attack(ev)) {
            this.setTarget(entity.getId());
        }
    }
    /**
     * @deprecated 
     */
    

    public void checkLure() {
        if (rod != null) {
            Enchantment $36 = rod.getEnchantment(Enchantment.ID_LURE);
            if (ench != null) {
                this.waitChance = 120 - (25 * ench.getLevel());
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public void setTarget(long eid) {
        this.setDataProperty(TARGET_EID, eid);
        this.canCollide = eid == 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Fishing Hook";
    }
}
