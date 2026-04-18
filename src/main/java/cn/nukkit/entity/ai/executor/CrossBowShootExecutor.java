package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemCrossbow;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

import java.util.Arrays;
import java.util.function.Supplier;

public class CrossBowShootExecutor implements EntityControl, IBehaviorExecutor {
    protected MemoryType<? extends Entity> memory;
    protected float speed;
    protected int maxShootDistanceSquared;
    protected boolean clearDataWhenLose;
    protected final int coolDownTick;
    protected final int pullBowTick;
    /**
     * Used to specify a specific attack target.
     **/
    protected Entity target;
    /**
     * Items used for shooting
     */
    protected Supplier<Item> item;
    private int tick1;//control the coolDownTick
    private int tick2;//control the pullBowTick

    /**
     * Shooting actuator
     *
     * @param item              the item
     * @param memory            用于读取攻击目标的记忆<br>Used to read the memory of the attack target
     * @param speed             移动向攻击目标的速度<br>The speed of movement towards the attacking target
     * @param maxShootDistance  允许射击的最大距离，只有在这个距离内才能射击<br>The maximum distance at which it is permissible to shoot, and only at this distance can be fired
     * @param clearDataWhenLose 失去目标时清空记忆<br>Clear your memory when you lose your target
     * @param coolDownTick      攻击冷却时间(单位tick)<br>Attack cooldown (tick)
     * @param pullBowTick       每次攻击动画用时(单位tick)<br>Attack Animation time(tick)
     */
    public CrossBowShootExecutor(Supplier<Item> item, MemoryType<? extends Entity> memory, float speed, int maxShootDistance, boolean clearDataWhenLose, int coolDownTick, int pullBowTick) {
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

        if (!target.isAlive() || (target instanceof Player player && (player.isIgnoredByEntities() || !entity.level.getName().equals(player.level.getName())))) {
            return false;
        }

        if (!this.target.getPosition().equals(newTarget.getPosition())) {
            target = newTarget;
        }

        if (entity.getMovementSpeed() != speed) entity.setMovementSpeed(speed);
        Location clone = this.target.getLocation();

        if (entity.distanceSquared(target) > maxShootDistanceSquared) {
            setRouteTarget(entity, clone);
        } else {
            setRouteTarget(entity, null);
        }
        setLookTarget(entity, clone);

        if (tick2 == 0 && tick1 > coolDownTick) {
            if (entity.distanceSquared(target) <= maxShootDistanceSquared) {
                this.tick1 = 0;
                this.tick2++;
                playBowAnimation(entity, 0);
            }
        }
        if (tick2 != 0) {
            playBowAnimation(entity, tick2);
            tick2++;
            if (tick2 > pullBowTick) {
                Item tool = item.get();
                if (tool instanceof ItemCrossbow bow) {
                    bowShoot(bow, entity);
                    stopBowAnimation(entity);
                    tick2 = 0;
                    return target.getHealthCurrent() != 0;
                }
            }
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
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
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        if (clearDataWhenLose) {
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        }
        entity.setEnablePitch(false);
        stopBowAnimation(entity);
        this.target = null;
    }

    protected void bowShoot(ItemCrossbow bow, EntityLiving entity) {
        double damage = 2;
        Enchantment bowDamage = bow.getEnchantment(Enchantment.ID_BOW_POWER);
        if (bowDamage != null && bowDamage.getLevel() > 0) {
            damage += (double) bowDamage.getLevel() * 0.5 + 0.5;
        }
        Enchantment flameEnchant = bow.getEnchantment(Enchantment.ID_BOW_FLAME);
        boolean flame = flameEnchant != null && flameEnchant.getLevel() > 0;

        final NbtMap nbt = NbtMap.builder()
                .putList("Pos", NbtType.DOUBLE, Arrays.asList(
                                entity.x,
                                entity.y + entity.getCurrentHeight() / 2 + 0.2f,
                                entity.z
                        )
                ).putList("Motion", NbtType.DOUBLE, Arrays.asList(
                                -Math.sin(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI),
                                -Math.sin(entity.pitch / 180 * Math.PI),
                                Math.cos(entity.headYaw / 180 * Math.PI) * Math.cos(entity.pitch / 180 * Math.PI)
                        )
                ).putList("Rotation", NbtType.FLOAT, Arrays.asList(
                                (entity.headYaw > 180 ? 360 : 0) - (float) entity.headYaw,
                                (float) -entity.pitch
                        )
                )
                .putShort("Fire", (short) (flame ? 45 * 60 : 0))
                .putDouble("damage", damage)
                .build();

        double p = (double) pullBowTick / 20;
        double f = Math.min((p * p + p * 2) / 3, 1) * 3;

        EntityArrow arrow = (EntityArrow) Entity.createEntity(Entity.ARROW, entity.chunk, nbt, entity, f == 2);
        if (arrow == null) {
            return;
        }
        arrow.setPickupMode(EntityProjectile.PICKUP_NONE);

        EntityShootBowEvent entityShootBowEvent = new EntityShootBowEvent(entity, bow, arrow, f);
        Server.getInstance().getPluginManager().callEvent(entityShootBowEvent);
        if (entityShootBowEvent.isCancelled()) {
            entityShootBowEvent.getProjectile().kill();
        } else {
            entityShootBowEvent.getProjectile().setMotion(entityShootBowEvent.getProjectile().getMotion().multiply(entityShootBowEvent.getForce()));
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

    private void playBowAnimation(Entity entity, int chargeAmount) {
        if(chargeAmount == 0) {
            entity.level.addSound(entity, Sound.CROSSBOW_LOADING_START);
            entity.setDataProperty(ActorDataTypes.TARGET, this.target.getId());
            entity.setDataFlag(ActorFlags.USING_ITEM);
        } else entity.setDataProperty(ActorDataTypes.CHARGE_AMOUNT, chargeAmount*2);
        if(chargeAmount == 30) entity.level.addSound(entity, Sound.CROSSBOW_LOADING_MIDDLE);
        if(chargeAmount == 60) {
            entity.setDataFlag(ActorFlags.CHARGED);
            entity.level.addSound(entity, Sound.CROSSBOW_LOADING_END);
        }
    }

    private void stopBowAnimation(Entity entity) {
        entity.setDataProperty(ActorDataTypes.TARGET, 0L);
        entity.setDataProperty(ActorDataTypes.CHARGE_AMOUNT, 0);
        entity.setDataFlag(ActorFlags.USING_ITEM, false);
        entity.setDataFlag(ActorFlags.CHARGED, false);
    }
}
