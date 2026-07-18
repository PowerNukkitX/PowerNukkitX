package org.powernukkitx.level.generator.object;

import org.powernukkitx.block.BlockBedrock;
import org.powernukkitx.block.BlockEndGateway;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.random.RandomSourceProvider;

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
