package cn.nukkit.entity.ai.executor.villager;

import cn.nukkit.block.BlockBed;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.EntityControl;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.level.Location;
import cn.nukkit.math.BVector3;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;


public class SleepExecutor implements EntityControl, IBehaviorExecutor {


    public SleepExecutor() {}
    @Override
    public boolean execute(EntityIntelligent entity) {
        return true;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        if(entity.getMemoryStorage().notEmpty(CoreMemoryTypes.OCCUPIED_BED)) {
            if(entity.getMemoryStorage().get(CoreMemoryTypes.OCCUPIED_BED) instanceof BlockBed bed) {

                BlockBed head = bed.getHeadPart();
                BlockBed foot = bed.getFootPart();

                Location sleepingLocation = foot.getLocation().add(switch (head.getBlockFace()) {
                    case NORTH -> new Vector3(0.5f, 0.5625f, 0);
                    case SOUTH -> new Vector3(0.5f, 0.5625f, 1);
                    case WEST -> new Vector3(0, 0.5625f, 0.5);
                    case EAST -> new Vector3(1, 0.5625f, 0.5);
                    default -> Vector3.ZERO;
                });
                sleepingLocation.setYaw(BVector3.getYawFromVector(head.getBlockFace().getOpposite().getUnitVector()));
                sleepingLocation.setHeadYaw(sleepingLocation.getYaw());
                entity.teleport(sleepingLocation);
                entity.respawnToAll();
                entity.setDataFlag(ActorFlags.SLEEPING);
                entity.setDataProperty(ActorDataTypes.BED_POSITION, head.asBlockVector3());
                entity.setDataFlag(ActorFlags.BODY_ROTATION_BLOCKED);
            }
        }
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setDataFlag(ActorFlags.SLEEPING, false);
        entity.setDataFlag(ActorFlags.BODY_ROTATION_BLOCKED, false);
        entity.setDataProperty(ActorDataTypes.BED_POSITION, new BlockVector3(0, 0 ,0));
        if(!entity.getLevel().isNight()) {
            if(entity instanceof EntityVillagerV2 villager) {
                villager.heal(villager.getHealthMax());
            }
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }
}
