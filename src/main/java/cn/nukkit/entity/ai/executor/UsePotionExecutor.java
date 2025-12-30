package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.BlockMagma;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.plugin.InternalPlugin;

import java.util.Arrays;

public class UsePotionExecutor implements EntityControl, IBehaviorExecutor {

    protected float speed;
    protected final int coolDownTick;
    protected final int useDelay;

    private int tick1;//control the coolDownTick
    private int tick2;//control the pullBowTick

    /**
     *
     * @param speed             <br>The speed of movement towards the attacking target
     * @param coolDownTick      <br>Attack cooldown (tick)
     * @param useDelay          <br>Attack Animation time(tick)
     */
    public UsePotionExecutor(float speed, int coolDownTick, int useDelay) {
        this.speed = speed;
        this.coolDownTick = coolDownTick;
        this.useDelay = useDelay;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (tick2 == 0) {
            tick1++;
        }
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);

        if (entity.getMovementSpeed() != speed) entity.setMovementSpeed(speed);

        setRouteTarget(entity, null);

        if (tick2 == 0 && tick1 > coolDownTick) {
            this.tick1 = 0;
            this.tick2++;
            startShootSequence(entity);
        } else if (tick2 != 0) {
            tick2++;
            if (tick2 > useDelay) {
                entity.getLevel().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> endShootSequence(entity), 20);
                tick2 = 0;
                return true;
            }
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setMovementSpeed(entity.getDefaultSpeed());
        entity.setEnablePitch(false);
        endShootSequence(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setMovementSpeed(entity.getDefaultSpeed());
        entity.setEnablePitch(false);
        endShootSequence(entity);
    }


    private void startShootSequence(Entity entity) {
        if(entity instanceof EntityMob mob) {
            mob.setItemInHand(getPotion(entity));
        }
    }

    private void endShootSequence(Entity entity) {
        if(entity instanceof EntityMob mob) {
            Item item = mob.getItemInHand();
            if(item instanceof ItemPotion potion) {
                PotionType.get(potion.getDamage()).getEffects(false).forEach(entity::addEffect);
            }
            mob.setItemInHand(Item.AIR);
        }
    }

    public Item getPotion(Entity entity) {
        if(entity.isInsideOfWater() && !entity.hasEffect(EffectType.WATER_BREATHING)) {
            return ItemPotion.fromPotion(PotionType.WATER_BREATHING);
        } else if(!entity.hasEffect(EffectType.FIRE_RESISTANCE) && (entity.isOnFire() || Arrays.stream(entity.level.getCollisionBlocks(entity.getBoundingBox().getOffsetBoundingBox(0, -1, 0))).anyMatch(block -> block instanceof BlockMagma))) {
            return ItemPotion.fromPotion(PotionType.FIRE_RESISTANCE);
        } else if(entity.getHealth() < entity.getMaxHealth()) {
            return ItemPotion.fromPotion(PotionType.HEALING);
        } else if(entity instanceof EntityIntelligent intelligent) {
            if(intelligent.getMemoryStorage().notEmpty(CoreMemoryTypes.BE_ATTACKED_EVENT)) {
                EntityDamageEvent event = intelligent.getMemoryStorage().get(CoreMemoryTypes.BE_ATTACKED_EVENT);
                if(event instanceof EntityDamageByEntityEvent e) {
                    if(e.getDamager().distance(entity) > 11) {
                        return ItemPotion.fromPotion(PotionType.SWIFTNESS);
                    }
                }
            }
        }
        return Item.AIR;
    }
}
