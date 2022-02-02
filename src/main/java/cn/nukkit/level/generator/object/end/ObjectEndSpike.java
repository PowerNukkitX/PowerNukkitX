package cn.nukkit.level.generator.object.end;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityEndCrystal;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import static cn.nukkit.block.BlockID.BEDROCK;
import static cn.nukkit.block.BlockID.OBSIDIAN;
import static cn.nukkit.block.BlockID.IRON_BARS;

/**
 * @author GoodLucky777
 */
public class ObjectEndSpike extends BasicGenerator {

    private static final BlockState STATE_BEDROCK_INFINIBURN = BlockState.of(BEDROCK, 1);
    private static final BlockState STATE_OBSIDIAN = BlockState.of(OBSIDIAN);
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
            for (int x = position.getFloorX() - radius; x <= position.getFloorX() + radius; x++) {
                for (int z = position.getFloorZ() - radius; z <= position.getFloorZ() + radius; z++) {
                    if (y < height && tempPosition.setComponents(x, y, z).distanceSquared((double) position.getFloorX(), (double) position.getFloorY(), (double) position.getFloorZ()) <= Math.pow(radius, 2) + 1) {
                        level.setBlockStateAt(x, y, z, STATE_OBSIDIAN);
                    } else if (y > 65) { // To remove end stones if this is lower than ground height
                        level.setBlockStateAt(x, y, z, BlockState.AIR);
                    }
                }
            }
        }
        
        // Generate an iron bars if hasIronBars is true
        if (hasIronBars) {
            for (int y = height; y <= 3; y++) {
                for (int x = position.getFloorX() - 2; x <= position.getFloorX() + 2; x++) {
                    for (int z = position.getFloorZ() - 2; z <= position.getFloorZ() + 2; z++) {
                        if (y == 3 || (Math.abs(x) == 2 || Math.abs(z) == 2)) {
                            level.setBlockStateAt(x, y, z, STATE_IRON_BARS);
                        }
                    }
                }
            }
        }
        
        // Generate a bedrock and an end crystal
        level.setBlockStateAt(position.getFloorX(), height, position.getFloorZ(), STATE_BEDROCK_INFINIBURN);
        
        Entity endCrystal = Entity.createEntity(EntityEndCrystal.NETWORK_ID, new Position(position.getFloorX() + 0.5, height + 1, position.getFloorZ() + 0.5));
        level.getChunk(position.getFloorX() >> 4, position.getFloorZ() >> 4).addEntity(endCrystal);
        
        return true;
    }
}
