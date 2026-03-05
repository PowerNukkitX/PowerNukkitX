package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.Utils;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;


/**
 * Handles ride-based taming behavior for rideable animals.
 *
 * While being ridden, the entity performs random roaming for a period
 * and then rolls a probability check to either become tamed (assigning
 * the rider as owner) or fail and dismount the rider, triggering the
 * corresponding tame event feedback.
 * 
 * @author Curse
 */
public class RideableTameExecutor extends FlatRandomRoamExecutor {
    protected final int tameProbability;
    private int tick1;

    public RideableTameExecutor(float speed, int maxRoamRange, int frequency) {
        this(speed, maxRoamRange, frequency, false, 100);
    }

    public RideableTameExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime) {
        this(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, false, 10, 35);
    }

    public RideableTameExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, boolean avoidWater, int maxRetryTime, int tameProbability) {
        super(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, avoidWater, maxRetryTime);
        Preconditions.checkArgument(tameProbability > 0 && tameProbability <= 100);
        this.tameProbability = tameProbability;
        this.tick1 = 0;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (!(entity instanceof EntityAnimal animal)) return false;
        if (tick1 != 0) {
            if (tick1 > 13) {
                if (animal.isEquine()) animal.equineStopTameFailAnimation();
                return false;
            }
            tick1++;
            return true;
        }

        currentTargetCalTick++;
        durationTick++;

        if (entity.isEnablePitch()) entity.setEnablePitch(false);

        if (currentTargetCalTick >= frequency || (calNextTargetImmediately && needUpdateTarget(entity))) {
            Vector3 target = next(entity);

            if (avoidWater) {
                String blockId;
                int time = 0;
                while (time <= maxRetryTime && ((blockId = entity.level.getTickCachedBlock(target.add(0, -1, 0)).getId()) == Block.FLOWING_WATER || blockId == Block.WATER)) {
                    target = next(entity);
                    time++;
                }
            }

            if (entity.getMovementSpeed() != speed)
                entity.setMovementSpeed(speed);

            setRouteTarget(entity, target);
            setLookTarget(entity, target);

            currentTargetCalTick = 0;
            entity.getBehaviorGroup().setForceUpdateRoute(calNextTargetImmediately);
        }

        if (durationTick <= runningTime || runningTime == -1)
            return true;
        else {
            currentTargetCalTick = 0;
            durationTick = 0;

            if (Utils.rand(0, 100) <= tameProbability) {
                EntityEventPacket packet = new EntityEventPacket();

                animal.setOwnerName(animal.getMemoryStorage().get(CoreMemoryTypes.RIDER_NAME));
                animal.setTamed(true);
                animal.setPersistent(true);
                animal.updateInventoryFlags();
                packet.eid = animal.getId();
                packet.event = EntityEventPacket.TAME_SUCCESS;
                Player player = (Player) animal.getRider();
                if (player == null) return false;
                player.dataPacket(packet);

            } else {
                EntityEventPacket packet = new EntityEventPacket();

                packet.eid = animal.getId();
                packet.event = EntityEventPacket.TAME_FAIL;
                Player player = (Player) animal.getRider();
                if (player == null) return false;
                player.dataPacket(packet);
                if (animal.isEquine()) animal.equinePlayTameFailAnimation();
                animal.dismountEntity(animal.getRider());
                animal.setPersistent(true);

                tick1++;
                return true;
            }

            return false;
        }
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        super.onStop(entity);
        tick1 = 0;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        super.onStart(entity);
        tick1 = 0;
    }
}
