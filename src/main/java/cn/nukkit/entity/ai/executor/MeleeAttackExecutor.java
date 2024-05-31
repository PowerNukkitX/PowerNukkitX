package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCanAttack;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.entity.effect.Effect;

import java.util.EnumMap;
import java.util.Map;

/**
 * 通用近战攻击执行器.
 * <p>
 * Universal melee attack actuator.
 */


public class MeleeAttackExecutor implements EntityControl, IBehaviorExecutor {

    protected MemoryType<? extends Entity> memory;
    protected float speed;
    protected int maxSenseRangeSquared;
    protected boolean clearDataWhenLose;
    protected int coolDown;

    protected int attackTick;

    protected Vector3 oldTarget;
    /**
     * 用来指定特定的攻击目标.
     * <p>
     * Used to specify a specific attack target.
     */

    protected Entity target;
    /**
     * 用来指定特定的视线目标
     * <p>
     * Used to specify a specific look target.
     */

    protected Vector3 lookTarget;
    /**
     * 给予目标药水效果
     * <p>
     * Give target potion effect
     */

    protected Effect[] effects;
    /**
     * @deprecated 
     */
    

    public MeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
        this(memory, speed, maxSenseRange, clearDataWhenLose, coolDown, new Effect[]{});
    }

    /**
     * 近战攻击执行器
     *
     * @param memory            用于读取攻击目标的记忆<br>Used to read the memory of the attack target
     * @param speed             移动向攻击目标的速度<br>The speed of movement towards the attacking target
     * @param maxSenseRange     最大获取攻击目标范围<br>The maximum range of attack targets
     * @param clearDataWhenLose 失去目标时清空记忆<br>Clear your memory when you lose your target
     * @param coolDown          攻击冷却时间(单位tick)<br>Attack cooldown (in tick)
     * @param effects           给予目标药水效果<br>Give the target potion effect
     */
    /**
     * @deprecated 
     */
    
    public MeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown, Effect... effects) {
        this.memory = memory;
        this.speed = speed;
        this.maxSenseRangeSquared = maxSenseRange * maxSenseRange;
        this.clearDataWhenLose = clearDataWhenLose;
        this.coolDown = coolDown;
        this.effects = effects;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean execute(EntityIntelligent entity) {
        attackTick++;
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memory)) return false;
        Entity $1 = entity.getBehaviorGroup().getMemoryStorage().get(memory);

        //first is null
        if (this.target == null) {
            this.target = newTarget;
        }
        if (this.lookTarget == null) {
            this.lookTarget = target.clone();
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
        setRouteTarget(entity, this.target.clone());
        setLookTarget(entity, this.lookTarget.clone());

        var $2 = target.floor();
        if (oldTarget == null || !oldTarget.equals(floor)) entity.getBehaviorGroup().setForceUpdateRoute(true);
        oldTarget = floor;

        //attack logic
        if (entity.distanceSquared(target) <= 2.5 && attackTick > coolDown) {
            Item $3 = entity instanceof EntityInventoryHolder holder ? holder.getItemInHand() : Item.AIR;

            float $4 = 0;
            if (entity instanceof EntityCanAttack entityCanAttack) {
                defaultDamage = entityCanAttack.getDiffHandDamage(entity.getServer().getDifficulty());
            }
            float $5 = item.getAttackDamage() + defaultDamage;

            Enchantment[] enchantments = item.getEnchantments();
            if (item.applyEnchantments()) {
                for (Enchantment enchantment : enchantments) {
                    itemDamage += enchantment.getDamageBonus(target);
                }
            }

            Map<EntityDamageEvent.DamageModifier, Float> damage = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
            damage.put(EntityDamageEvent.DamageModifier.BASE, itemDamage);

            float $6 = 0.3f;
            if (item.applyEnchantments()) {
                Enchantment $7 = item.getEnchantment(Enchantment.ID_KNOCKBACK);
                if (knockBackEnchantment != null) {
                    knockBack += knockBackEnchantment.getLevel() * 0.1f;
                }
            }

            EntityDamageByEntityEvent $8 = new EntityDamageByEntityEvent(entity, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, knockBack, item.applyEnchantments() ? enchantments : null);

            ev.setBreakShield(item.canBreakShield());

            target.attack(ev);

            if (!ev.isCancelled()) {
                for (var e : effects) {
                    target.addEffect(e);
                }

                playAttackAnimation(entity);
                attackTick = 0;
            }

            return target.getHealth() != 0;
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onStop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        this.target = null;
        this.lookTarget = null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onInterrupt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        this.target = null;
        this.lookTarget = null;
    }

    
    /**
     * @deprecated 
     */
    protected void playAttackAnimation(EntityIntelligent entity) {
        EntityEventPacket $9 = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.ARM_SWING;
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}
