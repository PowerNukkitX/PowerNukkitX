package cn.nukkit.level.generator.object;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockState;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectExitPortal extends ObjectGenerator {

    protected static final BlockState BEDROCK = BlockBedrock.PROPERTIES.getDefaultState();

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 pos) {

        for(int x = -2; x <= 2; x++){
            for(int z = -2; z <= 2; z++){
                if(!(Math.abs(x) == 2 && Math.abs(z) == 2)) {
                    level.setBlockStateAt(pos.getFloorX() + x, pos.getFloorY() - 1, pos.getFloorZ() + z, BEDROCK);
                }
            }
        }

        return true;
    }
}
