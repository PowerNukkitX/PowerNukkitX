package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;
import cn.nukkit.entity.ai.memory.RandomRoamTargetMemory;
import cn.nukkit.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class RandomRoamTargetSensor implements ISensor {
    protected int playerSenseRange;
    protected int randomTargetRange;

    protected int frequency;
    protected int currentTargetCalTick = 0;

    public RandomRoamTargetSensor(int frequency,int playerSenseRange,int randomTargetRange) {
        this.frequency = frequency;
        this.currentTargetCalTick = this.frequency;
        this.playerSenseRange = playerSenseRange;
        this.randomTargetRange = randomTargetRange;
    }

    @Override
    public IMemory<?> sense(EntityIntelligent entity) {
        currentTargetCalTick++;
        if (currentTargetCalTick >= frequency) {
            //roam to a random location
            var random = ThreadLocalRandom.current();
            Vector3 target = entity.getPosition().add(random.nextInt(randomTargetRange * 2) - randomTargetRange, 0, random.nextInt(randomTargetRange * 2) - randomTargetRange);
            currentTargetCalTick = 0;
            return new RandomRoamTargetMemory(target);
        }
        return null;
    }

    protected Vector3 next(EntityIntelligent entity){
        //随机计算下一个落点
        Vector3 next = this.nextXZ(entity.getX(), entity.getY(), randomTargetRange);
        next.y = entity.getLevel().getHighestBlockAt(next.getFloorX(), next.getFloorZ()) + 1;
        return next;
    }

    protected Vector3 nextXZ(double centerX, double centerZ, int maxRange) {
        Vector3 vec3 = new Vector3(centerX, 0, centerZ);
        var random = ThreadLocalRandom.current();
        vec3.x = Math.round(vec3.x) + random.nextInt(-maxRange, maxRange) + 0.5;
        vec3.z = Math.round(vec3.z) + random.nextInt(-maxRange, maxRange) + 0.5;
        return vec3;
    }
}
