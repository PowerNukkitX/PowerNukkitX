package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.Utils;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * 代表玩家驯服马时，马的行为
 * <p>
 * Represents the behavior of a horse when the player tames it
 */


public class TameHorseExecutor extends FlatRandomRoamExecutor {
    protected final int tameProbability;
    private int tick1;//control the stopTameFailAnimation

    public TameHorseExecutor(float speed, int maxRoamRange, int frequency) {
        this(speed, maxRoamRange, frequency, false, 100);
    }

    public TameHorseExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime) {
        this(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, false, 10, 35);
    }

    /**
     * Instantiates a new Flat random roam executor.
     *
     * @param speed                    移动速度<br>Movement speed
     * @param maxRoamRange             随机行走目标点的范围<br>The range of the target point that is randomly walked
     * @param frequency                更新目标点的频率<br>How often the target point is updated
     * @param calNextTargetImmediately 是否立即选择下一个目标点,不管执行频率<br>Whether to select the next target point immediately, regardless of the frequency of execution
     * @param runningTime              马儿随机跑动的时间，跑动结束后会判断是否驯服成功<br>The time when the horse runs randomly, after the run, will judge whether the taming is successful
     * @param avoidWater               是否避开水行走<br>Whether to walk away from water
     * @param maxRetryTime             选取目标点的最大尝试次数<br>Pick the maximum number of attempts at the target point
     * @param tameProbability          马被驯服的概率(取值范围1-100)<br>Probability of a horse being tamed (value range 1-100)
     */
    public TameHorseExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, boolean avoidWater, int maxRetryTime, int tameProbability) {
        super(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, avoidWater, maxRetryTime);
        Preconditions.checkArgument(tameProbability > 0 && tameProbability <= 100);
        this.tameProbability = tameProbability;
        this.tick1 = 0;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        //Fail Animation
        if (tick1 != 0) {
            if (tick1 > 13) {
                var horse = (EntityHorse) entity;
                horse.stopTameFailAnimation();
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
            //更新寻路target
            setRouteTarget(entity, target);
            //更新视线target
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
                var horse = (EntityHorse) entity;
                horse.setOwnerName(horse.getMemoryStorage().get(CoreMemoryTypes.RIDER_NAME));
                EntityEventPacket packet = new EntityEventPacket();
                packet.eid = horse.getId();
                packet.event = EntityEventPacket.TAME_SUCCESS;
                Player player = (Player) horse.getRider();
                if (player == null) {
                    return false;
                }
                player.dataPacket(packet);
            } else {
                var horse = (EntityHorse) entity;
                EntityEventPacket packet = new EntityEventPacket();
                packet.eid = horse.getId();
                packet.event = EntityEventPacket.TAME_FAIL;
                Player player = (Player) horse.getRider();
                if (player == null) {
                    return false;
                }
                player.dataPacket(packet);
                horse.playTameFailAnimation();
                horse.dismountEntity(horse.getRider());
                horse.setPersistent(true);
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
