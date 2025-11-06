package cn.nukkit.level.generator.object;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockEndGateway;
import cn.nukkit.block.BlockState;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.Arrays;

public class ObjectEndGateway extends ObjectGenerator {

    protected static final BlockState BEDROCK = BlockBedrock.PROPERTIES.getDefaultState();
    protected static final BlockState END_GATEWAY = BlockEndGateway.PROPERTIES.getDefaultState();

    @Override
    public boolean generate(BlockManager level, RandomSourceProvider rand, Vector3 pos) {
        Arrays.stream(BlockFace.values()).forEach(face -> level.setBlockStateAt(pos.up().getSide(face), BEDROCK));
        Arrays.stream(BlockFace.values()).forEach(face -> level.setBlockStateAt(pos.down().getSide(face), BEDROCK));
        level.setBlockStateAt(pos, END_GATEWAY);
        return true;
    }
}
