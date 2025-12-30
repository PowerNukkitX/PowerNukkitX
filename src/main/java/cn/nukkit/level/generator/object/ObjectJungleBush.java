package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockJungleLeaves;
import cn.nukkit.block.BlockJungleLog;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

public class ObjectJungleBush extends TreeGenerator {

    private final BlockState metaWood = BlockJungleLog.PROPERTIES.getBlockState(
            CommonBlockProperties.PILLAR_AXIS.createValue(BlockFace.Axis.Y));

    private final BlockState metaLeaves = BlockJungleLeaves.PROPERTIES.getDefaultState();

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 vectorPosition) {
        BlockVector3 pos = new BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ());

        level.setBlockStateAt(pos, metaWood);

        for (int y = -2; y <= 1; y++) {
            int radius = 2 - y;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radius * radius) {
                        BlockVector3 leafPos = pos.add(x, y, z);
                        Block existing = level.getBlockIfCachedOrLoaded(leafPos.x, leafPos.y, leafPos.z);
                        if (existing.isAir() || existing.canBeReplaced() || existing instanceof BlockLeaves) {
                            level.setBlockStateAt(leafPos, metaLeaves);
                        }
                    }
                }
            }
        }

        return true;
    }
}