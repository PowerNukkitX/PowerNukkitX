package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.mob.EntityZombiePigman;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.level.Sound;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

public class PiglinTransformExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;

    public PiglinTransformExecutor() {}

    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        if(tick >= 300) {
            transform(entity);
            return false;
        }
        return true;
    }


    @Override
    public void onStart(EntityIntelligent entity) {
        tick = -1;
        entity.setDataFlag(ActorFlags.SHAKING);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setDataFlag(ActorFlags.SHAKING, false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }

    private void transform(EntityIntelligent entity) {
        entity.saveNBT();
        entity.close();
        EntityZombiePigman entityZombiePigman = new EntityZombiePigman(entity.getChunk(), entity.namedTag);
        entityZombiePigman.setPosition(entity);
        entityZombiePigman.setRotation(entity.yaw, entity.pitch);
        entityZombiePigman.spawnToAll();
        entityZombiePigman.level.addSound(entityZombiePigman, Sound.MOB_PIGLIN_CONVERTED_TO_ZOMBIFIED);
        Inventory inventory = entityZombiePigman.getEquipmentInventory();
        for(int i = 2; i < inventory.getSize(); i++) {
            entityZombiePigman.level.dropItem(entityZombiePigman, inventory.getItem(i));
            inventory.clear(i);
        }
        entityZombiePigman.addEffect(Effect.get(EffectType.NAUSEA).setDuration(15));
    }

}


