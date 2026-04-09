package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.components.NameableComponent;
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
import cn.nukkit.level.particle.WaterWakeParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.ItemHelper;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.AddActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
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
    public String getIdentifier() {
        return FISHING_HOOK;
    }

    public int waitChance = 120;
    public int waitTimer = 240;
    public boolean attracted = false;
    public int attractTimer = 0;
    public boolean caught = false;
    public int caughtTimer = 0;
    @SuppressWarnings("java:S1845")
    public boolean canCollide = true;

    public Vector3 fish = null;

    public Item rod = null;

    public EntityFishingHook(IChunk chunk, NbtMap nbt) {
        this(chunk, nbt, null);
    }

    public EntityFishingHook(IChunk chunk, NbtMap nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
        // https://github.com/PowerNukkit/PowerNukkit/issues/267
        if (this.age > 0) {
            this.close();
        }
    }

    @Override
    public float getLength() {
        return 0.2f;
    }

    @Override
    public float getGravity() {
        return 0.05f;
    }

    @Override
    public float getDrag() {
        return 0.04f;
    }

    @Override
    public boolean canCollide() {
        return this.canCollide;
    }

    @Override
    public NameableComponent getComponentNameable() {
        return DEFAULT_NOT_NAMEABLE;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean hasUpdate;
        long target = getDataProperty(ActorDataTypes.TARGET);
        if (target != 0L) {
            Entity entity = getLevel().getEntity(target);
            if (entity == null || !entity.isAlive()) {
                setDataProperty(ActorDataTypes.TARGET, 0L);
            } else {
                Vector3f offset = entity.getAttachmentOffset(this);
                setPosition(new Vector3(entity.x + offset.x, entity.y + offset.y, entity.z + offset.z));
            }
            return false;
        }

        hasUpdate = super.onUpdate(currentTick);

        boolean inWater = this.isInsideOfWater();
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
                    ThreadLocalRandom random = ThreadLocalRandom.current();
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

    public int getWaterHeight() {
        for (int y = this.getFloorY(); y <= level.getMaxHeight(); y++) {
            String id = this.level.getBlockIdAt(this.getFloorX(), y, this.getFloorZ());
            if (Objects.equals(id, Block.AIR)) {
                return y;
            }
        }
        return this.getFloorY();
    }

    public void fishBites() {
        Collection<Player> viewers = this.getViewers().values();

        final ActorEventPacket pk = new ActorEventPacket();
        pk.setTargetRuntimeID(this.getId());
        pk.setType(ActorEvent.FISHHOOK_HOOKTIME);

        final ActorEventPacket bubblePk = new ActorEventPacket();
        bubblePk.setTargetRuntimeID(this.getId());
        bubblePk.setType(ActorEvent.FISHHOOK_BUBBLE);

        final ActorEventPacket teasePk = new ActorEventPacket();
        teasePk.setTargetRuntimeID(this.getId());
        teasePk.setType(ActorEvent.FISHHOOK_TEASE);

        Server.broadcastPacket(viewers, pk);
        Server.broadcastPacket(viewers, bubblePk);
        Server.broadcastPacket(viewers, teasePk);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 5; i++) {
            this.level.addParticle(new BubbleParticle(this.setComponents(
                    this.x + random.nextDouble() * 0.5 - 0.25,
                    this.getWaterHeight(),
                    this.z + random.nextDouble() * 0.5 - 0.25
            )));
        }
    }

    public void spawnFish() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        this.fish = new Vector3(
                this.x + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1),
                this.getWaterHeight(),
                this.z + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1)
        );
    }

    public boolean attractFish() {
        double multiply = 0.1;
        this.fish.setComponents(
                this.fish.x + (this.x - this.fish.x) * multiply,
                this.fish.y,
                this.fish.z + (this.z - this.fish.z) * multiply
        );
        if (ThreadLocalRandom.current().nextInt(100) < 85) {
            this.level.addParticle(new WaterWakeParticle(this.fish));
        }
        double dist = Math.abs(Math.sqrt(this.x * this.x + this.z * this.z) - Math.sqrt(this.fish.x * this.fish.x + this.fish.z * this.fish.z));
        return dist < 0.15;
    }

    public void reelLine() {
        if (this.shootingEntity instanceof Player player && this.caught) {
            Item item = Fishing.getFishingResult(this.rod);
            int experience = ThreadLocalRandom.current().nextInt(3) + 1;
            Vector3 pos = new Vector3(this.x, this.getWaterHeight(), this.z); //实体生成在水面上
            Vector3 motion = player.subtract(pos).multiply(0.1);
            motion.y += Math.sqrt(player.distance(pos)) * 0.08;

            PlayerFishEvent event = new PlayerFishEvent(player, this, item, experience, motion);
            this.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                EntityItem itemEntity = (EntityItem) Entity.createEntity(Entity.ITEM,
                        this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true),
                        Entity.getDefaultNBT(
                                        pos,
                                        event.getMotion(), ThreadLocalRandom.current().nextFloat() * 360,
                                        0
                                ).toBuilder().putCompound("Item", ItemHelper.write(event.getLoot(), null))
                                .putShort("Health", (short) 5)
                                .putShort("PickupDelay", (short) 1)
                                .build());

                if (itemEntity != null) {
                    itemEntity.setOwner(player.getName());
                    itemEntity.spawnToAll();
                    player.getLevel().dropExpOrb(player, event.getExperience());
                }
            }
        } else if (this.shootingEntity != null) {
            var eid = this.getDataProperty(ActorDataTypes.TARGET);
            var targetEntity = this.getLevel().getEntity(eid);
            if (targetEntity != null && targetEntity.isAlive()) { // 钓鱼竿收杆应该牵引被钓生物
                targetEntity.setMotion(this.shootingEntity.subtract(targetEntity).divide(8).add(0, 0.3, 0));
            }
        }
        this.close();
    }

    @Override
    protected BedrockPacket createAddEntityPacket() {
        final AddActorPacket pk = new AddActorPacket();
        pk.setTargetActorID(this.getId());
        pk.setTargetRuntimeID(this.getId());
        pk.setEntityType(this.getNetworkId());
        pk.setPosition(org.cloudburstmc.math.vector.Vector3f.from(this.x, this.y, this.z));
        pk.setVelocity(org.cloudburstmc.math.vector.Vector3f.from(this.motionX, this.motionY, this.motionZ));
        pk.setRotation(org.cloudburstmc.math.vector.Vector2f.from(this.pitch, this.yaw));

        long ownerId = -1;
        if (this.shootingEntity != null) {
            ownerId = this.shootingEntity.getId();
        }
        this.entityDataMap.put(ActorDataTypes.OWNER, ownerId);
        pk.setActorData(this.entityDataMap);
        return pk;
    }


    @Override
    public void onCollideWithEntity(Entity entity) {
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();

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

    public void checkLure() {
        if (rod != null) {
            Enchantment ench = rod.getEnchantment(Enchantment.ID_LURE);
            if (ench != null) {
                this.waitChance = 120 - (25 * ench.getLevel());
            }
        }
    }

    public void setTarget(long eid) {
        this.setDataProperty(ActorDataTypes.OWNER, eid);
        this.canCollide = eid == 0;
    }

    @Override
    public String getOriginalName() {
        return "Fishing Hook";
    }
}
