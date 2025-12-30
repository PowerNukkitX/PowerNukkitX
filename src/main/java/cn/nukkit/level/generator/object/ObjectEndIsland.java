package cn.nukkit.level.generator.object;

import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectEndIsland extends ObjectGenerator {


    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 position) {

        float n = (float) (rand.nextInt(2) + 4);
        for (int y = 0; n > 0.5f; y--) {
            for (int x = NukkitMath.floorFloat(-n); x <= NukkitMath.ceilFloat(n); x++) {
                for (int z = NukkitMath.floorFloat(-n); z <= NukkitMath.ceilFloat(n); z++) {
                    if ((float) (x * x + z * z) <= (n + 1f) * (n + 1f)) {
                        level.setBlockStateAt(position.getFloorX() + x, position.getFloorY() + y, position.getFloorZ() + z, END_STONE);
                    }
                }
            }
            n -= (float) ((double) rand.nextInt(1) + 0.5f);
        }
        return true;
    }
}
