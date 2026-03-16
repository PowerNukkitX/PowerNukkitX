package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCactus;
import cn.nukkit.block.BlockMagma;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.HealableComponent;
import cn.nukkit.entity.components.NameableComponent;
import cn.nukkit.entity.components.TameableComponent;
import cn.nukkit.entity.custom.CustomEntityComponents;
import cn.nukkit.entity.custom.CustomEntityDefinition.Meta;
import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.entity.passive.EntityWanderingTrader;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.weather.EntityWeather;
import cn.nukkit.event.entity.EntityDamageBlockedEvent;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.inventory.EntityHandItem;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventorySlice;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShield;
import cn.nukkit.item.ItemTurtleHelmet;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.TickCachedBlockIterator;
import cn.nukkit.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;


@Slf4j
public abstract class EntityLiving extends Entity implements EntityDamageable {
    protected int attackTime = 0;
    protected boolean invisible = false;
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
            this.namedTag.putFloat("Health", this.getHealthMax());
        }

        this.setHealthMax(this.getHealthMax());
        setHealthCurrent(this.namedTag.getFloat("Health"));

        // Load Tame and Chest from NBT
        if (this.namedTag.contains("Tamed")) {
            boolean hasTagTamed = this.namedTag.getBoolean("Tamed");
            if (hasTagTamed || this.isTamed()) this.setDataFlag(EntityFlag.TAMED, true, false);
        }
        if (this.namedTag.contains("Chested")) {
            this.setDataFlag(EntityFlag.CHESTED, this.namedTag.getBoolean("Chested"), true);
        }
        updateInventoryFlags();

        if (this.isAgeable()) {
            restoreBabyStateFromNbt();
            if (this.isBaby()) ensureGrowLoaded();
        }

        if (this.canBeSaddled()) {
            if (this.namedTag.contains("saddled")) {
                this.setDataFlag(EntityFlag.SADDLED, this.namedTag.getBoolean("saddled"));
            } else {
                this.setDataFlag(EntityFlag.SADDLED, false);
            }
        }

        if (this.isBaby()) loadParentFromNBT();

        if (!this.isPlayer && this.namedTag != null && this.namedTag.contains(NBT_RIDING_UUID)) {
            this.restoreMountTries = 60;
        }
    }


    protected void loadParentFromNBT() {
        if (!(this instanceof EntityIntelligent ei)) return;
        if (this.namedTag == null) return;
        if (!this.isBaby()) return;
        if (ei.getMemoryStorage().notEmpty(CoreMemoryTypes.PARENT)) return;

        UUID wanted = null;
        String parentStr = this.namedTag.getString("Parent");
        if (parentStr != null && !parentStr.isEmpty()) {
            try {
                wanted = UUID.fromString(parentStr);
            } catch (IllegalArgumentException ignored) {
                wanted = null;
            }
        }

        List<Entity> nearby = new ArrayList<>();
        EntityQueryOptions opts = new EntityQueryOptions()
                .location(this)
                .maxDistance(8);

        this.level.getEntities(opts, nearby);

        Entity foundByUuid = null;
        Entity fallbackSameTypeAdult = null;
        double bestUuidD2 = Double.MAX_VALUE;
        double bestFallbackD2 = Double.MAX_VALUE;

        for (Entity e : nearby) {
            if (e == null || e == this) continue;

            double d2 = this.distanceSquared(e);

            if (wanted != null) {
                var uid = e.getUniqueId();
                if (uid != null && wanted.equals(uid) && d2 < bestUuidD2) {
                    bestUuidD2 = d2;
                    foundByUuid = e;
                    continue;
                }
            }

            if (e instanceof EntityCreature c) {
                if (!c.getIdentifier().equals(this.getIdentifier())) continue;
                if (c.isBaby()) continue;
                if (d2 < bestFallbackD2) {
                    bestFallbackD2 = d2;
                    fallbackSameTypeAdult = c;
                }
            }
        }

        Entity chosen = (foundByUuid != null) ? foundByUuid : fallbackSameTypeAdult;
        if (chosen == null) return;

        ei.getMemoryStorage().put(CoreMemoryTypes.PARENT, chosen);

        var chosenUuid = chosen.getUniqueId();
        if (chosenUuid != null) {
            this.namedTag.putString("Parent", chosenUuid.toString());
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (restoreMountTries > 0) {
            restoreMountTries--;
            if ((restoreMountTries % 4) == 0) tryRestoreMountLink();
            if (restoreMountTries == 0 && this.riding == null) this.namedTag.remove(NBT_RIDING_UUID);
        }

        return super.onUpdate(currentTick);
    }

    private void tryRestoreMountLink() {
        String uuidStr = this.namedTag.getString(NBT_RIDING_UUID);
        UUID wanted;
        try {
            wanted = UUID.fromString(uuidStr);
        } catch (IllegalArgumentException ignored) {
            this.namedTag.remove(NBT_RIDING_UUID);
            restoreMountTries = 0;
            return;
        }

        double radius = 8;
        for (Entity e : this.level.getNearbyEntities(this.boundingBox.grow(radius, radius, radius), this)) {
            if (e == null || e.closed) continue;
            if (e instanceof Player) continue;

            if (wanted.equals(e.getUniqueId())) {
                e.mountEntity(this, false);
                if (this.riding != null) restoreMountTries = 0;
                return;
            }
        }
    }

    @Override
    public void setHealthCurrent(float health) {
        boolean wasAlive = this.isAlive();
        super.setHealthCurrent(health);
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
        this.namedTag.putFloat("Health", this.getHealthCurrent());

        if (!isAgeable()) return;
        if (!isBaby()) {
            if (namedTag.contains(TAG_ENTITY_GROW_LEFT)) namedTag.remove(TAG_ENTITY_GROW_LEFT);
            return;
        }
        ensureGrowLoaded();
        if (growDirty) {
            namedTag.putInt(TAG_ENTITY_GROW_LEFT, Math.max(0, ticksGrowLeft));
            growDirty = false;
        }

        if (this.canBeSaddled()) {
            this.namedTag.putBoolean("saddled", isSaddled());
        }
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
                    boolean blocked = !visited.isEmpty() && this.level.blocksBlockSight(visited.getLast(), includeLiquidBlocks, includePassableBlocks);
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
                    boolean blocked = !visited.isEmpty() && this.level.blocksBlockSight(visited.getLast(), includeLiquidBlocks, includePassableBlocks);

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
            pk.event = this.getHealthCurrent() <= 0 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;
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
        if (f < 1.0e-6) {
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

        this.setMotion(motion);
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }
        super.kill();
        Item weapon = Item.AIR;
        if (this.getLastDamageCause() instanceof EntityDamageByEntityEvent event
                && event.getDamager() instanceof EntityHandItem handItem) {
            weapon = handItem.getItemInHand();
        }

        EntityDeathEvent ev = new EntityDeathEvent(this, this.getDrops(weapon));
        this.server.getPluginManager().callEvent(ev);

        var manager = this.server.getScoreboardManager();
        // This will be null in the test environment, so it is necessary to check for null values.
        if (manager != null) manager.onEntityDead(this);
        if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            for (Item item : ev.getDrops()) {
                this.getLevel().dropItem(this, item);
            }
        }
        this.getLevel().dropExpOrb(this, getExperienceDrops());
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

        // Used to check collisions with magma / cactus blocks
        // Math.round handles coordinates with x.999999 under certain conditions; here, rounding is chosen.
        var block = this.level.getTickCachedBlock(getFloorX(), (int) (Math.round(this.y) - 1), getFloorZ());
        if (block instanceof BlockMagma || block instanceof BlockCactus) block.onEntityCollide(this);

        return hasUpdate;
    }

    /**
     * Defines the drops after the entity's death without looting
     * @deprecated Use {@link #getDrops(Item)}
     */
    @Deprecated
    public Item[] getDrops() {
        return Item.EMPTY_ARRAY;
    }

    /**
     * Defines the drops of the entity adjusted with the enchantments of the item
     * @param weapon - The weapon that was used to kill the entity.
     * @since 12/12/2025
     */
    public Item[] getDrops(@NotNull Item weapon) {
        return this.getDrops();
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
                blocks.removeFirst();
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

    /** The radius of the area blocks the entity will attempt to stay within around a target. */
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
        EnumSet<EntityFlag> ext = this.getEntityDataMap().getOrCreateFlags2();

        boolean changed;
        if (value) {
            changed = ext.add(EntityFlag.BLOCKING);
        } else {
            changed = ext.remove(EntityFlag.BLOCKING);
        }

        if (!changed) return;

        this.getEntityDataMap().put(EntityDataTypes.FLAGS_2, ext);

        EnumSet<EntityFlag> wire = EnumSet.copyOf(ext);
        if (value) {
            wire.add(EntityFlag.TRANSITION_BLOCKING);
        } else {
            wire.remove(EntityFlag.TRANSITION_BLOCKING);
        }

        EntityDataMap delta = new EntityDataMap();
        delta.put(EntityDataTypes.FLAGS_2, wire);
        sendData(this.hasSpawned.values().toArray(Player.EMPTY_ARRAY), delta);
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
        float base = this.getMovementSpeedDefault() * this.getSpeedMultiplier();
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

    public void initHome() {
        if (!this.hasHome() || !(this instanceof EntityIntelligent ei)) return;

        if (ei.namedTag.contains("HomeX")) {
            Vector3 home = new Vector3(
                ei.namedTag.getDouble("HomeX"),
                ei.namedTag.getDouble("HomeY"),
                ei.namedTag.getDouble("HomeZ")
            );
            ei.getMemoryStorage().put(CoreMemoryTypes.NEAREST_BLOCK, ei.level.getBlock(home));
        }
    }

    public void setHomePosition() {
        if (!this.hasHome() || !(this instanceof EntityIntelligent ei)) return;

        int x = ei.getFloorX();
        int y = ei.getFloorY();
        int z = ei.getFloorZ();
        Block home = ei.level.getBlock(x, y, z);

        CompoundTag tag = ei.namedTag;
        tag.putDouble("HomeX", home.x);
        tag.putDouble("HomeY", home.y);
        tag.putDouble("HomeZ", home.z);
        ei.getMemoryStorage().put(CoreMemoryTypes.NEAREST_BLOCK, home);
    }

    public Block getHomePosition() {
        if (!this.hasHome() || !(this instanceof EntityIntelligent ei)) return null;
        if (!this.namedTag.contains("HomeX")) return null;

        Vector3 homeLoc = new Vector3(
            this.namedTag.getDouble("HomeX"),
            this.namedTag.getDouble("HomeY"),
            this.namedTag.getDouble("HomeZ")
        );

        return ei.level.getBlock(homeLoc);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        return handleEntityComponentsInteraction(player, item);
    }


    /**
     * Entity Components Interactions Start
     */

    /**
     * Generic handler for interact-with-item logic gating from entity components.
     *
     * @return true if the item interaction was effectively applied.
     */
    public boolean handleEntityComponentsInteraction(Player player, Item item) {
        if (player == null || item.isNull()) return false;

        boolean handled = false;
        boolean healed  = false;
        int currentTick = this.getLevel().getTick();

        // PIPELINE ORDER:

        // 0) Tameable
        if (isTameable() && !isTamed() && !isAngry()) handled = tryTame(player, item);
        // 1) Breedable
        if (!handled && isBreedable() && !isBaby()) handled = tryBreed(player, item, currentTick);
        // 2) Force Growth
        if (!handled) handled = tryGrow(player, item);
        // 3) Healable / Feedable (breeding items can also be used for healing)
        if (isHealable()) healed = tryHeal(player, item);
        // 4) Feed items play effects
        if (handled || healed) finishFoodPipeline(player, item, currentTick);

        // 4) Nameable
        if (!(handled || healed) && item.getId().equals(Item.NAME_TAG) && isNameable() && !player.isAdventure()) {
            handled = trySetNameTag(player, item);
        }

        return handled || healed;
    }

    // Try tame by using item / food
    protected boolean tryTame(Player player, Item item) {
        if (!this.isTameable()) return false;
        TameableComponent tameable = getComponentTameable();
        if (!isTameableItem(item, tameable)) return false;

        float p = tameable.resolvedProbability();
        boolean success = p >= 1.0f || Utils.rand(0.0f, 1.0f) <= p;

        if (success) {
            this.onTameSuccess(player);
        } else this.onTameFail(player);

        return true;
    }

    protected boolean isTameableItem(Item item, TameableComponent tameable) {
        if (item.isNull()) return false;

        String key = item.getId();
        if (key == null || key.isEmpty()) return false;

        return tameable.resolvedTameItems().contains(key.trim().toLowerCase(Locale.ROOT));
    }


    // Try breed by using item / food
    protected boolean tryBreed(Player player, Item item, int currentTick) {
        BreedableComponent breedable = getComponentBreedable();
        if (breedable == null || breedable.isEmpty()) return false;

        if (!(this instanceof EntityIntelligent ei)) return false;
        if (!isBreedableItem(item, breedable)) return false;
        if (this.isBaby()) return false;

        // 1) Entity filters gate
        EntityFilter filter = breedable.loveFilters();
        if (filter != null) {
            EntityFilter.Context ctx = EntityFilter.Context.of(player, item).withOther(player);
            if (!filter.test(this, ctx)) return false;
        }

        // 2) Check if entity is required to be tamed for breeding state
        if (breedable.resolvedRequireTame() && !isTamed()) {
            return false;
        }

        // 3) Validate if the entity can be start breeding state while sitting
        if (!breedable.resolvedAllowSitting() && (this instanceof EntityCanSit ecs) && ecs.isSitting()) {
            return false;
        }

        // 4) Check if entity is required to be full health before breeding
        if (breedable.resolvedRequireFullHealth() && this.getHealthCurrent() < this.getHealthMax()) {
            return false;
        }

        if (Boolean.TRUE.equals(ei.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE))) {
            return false;
        }

        if (!hasEnvironmentRequirements(breedable)) {
            return false;
        }

        int cooldownTicks = (int) (breedable.resolvedBreedCooldown() * 20.0f);
        if (cooldownTicks > 0 && ei.getMemoryStorage().notEmpty(CoreMemoryTypes.LAST_IN_LOVE_TIME)) {
            int lastLove = ei.getMemoryStorage().get(CoreMemoryTypes.LAST_IN_LOVE_TIME);

            if (lastLove > currentTick) {
                ei.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, currentTick);
                lastLove = currentTick;
            }

            if ((currentTick - lastLove) < cooldownTicks) return false;
        }

        ei.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, true);
        ei.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, currentTick);
        ei.setDataFlag(EntityFlag.IN_LOVE, true);

        sendBreedingAnimation(item);
        sendLoveParticles();

        return true;
    }

    protected boolean isBreedableItem(Item item, BreedableComponent breedable) {
        if (item.isNull()) return false;

        String key = item.getId();
        if (key == null || key.isEmpty()) return false;

        return breedable.resolvedBreedItems().contains(key.trim().toLowerCase(Locale.ROOT));
    }

    protected boolean hasEnvironmentRequirements(BreedableComponent breedable) {
        List<BreedableComponent.EnvironmentRequirement> reqs = breedable.environmentRequirements();
        if (reqs == null || reqs.isEmpty()) return true;

        for (BreedableComponent.EnvironmentRequirement req : reqs) {
            if (req == null || req.isEmpty()) continue;

            Set<String> blockTypes = req.blockTypes();
            if (blockTypes == null || blockTypes.isEmpty()) continue;

            int needed = (req.count() == null) ? 1 : Math.max(1, req.count());
            float r = (req.radius() == null) ? 16.0f : req.radius();

            int radius = (int) Math.floor(Math.max(0.0f, r));

            int found = this.countBlocksInRadius(radius, needed, b -> blockTypes.contains(b.getId()));

            if (found >= needed) return true;
        }

        return false;
    }

    protected int countBlocksInRadius(int radius, int needed, Predicate<Block> matcher) {
        if (radius < 0 || needed <= 0 || matcher == null) return 0;
        if (this.level == null) return 0;

        int count = 0;

        int cx = (int) Math.floor(this.x);
        int cy = (int) Math.floor(this.y);
        int cz = (int) Math.floor(this.z);

        int minX = cx - radius;
        int maxX = cx + radius;
        int minY = cy - radius;
        int maxY = cy + radius;
        int minZ = cz - radius;
        int maxZ = cz + radius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block b = this.level.getBlock(x, y, z);
                    if (b != null && matcher.test(b) && ++count >= needed) return count;
                }
            }
        }

        return count;
    }

    public void sendBreedingAnimation(Item item) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.event = EntityEventPacket.EATING_ITEM;
        pk.eid = this.getId();
        pk.data =  item.getFullId();
        Server.broadcastPacket(this.getViewers().values(), pk);
    }

    public void sendLoveParticles() {
        EntityEventPacket pk = new EntityEventPacket();
        pk.event = EntityEventPacket.LOVE_PARTICLES;
        pk.eid = this.getId();
        pk.data = 0;
        Server.broadcastPacket(this.getViewers().values(), pk);
    }


    // Try force growth by using item / food
    protected boolean tryGrow(Player player, Item item) {
        if (this.isBaby() && this.isAgeable()) {
            AgeableComponent ageable = getComponentAgeable();

            // 1) Entity filters gate
            EntityFilter filter = ageable.interactFilters();
            if (filter != null) {
                EntityFilter.Context ctx = EntityFilter.Context.of(player, item).withOther(player);
                if (!filter.test(this, ctx)) return false;
            }

            if (isPauseGrowthItem(item, ageable)) {
                setGrowthPaused(true);
                return true;
            } else if (isFeedableGrowthItem(item, ageable)) {
                this.babyFeedGrowBoost(item);
                return true;
            }
        }
        return false;
    }

    public final boolean isPauseGrowthItem(Item item, AgeableComponent ageable) {
        if (item.isNull()) return false;
        if (ageable == null || ageable.isEmpty()) return false;
        String id = item.getId();
        return ageable.resolvedPauseGrowthItems().contains(id);
    }

    public final boolean isFeedableGrowthItem(Item item, AgeableComponent ageable) {
        if (item.isNull()) return false;
        if (ageable == null || ageable.isEmpty()) return false;
        String id = item.getId();
        return ageable.resolvedFeedableItems().contains(id);
    }


    // Try heal entity by using item / food
    protected boolean tryHeal(Player player, Item item) {
        HealableComponent healable = getComponentHealable();
        if (healable == null || healable.isEmpty()) return false;

        String itemId = item.getId();
        HealableComponent.Item entry = null;
        for (HealableComponent.Item it : healable.resolvedItems()) {
            if (it == null) continue;
            String id = it.item();
            if (id != null && id.equals(itemId)) {
                entry = it;
                break;
            }
        }
        if (entry == null) return false;

        // 1) Entity filters gate
        EntityFilter filter = healable.filters();
        if (filter != null) {
            EntityFilter.Context ctx = EntityFilter.Context.of(player, item).withOther(player);
            if (!filter.test(this, ctx)) return false;
        }

        // 2) If full health and not force_use deny
        if (!healable.resolvedForceUse() && this.getHealthCurrent() >= this.getHealthMax()) return false;

        // 3) Apply heal
        int healAmount = entry.resolvedHealAmount();
        if (healAmount > 0) {
            float newHp = Math.min(this.getHealthMax(), this.getHealthCurrent() + healAmount);
            this.setHealthCurrent(newHp);
        }

        // 4) Apply effects
        for (HealableComponent.Effect ef : entry.resolvedEffects()) {
            if (ef == null || ef.isEmpty()) continue;

            String name = ef.name();
            if (name == null || name.isEmpty()) continue;

            float chance = (ef.chance() != null) ? ef.chance() : 1.0f;
            if (chance <= 0.0f) continue;
            if (chance < 1.0f && Math.random() > chance) continue;

            int duration = (ef.duration() != null) ? Math.max(0, ef.duration().intValue()) : 0;
            int amplifier = (ef.amplifier() != null) ? Math.max(0, ef.amplifier().intValue()) : 0;

            Effect type = Effect.get(name);
            if (type == null) continue;

            this.addEffect(type.setAmplifier(amplifier).setDuration(duration).setVisible(true));
        }

        return true;
    }

    protected boolean isHealableItem(Item item, HealableComponent healable) {
        if (item.isNull()) return false;

        String key = item.getId();
        if (key == null || key.isEmpty()) return false;

        return healable.resolvedHealableItems().contains(key.trim().toLowerCase(Locale.ROOT));
    }


    // Finish food interaction, play effects, set memory
    protected void finishFoodPipeline(Player player, Item item, int currentTick) {
        this.setPersistent(true);
        this.getLevel().addSound(this, Sound.RANDOM_EAT);
        this.getLevel().addParticle(new ItemBreakParticle(this.add(0, getHeight() * 0.75F, 0), Item.get(item.getId(), 0, 1)));

        if (!(this instanceof EntityIntelligent ei)) return;

        ei.getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
        ei.getMemoryStorage().put(CoreMemoryTypes.LAST_BE_FEED_TIME, currentTick);
    }


    // Try set name, gating by nameable component
    protected boolean trySetNameTag(Player player, Item item) {
        NameableComponent nameable = getComponentNameable();
        if (nameable == null || nameable.isEmpty()) return false;

        if (!item.hasCustomName()) return false;
        if (!nameable.resolvedAllowNameTagRenaming()) return false;
        if (requiresSneakToNameTag(player) && !player.isSneaking()) return false;

        this.setNameTag(item.getCustomName());
        this.setNameTagVisible(nameable.resolvedAlwaysShow());
        this.setPersistent(true);

        return true;
    }

    protected boolean requiresSneakToNameTag(@NotNull Player player) {
        if (this instanceof EntityVillager || this instanceof EntityWanderingTrader) return true;
        if (this.isRideable()) {
            if (!this.requireSaddleToMount()) return true;
            if (this.isSaddled()) return true;
        }
        if (this.canSit()) return this.isTamed();

        return false;
    }

    /**
     * Entity Components Interactions End
     */

}