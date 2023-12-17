package cn.nukkit.level.biome.type;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public abstract class WateryBiome extends CoveredBiome {


    @Override
    public int getSurfaceDepth(int y) {
        if (useNewRakNetSurfaceDepth()) {
            return getSurfaceDepth(0, y, 0);
        }
        return 0;
    }


    @Override
    public int getSurfaceBlock(int y) {
        if (useNewRakNetSurface()) {
            return getSurfaceId(0, y, 0) >> 4;
        }
        //doesn't matter, surface depth is 0
        return 0;
    }


    @Override
    public int getGroundDepth(int y) {
        if (useNewRakNetGroundDepth()) {
            return getGroundDepth(0, y, 0);
        }
        return 5;
    }


    @Override
    public int getGroundBlock(int y) {
        if (useNewRakNetGround()) {
            return getGroundId(0, y, 0) >> 4;
        }
        return DIRT;
    }
}
