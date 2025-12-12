package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCanAttack;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.EnumMap;
import java.util.Map;


/** Universal melee attack actuator. */
public class MeleeAttackExecutor implements EntityControl, IBehaviorExecutor {

    protected MemoryType<? extends Entity> memory;
    protected float speed;
    protected int maxSenseRangeSquared;
    protected boolean clearDataWhenLose;
    protected int coolDown;
    protected float attackRange;

    protected int attackTick;

    protected Vector3 oldTarget;

    /** Used to specify a specific attack target. */
    protected Entity target;

    /** Used to specify a specific look target. */
    protected Vector3 lookTarget;

    /** Give target potion effect */
    protected Effect[] effects;

    public MeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
        this(memory, speed, maxSenseRange, clearDataWhenLose, coolDown,2.5f);
    }

    public MeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown, Effect... effects) {
        this(memory, speed, maxSenseRange, clearDataWhenLose, coolDown, 2.5f, effects);
    }

    /**
     * Melee Attack Executor
     *
     * @param memory            Used to read the memory of the attack target
     * @param speed             The speed of movement towards the attacking target
     * @param maxSenseRange     The maximum range of attack targets
     * @param clearDataWhenLose Clear your memory when you lose your target
     * @param coolDown          Attack cooldown (in tick)
     * @param effects           Give the target potion effect
     */
    public MeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown, float attackRange, Effect... effects) {
        this.memory = memory;
        this.speed = speed;
        this.maxSenseRangeSquared = maxSenseRange * maxSenseRange;
        this.clearDataWhenLose = clearDataWhenLose;
        this.coolDown = coolDown;
        this.attackRange = attackRange;
        this.effects = effects;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        attackTick++;
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memory)) return false;
        Entity newTarget = entity.getBehaviorGroup().getMemoryStorage().get(memory);

        //first is null
        if (this.target == null) {
            this.target = newTarget;
        }
        if (this.lookTarget == null) {
            this.lookTarget = target.getLocation();
        }

        //some check
        if (!target.isAlive()) return false;
        else if (entity.distanceSquared(target) > maxSenseRangeSquared) return false;
        else if (target instanceof Player player) {
            if (player.isCreative() || player.isSpectator() || !player.isOnline() || !entity.level.getName().equals(player.level.getName())) {
                return false;
            }
        }


        //update target and look target
        if (!this.target.getPosition().equals(newTarget.getPosition())) {
            target = newTarget;
        }
        if (!this.lookTarget.equals(newTarget.getLocation())) {
            lookTarget = newTarget.getLocation();
        }

        //set some motion control
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        if (entity.getMovementSpeed() != speed) entity.setMovementSpeed(speed);
        //set target and look target
        setRouteTarget(entity, this.target.getLocation());
        setLookTarget(entity, this.lookTarget);

        var floor = target.floor();
        if (oldTarget == null || !oldTarget.equals(floor)) entity.getBehaviorGroup().setForceUpdateRoute(true);
        oldTarget = floor;

        //attack logic
        if (entity.distanceSquared(target) <= attackRange && attackTick > coolDown) {
            Item item = entity instanceof EntityInventoryHolder holder ? holder.getItemInHand() : Item.AIR;

            float defaultDamage = 0;
            if (entity.isCustomEntity()) {
                defaultDamage = entity.getAttackPower();
            } else if (entity instanceof EntityCanAttack entityCanAttack) {
                defaultDamage = entityCanAttack.getDiffHandDamage(entity.getServer().getDifficulty());
            }
            float itemDamage = item.getAttackDamage(entity) + defaultDamage;

            Enchantment[] enchantments = item.getEnchantments();
            if (item.applyEnchantments()) {
                for (Enchantment enchantment : enchantments) {
                    itemDamage += enchantment.getDamageBonus(target, entity);
                }
            }

            Map<EntityDamageEvent.DamageModifier, Float> damage = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
            damage.put(EntityDamageEvent.DamageModifier.BASE, itemDamage);

            float knockBack = 0.5f;
            if (item.applyEnchantments()) {
                Enchantment knockBackEnchantment = item.getEnchantment(Enchantment.ID_KNOCKBACK);
                if (knockBackEnchantment != null) {
                    knockBack += knockBackEnchantment.getLevel() * 0.1f;
                }
            }

            EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(entity, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, knockBack, item.applyEnchantments() ? enchantments : null);

            ev.setBreakShield(item.canBreakShield());

            target.attack(ev);
            if (!ev.isCancelled()) {
                for (var e : effects) {
                    target.addEffect(e);
                }

                playAttackAnimation(entity);
                entity.getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_TIME, entity.getLevel().getTick());
                entity.getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_ENTITY, target);
                attackTick = 0;
            }

            return target.getHealth() != 0;
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        // Reset Speed
        entity.setMovementSpeed(entity.getDefaultSpeed());
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        this.target = null;
        this.lookTarget = null;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        // Reset Speed
        entity.setMovementSpeed(entity.getDefaultSpeed());
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        this.target = null;
        this.lookTarget = null;
    }

    protected void playAttackAnimation(EntityIntelligent entity) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.ARM_SWING;
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}
