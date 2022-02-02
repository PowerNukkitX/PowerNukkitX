package cn.nukkit.level.generator.object.end;

import cn.nukkit.block.BlockChorusFlower;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import static cn.nukkit.block.BlockID.AIR;
import static cn.nukkit.block.BlockID.CHORUS_FLOWER;
import static cn.nukkit.block.BlockID.CHORUS_PLANT;

/**
 * @author GoodLucky777
 */
public class ObjectChorusTree extends BasicGenerator {

    private static final BlockState STATE_CHORUS_FLOWER_FULLY_AGED = BlockState.of(CHORUS_FLOWER, BlockChorusFlower.AGE.getMaxValue());
    private static final BlockState STATE_CHORUS_PLANT = BlockState.of(CHORUS_PLANT);
    
    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        return this.generate(level, rand, position, 8);
    }
    
    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position, int maxSize) {
        level.setBlockStateAt(position.getFloorX(), position.getFloorY(), position.getFloorZ(), STATE_CHORUS_PLANT);
        this.growImmediately(level, rand, position, maxSize, 0);
        return true;
    }
    
    public void growImmediately(ChunkManager level, NukkitRandom random, Vector3 position, int maxSize, int age) {
        // Random height
        int height = 1 + random.nextBoundedInt(4);
        if (age == 0) {
            height++;
        }
        
        // Grow upward
        for (int y = 1; y <= height; y++) {
            if (!this.isHorizontalAir(level, position.up(y))) {
                return;
            }
            level.setBlockStateAt(position.getFloorX(), position.getFloorY() + y, position.getFloorZ(), STATE_CHORUS_PLANT);
        }
        
        if (age < 4) {
            // Grow horizontally
            int attempt = random.nextBoundedInt(4);
            if (age == 0) {
                attempt++;
            }
            
            for (int i = 0; i < attempt; i++) {
                BlockFace face = BlockFace.Plane.HORIZONTAL.random(random);
                Vector3 check = position.up(height).getSide(face);
                if (level.getBlockIdAt(check.getFloorX(), check.getFloorY(), check.getFloorZ()) == AIR && level.getBlockIdAt(check.getFloorX(), check.getFloorY() - 1, check.getFloorZ()) == AIR) {
                    if (Math.abs(check.getFloorX() - position.getFloorX()) < maxSize && Math.abs(check.getFloorZ() - position.getFloorZ()) < maxSize && this.isHorizontalAirExcept(level, check, face.getOpposite())) {
                        level.setBlockStateAt(check.getFloorX(), check.getFloorY(), check.getFloorZ(), STATE_CHORUS_PLANT);
                        this.growImmediately(level, random, check, maxSize, age + 1);
                    }
                }
            }
        } else {
            // Death
            level.setBlockStateAt(position.getFloorX(), position.getFloorY() + height, position.getFloorZ(), STATE_CHORUS_FLOWER_FULLY_AGED);
        }
    }
    
    private boolean isHorizontalAir(ChunkManager level, Vector3 vector3) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Vector3 side = vector3.getSide(face);
            if (level.getBlockIdAt(side.getFloorX(), side.getFloorY(), side.getFloorZ()) != AIR) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isHorizontalAirExcept(ChunkManager level, Vector3 vector3, BlockFace except) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (face != except) {
                Vector3 side = vector3.getSide(face);
                if (level.getBlockIdAt(side.getFloorX(), side.getFloorY(), side.getFloorZ()) != AIR) {
                    return false;
                }
            }
        }
        return true;
    }
}
