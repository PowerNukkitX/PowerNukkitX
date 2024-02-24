package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.bow.EnchantmentBow;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.function.Supplier;

public class ShootExecutor implements EntityControl, IBehaviorExecutor {
    protected MemoryType<? extends Entity> memory;
    protected float speed;
    protected int maxShootDistanceSquared;
    protected boolean clearDataWhenLose;
    protected final int coolDownTick;
    protected final int pullBowTick;
    /**
     * 用来指定特定的攻击目标.
     * <p>
     * Used to specify a specific attack target.
     **/
    protected Entity target;
    /**
     * 用来射击的物品
     */
    protected Supplier<Item> item;
    private int tick1;//control the coolDownTick
    private int tick2;//control the pullBowTick

    /**
     * 射击执行器
     *
     * @param item              the item
     * @param memory            用于读取攻击目标的记忆<br>Used to read the memory of the attack target
     * @param speed             移动向攻击目标的速度<br>The speed of movement towards the attacking target
     * @param maxShootDistance  允许射击的最大距离，只有在这个距离内才能射击<br>The maximum distance at which it is permissible to shoot, and only at this distance can be fired
     * @param clearDataWhenLose 失去目标时清空记忆<br>Clear your memory when you lose your target
     * @param coolDownTick      攻击冷却时间(单位tick)<br>Attack cooldown (tick)
     * @param pullBowTick       每次攻击动画用时(单位tick)<br>Attack Animation time(tick)
     */
    public ShootExecutor(Supplier<Item> item, MemoryType<? extends Entity> memory, float speed, int maxShootDistance, boolean clearDataWhenLose, int coolDownTick, int pullBowTick) {
        this.item = item;
        this.memory = memory;
        this.speed = speed;
        this.maxShootDistanceSquared = maxShootDistance * maxShootDistance;
        this.clearDataWhenLose = clearDataWhenLose;
        this.coolDownTick = coolDownTick;
        this.pullBowTick = pullBowTick;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (tick2 == 0) {
            tick1++;
        }
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memory)) return false;
        Entity newTarget = entity.getBehaviorGroup().getMemoryStorage().get(memory);
        if (this.target == null) target = newTarget;
        //some check
        if (!target.isAlive()) return false;
        else if (target instanceof Player player) {
            if (player.isCreative() || player.isSpectator() || !player.isOnline() || !entity.level.getName().equals(player.level.getName())) {
                return false;
            }
        }

        if (!this.target.getPosition().equals(newTarget.getPosition())) {
            //更新目标
            target = newTarget;
        }

        if (entity.getMovementSpeed() != speed) entity.setMovementSpeed(speed);
        Location clone = this.target.clone();

        if (entity.distanceSquared(target) > maxShootDistanceSquared) {
            //更新寻路target
            setRouteTarget(entity, clone);
        } else {
            setRouteTarget(entity, null);
        }
        //更新视线target
        setLookTarget(entity, clone);

        if (tick2 == 0 && tick1 > coolDownTick) {
            if (entity.distanceSquared(target) <= maxShootDistanceSquared) {
                this.tick1 = 0;
                this.tick2++;
                playBowAnimation(entity);
            }
        } else if (tick2 != 0) {
            tick2++;
            if (tick2 > pullBowTick) {
                Item tool = item.get();
                if (tool instanceof ItemBow bow) {
                    bowShoot(bow, entity);
                    stopBowAnimation(entity);
                    tick2 = 0;
                    return target.getHealth() != 0;
                }
            }
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        stopBowAnimation(entity);
        this.target = null;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        //重置速度
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        stopBowAnimation(entity);
        this.target = null;
    }

    protected void bowShoot(ItemBow bow, EntityLiving entity) {
        playBowAnimation(entity);
        double damage = 2;
        Enchantment bowDamage = bow.getEnchantment(Enchantment.ID_BOW_POWER);
        if (bowDamage != null && bowDamage.getLevel() > 0) {
            damage += (double) bowDamage.getLevel() * 0.5 + 0.5;
        }
        Enchantment flameEnchant = bow.getEnchantment(Enchantment.ID_BOW_FLAME);
        boolean flame = flameEnchant != null && flameEnchant.getLevel() > 0;

        CompoundTag nbt = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(entity.x))
                        .add(new DoubleTag(entity.y + entity.getCurrentHeight() / 2 + 0.2f))
                        .add(new DoubleTag(entity.z)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(-Math.sin(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI)))
                        .add(new DoubleTag(-Math.sin(entity.pitch / 180 * Math.PI)))
                        .add(new DoubleTag(Math.cos(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI))))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag((entity.headYaw > 180 ? 360 : 0) - (float) entity.headYaw))
                        .add(new FloatTag((float) -entity.pitch)))
                .putShort("Fire", flame ? 45 * 60 : 0)
                .putDouble("damage", damage);

        double p = (double) pullBowTick / 20;
        double f = Math.min((p * p + p * 2) / 3, 1) * 3;

        EntityArrow arrow = (EntityArrow) Entity.createEntity(Entity.ARROW, entity.chunk, nbt, entity, f == 2);

        if (arrow == null) {
            return;
        }

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(entity, bow, arrow, f);
        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().kill();
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
            Enchantment infinityEnchant = bow.getEnchantment(Enchantment.ID_BOW_INFINITY);
            boolean infinity = infinityEnchant != null && infinityEnchant.getLevel() > 0;
            EntityProjectile projectile;
            if (infinity && (projectile = entityShootBowEvent.getProjectile()) instanceof EntityArrow) {
                ((EntityArrow) projectile).setPickupMode(EntityProjectile.PICKUP_CREATIVE);
            }

            for (var enc : bow.getEnchantments()) {
                if (enc instanceof EnchantmentBow enchantmentBow) {
                    enchantmentBow.onBowShoot(entity, arrow, bow);
                }
            }

            if (entityShootBowEvent.getProjectile() != null) {
                ProjectileLaunchEvent projectev = new ProjectileLaunchEvent(entityShootBowEvent.getProjectile(), entity);
                Server.getInstance().getPluginManager().callEvent(projectev);
                if (projectev.isCancelled()) {
                    entityShootBowEvent.getProjectile().kill();
                } else {
                    entityShootBowEvent.getProjectile().spawnToAll();
                    entity.getLevel().addSound(entity, Sound.RANDOM_BOW);
                }
            }
        }
    }

    private void playBowAnimation(Entity entity) {
        entity.setDataProperty(EntityDataTypes.TARGET_EID, this.target.getId());
        entity.setDataFlag(EntityFlag.FACING_TARGET_TO_RANGE_ATTACK);
    }

    private void stopBowAnimation(Entity entity) {
        entity.setDataProperty(EntityDataTypes.TARGET_EID, 0L);
        entity.setDataFlag(EntityFlag.FACING_TARGET_TO_RANGE_ATTACK, false);
    }
}
