package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.weather.EntityLightningBolt;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BVector3;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.registry.Registries;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PetteriM1
 * @author GoodLucky777
 */
public class EntityThrownTrident extends SlenderProjectile {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getIdentifier() {
        return THROWN_TRIDENT;
    }

    private static final String $1 = "pickup";
    private static final String $2 = "Trident";
    private static final String $3 = "favoredSlot";
    private static final String $4 = "isCreative";
    private static final String $5 = "player";
    private static final String $6 = "Trident";
    private static final Vector3 $7 = new Vector3(0, 0, 0);
    private static final BlockVector3 $8 = new BlockVector3(0, 0, 0);

    public boolean alreadyCollided;
    protected Item trident;
    // Default Values

    protected float $9 = 0.04f;

    protected float $10 = 0.01f;

    protected int pickupMode;
    private Vector3 collisionPos;
    private BlockVector3 stuckToBlockPos;
    private int favoredSlot;
    private boolean player;
    // Enchantment
    private int loyaltyLevel;
    private boolean hasChanneling;
    private int riptideLevel;
    private int impalingLevel;
    /**
     * @deprecated 
     */
    

    public EntityThrownTrident(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }
    /**
     * @deprecated 
     */
    

    public EntityThrownTrident(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getLength() {
        return 0.25f;
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
        return 0.01f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return NAME_TRIDENT;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {
        super.setHasAge(false);
        super.initEntity();

        this.closeOnCollide = false;

        this.pickupMode = namedTag.contains(TAG_PICKUP) ? namedTag.getByte(TAG_PICKUP) : PICKUP_ANY;
        this.favoredSlot = namedTag.contains(TAG_FAVORED_SLOT) ? namedTag.getInt(TAG_FAVORED_SLOT) : -1;
        this.player = !namedTag.contains(TAG_PLAYER) || namedTag.getBoolean(TAG_PLAYER);

        if (namedTag.contains(TAG_CREATIVE)) {
            if (pickupMode == PICKUP_ANY && namedTag.getBoolean(TAG_CREATIVE)) {
                pickupMode = PICKUP_CREATIVE;
            }
            namedTag.remove(TAG_CREATIVE);
        }

        if (namedTag.contains(TAG_TRIDENT)) {
            this.trident = NBTIO.getItemHelper(namedTag.getCompound(TAG_TRIDENT));
            this.loyaltyLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_LOYALTY);
            this.hasChanneling = this.trident.hasEnchantment(Enchantment.ID_TRIDENT_CHANNELING);
            this.riptideLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
            this.impalingLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_IMPALING);
        } else {
            this.trident = Item.AIR;
            this.loyaltyLevel = 0;
            this.hasChanneling = false;
            this.riptideLevel = 0;
            this.impalingLevel = 0;
        }

        if (namedTag.contains("CollisionPos")) {
            ListTag<DoubleTag> collisionPosList = this.namedTag.getList("CollisionPos", DoubleTag.class);
            collisionPos = new Vector3(collisionPosList.get(0).data, collisionPosList.get(1).data, collisionPosList.get(2).data);
        } else {
            collisionPos = defaultCollisionPos.clone();
        }

        if (namedTag.contains("StuckToBlockPos")) {
            ListTag<IntTag> stuckToBlockPosList = this.namedTag.getList("StuckToBlockPos", IntTag.class);
            stuckToBlockPos = new BlockVector3(stuckToBlockPosList.get(0).data, stuckToBlockPosList.get(1).data, stuckToBlockPosList.get(2).data);
        } else {
            stuckToBlockPos = defaultStuckToBlockPos.clone();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.put(TAG_TRIDENT, NBTIO.putItemHelper(this.trident));
        this.namedTag.putByte(TAG_PICKUP, this.pickupMode);
        this.namedTag.putList("CollisionPos", new ListTag<DoubleTag>()
                .add(new DoubleTag(this.collisionPos.x))
                .add(new DoubleTag(this.collisionPos.y))
                .add(new DoubleTag(this.collisionPos.z))
        );
        this.namedTag.putList("StuckToBlockPos", new ListTag<IntTag>()
                .add(new IntTag(this.stuckToBlockPos.x))
                .add(new IntTag(this.stuckToBlockPos.y))
                .add(new IntTag(this.stuckToBlockPos.z))
        );
        this.namedTag.putInt(TAG_FAVORED_SLOT, this.favoredSlot);
        this.namedTag.putBoolean(TAG_PLAYER, this.player);
    }

    public Item getItem() {
        return this.trident != null ? this.trident.clone() : Item.AIR;
    }
    /**
     * @deprecated 
     */
    

    public void setItem(Item item) {
        this.trident = item.clone();
        this.loyaltyLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_LOYALTY);
        this.hasChanneling = this.trident.hasEnchantment(Enchantment.ID_TRIDENT_CHANNELING);
        this.riptideLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE);
        this.impalingLevel = this.trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_IMPALING);
    }
    /**
     * @deprecated 
     */
    

    public void setCritical() {
        this.setCritical(true);
    }
    /**
     * @deprecated 
     */
    

    public boolean isCritical() {
        return this.getDataFlag(EntityFlag.CRITICAL);
    }
    /**
     * @deprecated 
     */
    

    public void setCritical(boolean value) {
        this.setDataFlag(EntityFlag.CRITICAL, value);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getResultDamage() {
        int $11 = super.getResultDamage();

        if (this.isCritical()) {
            base += ThreadLocalRandom.current().nextInt(base / 2 + 2);
        }

        return base;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected double getBaseDamage() {
        return 8;
    }


    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.isCollided && !this.hadCollision) {
            this.getLevel().addSound(this, Sound.ITEM_TRIDENT_HIT_GROUND);
        }

        boolean $12 = super.onUpdate(currentTick);

        if (this.onGround || this.hadCollision) {
            this.setCritical(false);
        }

        if (this.noClip) {
            if (this.canReturnToShooter()) {
                Entity $13 = this.shootingEntity;
                double $14 = 0.05d * (double) loyaltyLevel;
                Vector3 $15 = new Vector3(shooter.x - this.x, shooter.y + shooter.getEyeHeight() - this.y, shooter.z - this.z);
                BVector3 $16 = BVector3.fromPos(vector3);
                vector3 = bVector.addToPos();
                this.setPosition(new Vector3(this.x + vector3.x * force, this.y + vector3.y * force, this.z + vector3.z * force));
                this.setRotation(bVector.getYaw(), bVector.getPitch());
                this.setMotion(vector3.multiply(force));
                hasUpdate = true;
            } else {
                if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS) && !this.closed) {
                    this.level.dropItem(this, this.trident);
                }
                this.close();
            }
        }

        return hasUpdate;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void spawnTo(Player player) {
        AddEntityPacket $17 = new AddEntityPacket();
        pk.type = Registries.ENTITY.getEntityNetworkId(THROWN_TRIDENT);
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.yaw = (float) this.yaw;
        pk.pitch = (float) this.pitch;
        pk.entityData = this.entityDataMap;
        player.dataPacket(pk);

        super.spawnTo(player);
    }


    @Override
    /**
     * @deprecated 
     */
    
    public void onCollideWithEntity(Entity entity) {
        if (this.noClip) {
            return;
        }

        if (this.alreadyCollided) {
            this.move(this.motionX, this.motionY, this.motionZ);
            return;
        }

        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float $18 = this.getResultDamage();
        if (this.impalingLevel > 0 && (entity.isTouchingWater() || (entity.getLevel().isRaining() && entity.getLevel().canBlockSeeSky(entity)))) {
            damage = damage + (2.5f * (float) this.impalingLevel);
        }

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }
        entity.attack(ev);
        this.getLevel().addSound(this, Sound.ITEM_TRIDENT_HIT);
        this.hadCollision = true;
        this.setCollisionPos(this);
        this.setMotion(new Vector3(this.getMotion().getX() * -0.01, this.getMotion().getY() * -0.1, this.getMotion().getZ() * -0.01));

        if (this.hasChanneling) {
            if (this.level.isThundering() && this.level.canBlockSeeSky(this)) {
                Position $19 = this.getPosition();
                EntityLightningBolt $20 = new EntityLightningBolt(pos.getChunk(), getDefaultNBT(pos));
                lighting.spawnToAll();
                this.getLevel().addSound(this, Sound.ITEM_TRIDENT_THUNDER);
            }
        }

        if (this.canReturnToShooter()) {
            this.getLevel().addSound(this, Sound.ITEM_TRIDENT_RETURN);
            this.setNoClip(true);
            this.hadCollision = false;
            this.setTridentRope(true);
        }
    }
    /**
     * @deprecated 
     */
    

    public int getPickupMode() {
        return this.pickupMode;
    }
    /**
     * @deprecated 
     */
    

    public void setPickupMode(int pickupMode) {
        this.pickupMode = pickupMode;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        if (this.noClip) {
            return;
        }

        for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1))) {
            this.setStuckToBlockPos(new BlockVector3(collisionBlock.getFloorX(), collisionBlock.getFloorY(), collisionBlock.getFloorZ()));
            if (this.canReturnToShooter()) {
                this.getLevel().addSound(this, Sound.ITEM_TRIDENT_RETURN);
                this.setNoClip(true);
                this.setTridentRope(true);
                return;
            }
            onCollideWithBlock(position, motion, collisionBlock);
        }
    }

    public Vector3 getCollisionPos() {
        return collisionPos;
    }
    /**
     * @deprecated 
     */
    

    public void setCollisionPos(Vector3 collisionPos) {
        this.collisionPos = collisionPos;
    }

    public BlockVector3 getStuckToBlockPos() {
        return stuckToBlockPos;
    }
    /**
     * @deprecated 
     */
    

    public void setStuckToBlockPos(BlockVector3 stuckToBlockPos) {
        this.stuckToBlockPos = stuckToBlockPos;
    }
    /**
     * @deprecated 
     */
    

    public int getFavoredSlot() {
        return favoredSlot;
    }
    /**
     * @deprecated 
     */
    

    public void setFavoredSlot(int favoredSlot) {
        this.favoredSlot = favoredSlot;
    }
    /**
     * @deprecated 
     */
    

    public boolean isCreative() {
        return getPickupMode() == EntityProjectile.PICKUP_CREATIVE;
    }
    /**
     * @deprecated 
     */
    

    public boolean isPlayer() {
        return player;
    }
    /**
     * @deprecated 
     */
    

    public void setPlayer(boolean player) {
        this.player = player;
    }
    /**
     * @deprecated 
     */
    

    public int getLoyaltyLevel() {
        return loyaltyLevel;
    }
    /**
     * @deprecated 
     */
    

    public void setLoyaltyLevel(int loyaltyLevel) {
        this.loyaltyLevel = loyaltyLevel;
        if (loyaltyLevel > 0) {
            this.trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_LOYALTY).setLevel(loyaltyLevel));
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_LOYALTY);
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean hasChanneling() {
        return hasChanneling;
    }
    /**
     * @deprecated 
     */
    

    public void setChanneling(boolean hasChanneling) {
        this.hasChanneling = hasChanneling;
        if (hasChanneling) {
            this.trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_CHANNELING));
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_CHANNELING);
        }
    }
    /**
     * @deprecated 
     */
    

    public int getRiptideLevel() {
        return riptideLevel;
    }
    /**
     * @deprecated 
     */
    

    public void setRiptideLevel(int riptideLevel) {
        this.riptideLevel = riptideLevel;
        if (riptideLevel > 0) {
            this.trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_RIPTIDE).setLevel(riptideLevel));
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_RIPTIDE);
        }
    }
    /**
     * @deprecated 
     */
    

    public int getImpalingLevel() {
        return impalingLevel;
    }
    /**
     * @deprecated 
     */
    

    public void setImpalingLevel(int impalingLevel) {
        this.impalingLevel = impalingLevel;
        if (impalingLevel > 0) {
            this.trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_IMPALING).setLevel(impalingLevel));
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_IMPALING);
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean getTridentRope() {
        return this.getDataFlag(EntityFlag.RETURN_TRIDENT);
    }
    /**
     * @deprecated 
     */
    

    public void setTridentRope(boolean tridentRope) {
        if (tridentRope) {
            this.setDataProperty(OWNER_EID, this.shootingEntity.getId());
        } else {
            this.setDataProperty(OWNER_EID, -1);
        }
        this.setDataFlag(EntityFlag.RETURN_TRIDENT, tridentRope);
    }
    /**
     * @deprecated 
     */
    

    public boolean canReturnToShooter() {
        if (this.loyaltyLevel <= 0) {
            return false;
        }

        if (this.getCollisionPos().equals(defaultCollisionPos) && this.getStuckToBlockPos().equals(defaultStuckToBlockPos)) {
            return false;
        }

        Entity $21 = this.shootingEntity;
        if (shooter != null) {
            if (shooter.isAlive() && shooter instanceof Player) {
                return !(((Player) shooter).isSpectator());
            }
        }
        return false;
    }
}
