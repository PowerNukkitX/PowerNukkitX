package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCactus;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.entity.custom.CustomEntityComponents;
import cn.nukkit.entity.custom.CustomEntityDefinition.Meta;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.weather.EntityWeather;
import cn.nukkit.event.entity.EntityDamageBlockedEvent;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventorySlice;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShield;
import cn.nukkit.item.ItemTurtleHelmet;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.TickCachedBlockIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public abstract class EntityLiving extends Entity implements EntityDamageable {
    public final static float DEFAULT_SPEED = 0.1f;
    protected int attackTime = 0;
    protected boolean invisible = false;
    protected float movementSpeed = DEFAULT_SPEED;
    protected int turtleTicks = 0;
    private boolean attackTimeByShieldKb;
    private int attackTimeBefore;

    public EntityLiving(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected float getGravity() {
        return 0.08f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    protected double getStepHeight() {
        if (isCustomEntity()) {
            return meta().getMaxAutoStep(CustomEntityComponents.MAX_AUTO_STEP).base();
        }
        return 0.5625;
    }

    @Override
    protected double getStepHeightControlled() {
        if (isCustomEntity()) {
            return meta().getMaxAutoStep(CustomEntityComponents.MAX_AUTO_STEP).controlled();
        }
        return 0.5625;
    }

    @Override
    protected double getStepHeightJumpPrevented() {
        if (isCustomEntity()) {
            return meta().getMaxAutoStep(CustomEntityComponents.MAX_AUTO_STEP).jumpPrevented();
        }
        return 0.5625;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("HealF")) {
            this.namedTag.putFloat("Health", this.namedTag.getShort("HealF"));
            this.namedTag.remove("HealF");
        }

        if (!this.namedTag.contains("Health") || !(this.namedTag.get("Health") instanceof FloatTag)) {
            this.namedTag.putFloat("Health", this.getMaxHealth());
        }

        setHealth(this.namedTag.getFloat("Health"));
    }

    @Override
    public void setHealth(float health) {
        boolean wasAlive = this.isAlive();
        super.setHealth(health);
        if (this.isAlive() && !wasAlive) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = EntityEventPacket.RESPAWN;
            Server.broadcastPacket(this.hasSpawned.values(), pk);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putFloat("Health", this.getHealth());
    }

    public boolean hasLineOfSight(Entity target) {
        return hasLineOfSight(target, 0.0);
    }

    public boolean hasLineOfSight(Entity target, double thickness) {
        if (this.level != target.level) return false;

        final boolean includeLiquidBlocks = false;
        final boolean includePassableBlocks = false;
        final double step = 0.25;

        final Vector3 selfPos = this.getPosition();
        final double selfEye = this.getEyeHeight();
        final double selfChest = this.getHeight() * 0.60;

        Vector3[] fromPoints = new Vector3[] {
            selfPos.add(0, selfEye + 0.001, 0),
            selfPos.add(0, selfChest + 0.001, 0),
        };

        final double tH = Math.max(0.0, target.getHeight());
        final double tEye = Math.max(0.0, target.getEyeHeight());
        final Vector3 tBase = target.getPosition();

        Vector3[] toPoints = new Vector3[] {
            tBase.add(0, Math.max(0.2 * tH, 0.25), 0),
            tBase.add(0, Math.max(0.5 * tH, 0.5),  0),
            tBase.add(0, Math.max(0.8 * tH, 0.75), 0),
            tBase.add(0, Math.max(tEye, 0.9),      0),
        };

        boolean useCorridor = thickness > 0.0;

        for (Vector3 from : fromPoints) {
            for (Vector3 to : toPoints) {
                Vector3 dir = to.subtract(from);
                if (dir.lengthSquared() < 1e-6) continue;

                if (!useCorridor) {
                    List<Block> visited = this.level.raycastBlocks(from, to, true, false, step, false, false, true);
                    boolean blocked = !visited.isEmpty() && this.level.blocksBlockSight(visited.get(visited.size() - 1), includeLiquidBlocks, includePassableBlocks);
                    if (!blocked) return true;
                    continue;
                }

                Vector3 right = new Vector3(-dir.z, 0, dir.x);
                if (right.lengthSquared() < 1e-6) right = new Vector3(1, 0, 0);
                right = right.normalize().multiply(thickness);

                Vector3 up = new Vector3(0, thickness, 0);

                Vector3[] offsets = new Vector3[] {
                    right, right.multiply(-1),
                    up,    up.multiply(-1),
                };

                boolean allClear = true;
                for (Vector3 o : offsets) {
                    Vector3 f = from.add(o.x, o.y, o.z);
                    Vector3 t = to.add(o.x, o.y, o.z);

                    List<Block> visited = this.level.raycastBlocks(f, t, true, false, step, false, false, true);
                    boolean blocked = !visited.isEmpty() && this.level.blocksBlockSight(visited.get(visited.size() - 1), includeLiquidBlocks, includePassableBlocks);

                    if (blocked) { allClear = false; break; }
                }

                if (allClear) return true;
            }
        }
        return false;
    }

    public void collidingWith(Entity ent) { // can override (IronGolem|Bats)
        ent.applyEntityCollision(this);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.noDamageTicks > 0 && source.getCause() != DamageCause.SUICIDE) {//ignore it if the cause is SUICIDE
            return false;
        } else if (this.attackTime > 0 && !attackTimeByShieldKb) {
            EntityDamageEvent lastCause = this.getLastDamageCause();
            if (lastCause != null && lastCause.getDamage() >= source.getDamage()) {
                return false;
            }
        }

        if (isBlocking() && this.blockedByShield(source)) {
            return false;
        }

        if (super.attack(source)) {
            if (source instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                if (source instanceof EntityDamageByChildEntityEvent) {
                    damager = ((EntityDamageByChildEntityEvent) source).getChild();
                }

                //Critical hit
                if (damager instanceof Player && !damager.onGround) {
                    AnimatePacket animate = new AnimatePacket();
                    animate.action = AnimatePacket.Action.CRITICAL_HIT;
                    animate.eid = getId();

                    this.getLevel().addChunkPacket(damager.getChunkX(), damager.getChunkZ(), animate);
                    this.getLevel().addSound(this, Sound.GAME_PLAYER_ATTACK_STRONG);

                    source.setDamage(source.getDamage() * 1.5f);
                }

                if (damager.isOnFire() && !(damager instanceof Player)) {
                    this.setOnFire(2 * this.server.getDifficulty());
                }

                double deltaX = this.x - damager.x;
                double deltaZ = this.z - damager.z;
                this.knockBack(damager, source.getDamage(), deltaX, deltaZ, ((EntityDamageByEntityEvent) source).getKnockBack());
            }

            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = this.getHealth() <= 0 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;
            Server.broadcastPacket(this.hasSpawned.values(), pk);

            this.attackTime = source.getAttackCooldown();
            this.attackTimeByShieldKb = false;
            this.scheduleUpdate();

            return true;
        } else {
            return false;
        }
    }

    public void knockBack(Entity attacker, double damage, double x, double z) {
        this.knockBack(attacker, damage, x, z, 0.4);
    }

    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }

        if(this instanceof Player player) {
            float totalReduction = 0.0f;

            InventorySlice armorInventory = player.getInventory().getArmorInventory();

            for (Item item : armorInventory.getContents().values()){
                if(!item.isNull()){
                    totalReduction += item.getKnockbackResistance();
                }
            }

            base *= (1.0 - totalReduction);
        }

        float resist = this.getKnockbackResistance();
        base *= (1.0 - resist);
        if (base <= 0) {
            return;
        }

        f = 1 / f;

        Vector3 motion = new Vector3(this.motionX, this.motionY, this.motionZ);

        motion.x /= 2d;
        motion.y /= 2d;
        motion.z /= 2d;
        motion.x += x * f * base;
        motion.y += base;
        motion.z += z * f * base;

        if (motion.y > base) {
            motion.y = base;
        }

        this.setMotion(motion);
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }
        super.kill();
        EntityDeathEvent ev = new EntityDeathEvent(this, this.getDrops());
        this.server.getPluginManager().callEvent(ev);

        var manager = this.server.getScoreboardManager();
        //测试环境中此项会null，所以说需要判空下
        if (manager != null) manager.onEntityDead(this);

        if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            for (cn.nukkit.item.Item item : ev.getDrops()) {
                this.getLevel().dropItem(this, item);
            }
            this.getLevel().dropExpOrb(this, getExperienceDrops());
        }
    }

    @Override
    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean isBreathing = !this.isInsideOfWater();

        if (this instanceof Player player) {
            if (isBreathing && player.getInventory().getHelmet() instanceof ItemTurtleHelmet) {
                turtleTicks = 200;
            } else if (turtleTicks > 0) {
                isBreathing = true;
                turtleTicks--;
            }

            if (player.isCreative() || player.isSpectator()) {
                isBreathing = true;
            }
        }

        this.setDataFlag(EntityFlag.BREATHING, isBreathing);

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            if (this.isInsideOfSolid()) {
                hasUpdate = true;
                this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 1));
            }

            if (this.isOnLadder() || this.hasEffect(EffectType.LEVITATION) || this.hasEffect(EffectType.SLOW_FALLING)) {
                this.resetFallDistance();
            }

            if (!this.hasEffect(EffectType.WATER_BREATHING) && !this.hasEffect(EffectType.CONDUIT_POWER) && this.isInsideOfWater()) {
                if (this instanceof EntitySwimmable || (this instanceof Player && (((Player) this).isCreative() || ((Player) this).isSpectator()))) {
                    this.setAirTicks(400);
                } else {
                    if (turtleTicks == 0 || turtleTicks == 200) {
                        hasUpdate = true;
                        int airTicks = this.getAirTicks() - tickDiff;

                        if (airTicks <= -20) {
                            airTicks = 0;
                            this.attack(new EntityDamageEvent(this, DamageCause.DROWNING, 2));
                        }

                        setAirTicks(airTicks);
                    }
                }
            } else {
                if (this instanceof EntitySwimmable) {
                    hasUpdate = true;
                    int airTicks = getAirTicks() - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 2));
                    }

                    setAirTicks(airTicks);
                } else {
                    int airTicks = getAirTicks();

                    if (airTicks < 400) {
                        setAirTicks(Math.min(400, airTicks + tickDiff * 5));
                    }
                }
            }
        }

        if (this.attackTime > 0) {
            this.attackTime -= tickDiff;
            if (this.attackTime <= 0) {
                attackTimeByShieldKb = false;
            }
            hasUpdate = true;
        }

        //吐槽：性能不要了是吧放EntityLiving这里
        //逻辑迁移到EntityVehicle去了
//        if (this.riding == null) {
//            for (Entity entity : level.fastNearbyEntities(this.boundingBox.grow(0.20000000298023224, 0.0D, 0.20000000298023224), this)) {
//                if (entity instanceof EntityRideable) {
//                    this.collidingWith(entity);
//                }
//            }
//        }

        // Used to check collisions with magma / cactus blocks
        // Math.round处理在某些条件下 出现x.999999的坐标条件,这里选择四舍五入
        var block = this.level.getTickCachedBlock(getFloorX(), (int) (Math.round(this.y) - 1), getFloorZ());
        if (block instanceof BlockMagma || block instanceof BlockCactus) block.onEntityCollide(this);

        return hasUpdate;
    }

    /**
     * Defines the drops after the entity's death
     */
    public Item[] getDrops() {
        return Item.EMPTY_ARRAY;
    }

    public Integer getExperienceDrops() {
        return 0;
    }

    public Block[] getLineOfSight(int maxDistance) {
        return this.getLineOfSight(maxDistance, 0);
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength) {
        return this.getLineOfSight(maxDistance, maxLength, new String[]{});
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength, String[] transparent) {
        if (maxDistance > 120) {
            maxDistance = 120;
        }

        if (transparent != null && transparent.length == 0) {
            transparent = null;
        }

        List<Block> blocks = new ArrayList<>();

        var itr = new TickCachedBlockIterator(this.level, this.getPosition(), this.getDirectionVector(), this.getEyeHeight(), maxDistance);

        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);

            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }

            String id = block.getId();

            if (transparent == null) {
                if (!block.isAir()) {
                    break;
                }
            } else {
                if (Arrays.binarySearch(transparent, id) < 0) {
                    break;
                }
            }
        }

        return blocks.toArray(Block.EMPTY_ARRAY);
    }

    public Block getTargetBlock(int maxDistance) {
        return getTargetBlock(maxDistance, new String[]{});
    }

    public Block getTargetBlock(int maxDistance, String[] transparent) {
        try {
            Block[] blocks = this.getLineOfSight(maxDistance, 1, transparent);
            Block block = blocks[0];
            if (block != null) {
                if (transparent != null && transparent.length != 0) {
                    if (Arrays.binarySearch(transparent, block.getId()) < 0) {
                        return block;
                    }
                } else {
                    return block;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /** Returns the default movement speed of the entity */
    public float getDefaultSpeed() {
        if (isCustomEntity()) {
            return meta().getMovement(CustomEntityComponents.MOVEMENT).moveSpeed();
        }
        return EntityLiving.DEFAULT_SPEED;
    }

    /** Returns the current movement speed of the entity */
    public float getMovementSpeed() {
        return this.movementSpeed;
    }

    /** Returns the default movement multiplier of the entity */
    public float getSpeedMultiplier() {
        if (isCustomEntity()) {
            return meta().getMovement(CustomEntityComponents.MOVEMENT).multiplier();
        }
        return 1.0f;
    }

    /** The radius of the area of blocks the entity will attempt to stay within around a target. */
    public int getFollowRadius() {
        if (isCustomEntity()) {
            return meta().getFollowRange(CustomEntityComponents.FOLLOW_RANGE).radius();
        }
        return 0;
    }

    /** The maximum distance the mob will go from a target. */
    public int getFollowMax() {
        if (isCustomEntity()) {
            return meta().getFollowRange(CustomEntityComponents.FOLLOW_RANGE).max();
        }
        return 0;
    }

    /**
     * Set the movement speed of this Entity.
     *
     * @param speed Speed value
     */
    public void setMovementSpeed(float speed) {
        this.movementSpeed = (float) NukkitMath.round(speed, 2);
    }

    /** Gets the attack power of the entity. */
    public int getAttackPower() {
        if (isCustomEntity()) {
            Meta.Attack atk = meta().getAttack(CustomEntityComponents.ATTACK);
            int min = atk.min();
            int max = atk.max();
            if (max > min) return ThreadLocalRandom.current().nextInt(min, max + 1);
            return max;
        }
        return 1;
    }

    public int getAirTicks() {
        return this.getDataProperty(AIR_SUPPLY);
    }

    public void setAirTicks(int ticks) {
        this.setDataProperty(AIR_SUPPLY, ticks);
    }

    protected boolean blockedByShield(EntityDamageEvent source) {
        Entity damager = null;
        if (source instanceof EntityDamageByChildEntityEvent) {
            damager = ((EntityDamageByChildEntityEvent) source).getChild();
        } else if (source instanceof EntityDamageByEntityEvent) {
            damager = ((EntityDamageByEntityEvent) source).getDamager();
        }
        if (damager == null || damager instanceof EntityWeather || !this.isBlocking()) {
            return false;
        }

        Vector3 entityPos = damager.getPosition();
        Vector3 direction = this.getDirectionVector();
        Vector3 normalizedVector = this.getPosition().subtract(entityPos).normalize();
        boolean blocked = (normalizedVector.x * direction.x) + (normalizedVector.z * direction.z) < 0.0;
        boolean knockBack = !(damager instanceof EntityProjectile);
        EntityDamageBlockedEvent event = new EntityDamageBlockedEvent(this, source, knockBack, true);
        if (!blocked || !source.canBeReducedByArmor()) {
            event.setCancelled();
        }

        getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        if (event.getKnockBackAttacker() && damager instanceof EntityLiving attacker) {
            double deltaX = attacker.getX() - this.getX();
            double deltaZ = attacker.getZ() - this.getZ();
            attacker.knockBack(this, 0, deltaX, deltaZ);
            attacker.attackTime = 10;
            attacker.attackTimeByShieldKb = true;
        }

        onBlock(damager, source, event.getAnimation());
        return true;
    }

    protected void onBlock(Entity entity, EntityDamageEvent event, boolean animate) {
        if (animate) {
            getLevel().addSound(this, Sound.ITEM_SHIELD_BLOCK);
        }
    }

    public boolean isBlocking() {
        if(this.getDataFlag(EntityFlag.BLOCKING)) {
            if(this instanceof InventoryHolder holder) {
                if(holder.getInventory() instanceof HumanInventory inventory) {
                    return inventory.getItemInHand() instanceof ItemShield;
                }
            }
        }
        return false;
    }

    public void setBlocking(boolean value) {
        this.setDataFlagExtend(EntityFlag.BLOCKING, value, false);
        this.setDataFlagExtend(EntityFlag.TRANSITION_BLOCKING, value, true);
    }

    @Override
    public boolean isPersistent() {
        return (isCustomEntity() && meta().getBoolean(CustomEntityComponents.PERSISTENT, false))
            || (this.namedTag.contains("Persistent") && this.namedTag.getBoolean("Persistent"));
    }

    public void preAttack(Player player) {
        if (attackTimeByShieldKb) {
            attackTimeBefore = attackTime;
            attackTime = 0;
        }
    }

    public void postAttack(Player player) {
        if (attackTimeByShieldKb && attackTime == 0) {
            attackTime = attackTimeBefore;
        }
    }

    public int getAttackTime() {
        return attackTime;
    }

    public boolean isAttackTimeByShieldKb() {
        return attackTimeByShieldKb;
    }

    public int getAttackTimeBefore() {
        return attackTimeBefore;
    }

    public void recalcMovementSpeedFromEffects() {
        float base = this.getDefaultSpeed() * this.getSpeedMultiplier();
        float mul = 1.0f;

        Effect speed = this.getEffect(EffectType.SPEED);
        int speedLvl = (speed != null) ? (Math.max(0, speed.getAmplifier()) + 1) : 0;

        Effect slow = this.getEffect(EffectType.SLOWNESS);
        int slowLvl = (slow != null) ? (Math.max(0, slow.getAmplifier()) + 1) : 0;

        if (slowLvl >= 7) {
            mul = 0.0f;
        } else {
            if (speedLvl > 0) {
                mul *= (1.0f + 0.20f * speedLvl);
            }
            if (slowLvl > 0) {
                mul *= Math.max(0.0f, 1.0f - 0.15f * slowLvl);
            }
        }

        if (this instanceof Player p && p.isSprinting()) mul *= 1.3f;
        float newSpeed = base * mul;

        if (this instanceof Player) {
            ((Player) this).setMovementSpeed(newSpeed, true);
        } else {
            this.setMovementSpeed(newSpeed);
        }
    }
}
