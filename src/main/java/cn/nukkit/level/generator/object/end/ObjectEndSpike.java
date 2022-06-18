package cn.nukkit.level.generator.object.end;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import static cn.nukkit.block.BlockID.*;
import static java.lang.Math.abs;

/**
 * @author Mojang AB
 *         -- Ported from Minecraft JE
 *         -- by AsgoreDream(HelloworldSB)
 */
public class ObjectEndSpike extends BasicGenerator {

    private static final BlockState STATE_BEDROCK_INFINIBURN = BlockState.of(BEDROCK, 1);
    private static final BlockState STATE_OBSIDIAN = BlockState.of(OBSIDIAN);
    private static final BlockState STATE_IRON_BARS = BlockState.of(IRON_BARS);
    private static final BlockState STATE_AIR = BlockState.of(AIR);

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
        while (position.getY() > 5
                && level.getBlockIdAt(position.getFloorX(), position.getFloorY(), position.getFloorZ()) == AIR)
            position.down();
        for (int z = position.getFloorZ() - radius; z <= position.getFloorZ() + radius; ++z)
            for (int y = position.getFloorY() - radius; y < height; ++y)
                for (int x = position.getFloorX() - radius; x <= position.getFloorX() + radius; ++x) {
                    double d0 = position.getX() - x, d2 = position.getZ() - z;
                    if (d0 * d0 + d2 * d2 <= this.radius * this.radius + 1 && y < height)
                        level.setBlockStateAt(x, y, z, STATE_OBSIDIAN);
                    else if (y > 65)
                        level.setBlockStateAt(x, y, z, STATE_AIR);

                }
        if (hasIronBars) {
            for (int a = -2; a <= 2; ++a)
                for (int b = -2; b <= 2; ++b) {
                    level.setBlockStateAt(a + position.getFloorX(), height + 3, b + position.getFloorZ(),
                            STATE_IRON_BARS);
                    if (abs(a) == 2 || abs(b) == 2) {
                        level.setBlockStateAt(a + position.getFloorX(), height, b + position.getFloorZ(),
                                STATE_IRON_BARS);
                        level.setBlockStateAt(a + position.getFloorX(), height + 1, b + position.getFloorZ(),
                                STATE_IRON_BARS);
                        level.setBlockStateAt(a + position.getFloorX(), height + 2, b + position.getFloorZ(),
                                STATE_IRON_BARS);
                    }

                }
        }
        level.setBlockStateAt(position.getFloorX(), height, position.getFloorZ(), STATE_BEDROCK_INFINIBURN);

        // TODO: Generate Ender Crystal
        return true;
    }
}
