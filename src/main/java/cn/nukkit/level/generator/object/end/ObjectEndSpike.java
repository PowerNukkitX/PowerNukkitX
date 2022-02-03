package cn.nukkit.level.generator.object.end;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityEndCrystal;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import static cn.nukkit.block.BlockID.BEDROCK;
import static cn.nukkit.block.BlockID.OBSIDIAN;
import static cn.nukkit.block.BlockID.IRON_BARS;
import static cn.nukkit.block.BlockID.STONE;

/**
 * @author GoodLucky777
 */
public class ObjectEndSpike extends BasicGenerator {

    private static final BlockState STATE_BEDROCK_INFINIBURN = BlockState.of(BEDROCK, 1);
    private static final BlockState STATE_OBSIDIAN = BlockState.of(STONE); // For debug
    private static final BlockState STATE_IRON_BARS = BlockState.of(IRON_BARS);

    private Vector3 position;
    private int radius;
    private int height;
    private boolean hasIronBars;

    private Vector3 tempPosition = new Vector3();

    public ObjectEndSpike(Vector3 position, int radius, int height, boolean hasIronBars) {
        this.position = position;
        this.radius = radius;
        this.height = height;
        this.hasIronBars = hasIronBars;
    }

    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        this.position = position;
        return this.generate(level, rand);
    }

    public boolean generate(ChunkManager level, NukkitRandom rand) {
        // Generate an End Spike
        for (int y = 0; y <= height + 10; y++) {
            for (int tx = -radius; tx <= radius; ++tx) {
                for (int tz = -radius; tz <= radius; ++tz) {
                    int x = position.getFloorX() + tx, z = position.getFloorZ() + tz;
                    if (tx * tx + tz * tz <= radius * radius)
                        level.setBlockStateAt(x, y, z, STATE_OBSIDIAN);
                }
            }
        }

        // Generate an iron bars if hasIronBars is true
        if (hasIronBars) {
            for (int y = height; y <= height + 10; y++) {
                for (int tx = -radius; tx <= radius; ++tx) {
                    for (int tz = -radius; tz <= radius; ++tz) {
                        int x = position.getFloorX() + tx, z = position.getFloorZ() + tz;
                        if (tx * tx + tz * tz <= radius * radius)
                            level.setBlockStateAt(x, y, z, STATE_IRON_BARS);
                    }
                }
            }
        }

        // Generate a bedrock and an end crystal
        level.setBlockStateAt(position.getFloorX(), height, position.getFloorZ(), STATE_BEDROCK_INFINIBURN);

        Entity endCrystal = Entity.createEntity(EntityEndCrystal.NETWORK_ID,
                new Position(position.getFloorX() + 0.5, height + 1, position.getFloorZ() + 0.5));

        assert level != null && position != null : "Could not got level or position";

        // level.getChunk(position.getFloorX() >> 4, position.getFloorZ() >>
        // 4).addEntity(endCrystal);
        return true;
    }
}
