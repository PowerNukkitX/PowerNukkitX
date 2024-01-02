package cn.nukkit.level.generator;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrass;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.format.IChunk;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class Flat extends Generator {
    public Flat(Map<String, Object> options) {
        super(options);
    }

    @Override
    public String getName() {
        return "flat";
    }

    @Override
    public DimensionData getDimensionData() {
        return DimensionEnum.OVERWORLD.getDimensionData();
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        this.generateChunk(level.getChunk(chunkX, chunkZ));
    }

    static final BlockState bedrock = BlockBedrock.PROPERTIES.getDefaultState();
    static final BlockState grass =  BlockGrass.PROPERTIES.getDefaultState();
    static final BlockState dirt =  BlockDirt.PROPERTIES.getDefaultState();

    private void generateChunk(IChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setHeightMap(x, z, 5);
                for (int y = 0; y < 5; y++) {
                    if (y == 0) {
                        chunk.setBlockState(x, y, z, bedrock);
                    } else if (y == 4) chunk.setBlockState(x, y, z, grass);
                    else chunk.setBlockState(x, y, z, dirt);
                }
            }
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
    }
}
