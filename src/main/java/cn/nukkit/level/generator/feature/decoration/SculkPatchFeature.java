package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLichen;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockRedWool;
import cn.nukkit.block.BlockSculk;
import cn.nukkit.block.BlockSculkVein;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStone;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.random.NukkitRandom;

import java.util.HashMap;
import java.util.Map;

public class SculkPatchFeature extends GenerateFeature {

    public static final String NAME = "minecraft:sculk_patch_feature";

    private static final BlockState SCULK = BlockSculk.PROPERTIES.getDefaultState();

    private SimplexF noise;

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        SimplexF noise = ((NormalObjectHolder) level.getGeneratorObjectHolder()).getFeatureHolder().getSculkPatch();
        BlockManager manager = new BlockManager(level);
        for(int x = 0; x < 16; x++) {
            int baseX = ((chunk.getX() << 4) + x);
            for (int z = 0; z < 16; z++) {
                int baseZ = ((chunk.getZ() << 4) + z);
                for (int y = level.getMaxHeight(); y > level.getMinHeight(); y--) {
                    if(chunk.getSection(y >> 4).getBiomeId(x, y & 0x0f, z) == BiomeID.DEEP_DARK) {
                        Block block = manager.getBlockIfCachedOrLoaded(baseX, y, baseZ);
                        if (!block.isAir()) {
                            boolean air = false;
                            for (BlockFace face : BlockFace.values()) {
                                BlockVector3 side = new BlockVector3(baseX, y, baseZ).getSide(face);
                                Block block1 = manager.getBlockIfCachedOrLoaded(side.x, side.y, side.z);
                                if(block1.getChunkX() == chunkX && block1.getChunkZ() == chunkZ) {
                                    if (block1.isAir()) {
                                        air = true;
                                        break;
                                    }
                                }
                            }
                            if(air) {
                                float noiseV = noise.noise3D(baseX * 5.5f, y * 5.5f, baseZ * 5.5f, true);
                                if (noiseV < 0.1f) {
                                    manager.setBlockStateAt(baseX, y, baseZ, SCULK);
                                }
                            }
                        }
                    }
                }
            }
        }
        Map<BlockVector3, Integer> veinsToPlace = new HashMap<>();

        for (Block block : manager.getBlocks()) {
            if (!block.getId().equals(BlockID.SCULK)) {
                continue;
            }

            BlockVector3 sculkPos = block.asBlockVector3();

            for (BlockFace face : BlockFace.values()) {
                BlockVector3 neighborPos = sculkPos.getSide(face);

                if (neighborPos.getChunkX() != sculkPos.getChunkX() || neighborPos.getChunkZ() != sculkPos.getChunkZ()) {
                    continue;
                }

                Block neighbor = manager.getBlockAt(neighborPos.asVector3());

                boolean freeSpot = neighbor.isAir() || neighbor instanceof BlockSculkVein;
                boolean carrierBlock = neighbor.isSolid() && !neighbor.getId().equals(BlockID.SCULK) && !(neighbor instanceof BlockSculkVein);

                if (freeSpot) {
                    int bits = 0;
                    if (neighbor instanceof BlockSculkVein) {
                        bits = neighbor.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);
                    }
                    for (BlockFace supportFace : BlockFace.values()) {
                        BlockVector3 supportPos = neighborPos.getSide(supportFace);
                        Block support = manager.getBlockAt(supportPos.asVector3());
                        if (!support.isSolid()
                                || support.getId().equals(BlockID.SCULK)
                                || support instanceof BlockSculkVein) continue;
                        bits |= 1 << supportFace.getDUSWNEIndex();
                    }

                    if (bits != 0) {
                        veinsToPlace.merge(neighborPos, bits, (oldBits, newBits) -> oldBits | newBits);
                    }
                }

                if (carrierBlock) {
                    for (BlockFace outerFace : BlockFace.values()) {
                        BlockVector3 veinPos = neighborPos.getSide(outerFace);

                        if (veinPos.getChunkX() != sculkPos.getChunkX() || veinPos.getChunkZ() != sculkPos.getChunkZ()) continue;

                        Block existing = manager.getBlockAt(veinPos.asVector3());
                        if (!existing.isAir() && !(existing instanceof BlockSculkVein)) continue;

                        int bits = 0;
                        if (existing instanceof BlockSculkVein) {
                            bits = existing.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);
                        }

                        bits |= 1 << outerFace.getOpposite().getDUSWNEIndex();
                        veinsToPlace.merge(veinPos, bits, (oldBits, newBits) -> oldBits | newBits);
                    }
                }
            }
        }

        for (Map.Entry<BlockVector3, Integer> entry : veinsToPlace.entrySet()) {
            BlockVector3 veinPos = entry.getKey();
            Block existing = manager.getBlockAt(veinPos.asVector3());

            int currentBits = 0;
            if (existing instanceof BlockSculkVein) {
                currentBits = existing.getPropertyValue(CommonBlockProperties.MULTI_FACE_DIRECTION_BITS);
            }

            int mergedBits = currentBits | entry.getValue();
            if (mergedBits == 0) continue;
            manager.setBlockStateAt(
                    existing,
                    BlockSculkVein.PROPERTIES.getBlockState(
                            CommonBlockProperties.MULTI_FACE_DIRECTION_BITS.createValue(mergedBits)
                    )
            );
        }
        queueObject(chunk, manager);
    }

    @Override
    public String name() {
        return NAME;
    }
}
