package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockPackedIce;
import cn.nukkit.block.BlockSnowLayer;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.object.BlockManager;

import static cn.nukkit.block.BlockID.AIR;
import static cn.nukkit.block.BlockID.DIRT;
import static cn.nukkit.block.BlockID.GRASS_BLOCK;
import static cn.nukkit.block.BlockID.ICE;
import static cn.nukkit.block.BlockID.PACKED_ICE;
import static cn.nukkit.block.BlockID.SNOW;
import static cn.nukkit.block.BlockID.SNOW_LAYER;

public class IceSpikeFeature extends GenerateFeature {

    public static final String NAME = "minecraft:ice_spike_feature";

    private static final BlockState STATE_SPIKE = BlockPackedIce.PROPERTIES.getDefaultState();
    private static final BlockState STATE_SNOW_LAYER = BlockSnowLayer.PROPERTIES.getDefaultState();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ) ^ NAME.hashCode());
        BlockManager manager = new BlockManager(level);
        int attempts = 3 + random.nextInt(3);
        for (int attempt = 0; attempt < attempts; attempt++) {
            int originX = (chunkX << 4) + random.nextInt(16);
            int originZ = (chunkZ << 4) + random.nextInt(16);
            int originY = chunk.getHeightMap(originX & 15, originZ & 15);

            while (manager.getBlockIfCachedOrLoaded(originX, originY, originZ).isAir() && originY > level.getMinHeight() + 2) {
                originY--;
            }

            if (level.getBiomeId(originX, originY, originZ) != BiomeID.ICE_PLAINS_SPIKES) {
                continue;
            }

            if (!canPlaceOn(manager.getBlockIfCachedOrLoaded(originX, originY, originZ).getId())) {
                continue;
            }

            int height = random.nextInt(4) + 7;
            int width = height / 4 + random.nextInt(2);
            if (width > 1 && random.nextInt(60) == 0) {
                originY += 10 + random.nextInt(30);
            }

            for (int yOff = 0; yOff < height; yOff++) {
                float scale = (1.0F - (float) yOff / height) * width;
                int newWidth = (int) Math.ceil(scale);

                for (int xo = -newWidth; xo <= newWidth; xo++) {
                    float dx = Math.abs(xo) - 0.25F;
                    for (int zo = -newWidth; zo <= newWidth; zo++) {
                        float dz = Math.abs(zo) - 0.25F;
                        if ((xo == 0 && zo == 0 || dx * dx + dz * dz <= scale * scale)
                                && (xo != -newWidth && xo != newWidth && zo != -newWidth && zo != newWidth || random.nextFloat() <= 0.75F)) {
                            int px = originX + xo;
                            int py = originY + yOff;
                            int pz = originZ + zo;
                            String state = manager.getBlockIfCachedOrLoaded(px, py, pz).getId();
                            if (state.equals(AIR) || canReplace(state)) {
                                manager.setBlockStateAt(px, py, pz, STATE_SPIKE);
                            }

                            if (yOff != 0 && newWidth > 1) {
                                int nx = originX + xo;
                                int ny = originY - yOff;
                                int nz = originZ + zo;
                                state = manager.getBlockIfCachedOrLoaded(nx, ny, nz).getId();
                                if (state.equals(AIR) || canReplace(state)) {
                                    manager.setBlockStateAt(nx, ny, nz, STATE_SPIKE);
                                }
                            }
                        }
                    }
                }
            }

            int pillarWidth = width - 1;
            if (pillarWidth < 0) {
                pillarWidth = 0;
            } else if (pillarWidth > 1) {
                pillarWidth = 1;
            }

            for (int xo = -pillarWidth; xo <= pillarWidth; xo++) {
                for (int zo = -pillarWidth; zo <= pillarWidth; zo++) {
                    int cx = originX + xo;
                    int cy = originY - 1;
                    int cz = originZ + zo;
                    int runLength = 50;
                    if (Math.abs(xo) == 1 && Math.abs(zo) == 1) {
                        runLength = random.nextInt(5);
                    }

                    while (cy > 50) {
                        String state = manager.getBlockIfCachedOrLoaded(cx, cy, cz).getId();
                        if (!state.equals(AIR) && !canReplace(state) && !state.equals(PACKED_ICE)) {
                            break;
                        }

                        manager.setBlockStateAt(cx, cy, cz, STATE_SPIKE);
                        cy--;
                        if (--runLength <= 0) {
                            cy -= random.nextInt(5) + 1;
                            runLength = random.nextInt(5);
                        }
                    }
                }
            }

            int snowRadius = Math.max(2, width + 2);
            int topY = originY + height + 1;
            int bottomY = Math.max(level.getMinHeight() + 1, originY - 2);
            for (int x = originX - snowRadius; x <= originX + snowRadius; x++) {
                for (int z = originZ - snowRadius; z <= originZ + snowRadius; z++) {
                    for (int y = topY; y >= bottomY; y--) {
                        String here = manager.getBlockIfCachedOrLoaded(x, y, z).getId();
                        if (here.equals(PACKED_ICE)) {
                            String above = manager.getBlockIfCachedOrLoaded(x, y + 1, z).getId();
                            if (above.equals(AIR)) {
                                manager.setBlockStateAt(x, y + 1, z, STATE_SNOW_LAYER);
                            }
                            break;
                        } else if (!here.equals(AIR) && !here.equals(SNOW_LAYER)) {
                            break;
                        }
                    }
                }
            }
        }

        queueObject(chunk, manager);
    }

    private static boolean canPlaceOn(String id) {
        return id.equals(SNOW) || id.equals(SNOW_LAYER) || id.equals(DIRT) || id.equals(GRASS_BLOCK) || id.equals(ICE) || id.equals(PACKED_ICE);
    }

    private static boolean canReplace(String id) {
        return id.equals(SNOW)
                || id.equals(SNOW_LAYER)
                || id.equals(ICE)
                || id.equals(AIR)
                || id.equals(DIRT)
                || id.equals(GRASS_BLOCK);
    }

    @Override
    public String name() {
        return NAME;
    }
}
