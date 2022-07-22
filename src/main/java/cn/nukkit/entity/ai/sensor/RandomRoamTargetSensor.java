package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;
import cn.nukkit.entity.ai.memory.RandomRoamTargetMemory;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class RandomRoamTargetSensor implements ISensor {

    protected Random random = new Random();
    protected int playerSenseRange;
    protected int randomTargetRange;


    public RandomRoamTargetSensor(int playerSenseRange,int randomTargetRange) {
        this.playerSenseRange = playerSenseRange;
        this.randomTargetRange = randomTargetRange;
    }

    @NotNull
    @Override
    public IMemory<?> sense(EntityIntelligent entity) {
        //10% chance to roam to a random location
        if (random.nextInt(100) < 10) {
            Vector3 target = entity.getPosition().add(random.nextInt(randomTargetRange * 2) - randomTargetRange, 0, random.nextInt(randomTargetRange * 2) - randomTargetRange);
            return new RandomRoamTargetMemory(target);
        }
        return new RandomRoamTargetMemory(null);
    }

    protected Vector3 next(EntityIntelligent entity){
        //随机计算下一个落点
        Vector3 next = this.nextXZ(entity.getX(), entity.getY(), randomTargetRange);
        next.y = entity.getLevel().getHighestBlockAt(next.getFloorX(), next.getFloorZ()) + 1;
        return next;
    }

    protected Vector3 nextXZ(double centerX, double centerZ, int maxRange) {
        Vector3 vec3 = new Vector3(centerX, 0, centerZ);
        vec3.x = Math.round(vec3.x) + this.random.nextInt(-maxRange, maxRange) + 0.5;
        vec3.z = Math.round(vec3.z) + this.random.nextInt(-maxRange, maxRange) + 0.5;
        return vec3;
    }
}
