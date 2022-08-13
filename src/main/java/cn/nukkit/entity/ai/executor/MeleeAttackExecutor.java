package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.EntityMemory;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.EntityEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class MeleeAttackExecutor implements IBehaviorExecutor{

    protected Class<? extends EntityMemory<?>> memoryClazz;
    protected float speed;
    protected int maxSenseRangeSquared;
    protected boolean clearDataWhenLose;
    protected int coolDown;

    protected int attackTick;

    protected Vector3 oldTarget;
    public MeleeAttackExecutor(Class<? extends EntityMemory<?>> memoryClazz, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
        this.memoryClazz = memoryClazz;
        this.speed = speed;
        this.maxSenseRangeSquared = maxSenseRange * maxSenseRange;
        this.clearDataWhenLose = clearDataWhenLose;
        this.coolDown = coolDown;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        attackTick++;
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);

        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memoryClazz)) return false;

        //获取目标位置（这个clone很重要）
        Entity target = entity.getBehaviorGroup().getMemoryStorage().get(memoryClazz).getData();

        if (target instanceof Player player && !player.isSurvival()) return false;

        //检查距离
        if (entity.distanceSquared(target) > maxSenseRangeSquared) return false;

        if (entity.getMovementSpeed() != speed)
            entity.setMovementSpeed(speed);
        Vector3 clonedTarget = target.clone();
        //更新寻路target
        setRouteTarget(entity, clonedTarget);
        //更新视线target
        setLookTarget(entity, clonedTarget);

        var floor = clonedTarget.floor();

        if (oldTarget == null || oldTarget.equals(floor))
            entity.getBehaviorGroup().setForceUpdateRoute(true);

        oldTarget = floor;

        if (entity.distanceSquared(target) <= 4 && attackTick > coolDown) {
            Item item = entity instanceof EntityInventoryHolder holder ? holder.getItemInHand() : Item.fromString(MinecraftItemID.AIR.getNamespacedId());
            float itemDamage = item.getAttackDamage();
            Enchantment[] enchantments = item.getEnchantments();
            if (item.applyEnchantments()) {
                for (Enchantment enchantment : enchantments) {
                    itemDamage += enchantment.getDamageBonus(target);
                }
            }

            Map<EntityDamageEvent.DamageModifier, Float> damage = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
            damage.put(EntityDamageEvent.DamageModifier.BASE, itemDamage);

            float knockBack = 0.3f;
            if (item.applyEnchantments()) {
                Enchantment knockBackEnchantment = item.getEnchantment(Enchantment.ID_KNOCKBACK);
                if (knockBackEnchantment != null) {
                    knockBack += knockBackEnchantment.getLevel() * 0.1f;
                }
            }

            EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(entity, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, knockBack, item.applyEnchantments() ? enchantments : null);

            target.attack(ev);
            playAttackAnimation(entity);
            attackTick = 0;
            if (target.getHealth() == 0)
                return false;
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(0.1f);
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memoryClazz);
        }
        entity.setEnablePitch(false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(0.1f);
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memoryClazz);
        }
        entity.setEnablePitch(false);
    }

    protected void playAttackAnimation(EntityIntelligent entity) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.ARM_SWING;
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }

    protected void setRouteTarget(@NotNull EntityIntelligent entity, Vector3 vector3) {
        entity.setMoveTarget(vector3);
    }

    protected void setLookTarget(@NotNull EntityIntelligent entity, Vector3 vector3) {
        entity.setLookTarget(vector3);
    }

    protected void removeRouteTarget(@NotNull EntityIntelligent entity) {
        entity.setMoveTarget(null);
    }

    protected void removeLookTarget(@NotNull EntityIntelligent entity) {
        entity.setLookTarget(null);
    }

}
