package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class SpaceRandomRoamExecutor extends FlatRandomRoamExecutor {
    public SpaceRandomRoamExecutor(float speed, int maxRoamRange, int frequency) {
        this(speed, maxRoamRange, frequency, false, 100);
    }

    public SpaceRandomRoamExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime) {
        this(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, 10);
    }

    public SpaceRandomRoamExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, int maxRetryTime) {
        super(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, false, maxRetryTime);
    }

    @Override
    protected Vector3 next(EntityIntelligent entity) {
        var random = ThreadLocalRandom.current();
        int x = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorX();
        int z = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorZ();
        int y = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorY();
        return new Vector3(x, y, z);
    }
}
