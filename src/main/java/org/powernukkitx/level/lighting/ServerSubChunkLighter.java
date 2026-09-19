package org.powernukkitx.level.lighting;
import org.powernukkitx.block.BlockLightProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelProvider;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

import java.util.Collection;

/**
 * Processes server-side block and sky light changes for a chunk and its adjacent subchunks. It queues additions and
 * removals, exposes light access to the relighter, and marks changed data for client sync.
 *
 * @author Curse
 */
public final class ServerSubChunkLighter implements SubChunkLightAccess {
    private static final int NEIGHBORHOOD_SIZE = 3;
    private static final int NEIGHBORHOOD_RADIUS = 1;
    private final IChunk[][] chunks = new IChunk[NEIGHBORHOOD_SIZE][NEIGHBORHOOD_SIZE];
    private final int centerChunkX;
    private final int centerChunkZ;
    private final int minHeight;
    private final int maxHeight;
    private final SubChunkRelighter relighter;
    private final Long2IntOpenHashMap removalLightFilters = new Long2IntOpenHashMap();
    /**
     * Creates a new ServerSubChunkLighter instance.
     *
     * @param centerChunk value for this API
     */
    public ServerSubChunkLighter(Chunk centerChunk) {
        this.centerChunkX = centerChunk.getX();
        this.centerChunkZ = centerChunk.getZ();
        this.minHeight = centerChunk.getDimensionData().getMinHeight();
        this.maxHeight = centerChunk.getDimensionData().getMaxHeight();
        LevelProvider provider = centerChunk.getProvider();
        for (int offsetX = -NEIGHBORHOOD_RADIUS; offsetX <= NEIGHBORHOOD_RADIUS; offsetX++) {
            for (int offsetZ = -NEIGHBORHOOD_RADIUS; offsetZ <= NEIGHBORHOOD_RADIUS; offsetZ++) {
                int chunkX = centerChunkX + offsetX;
                int chunkZ = centerChunkZ + offsetZ;
                if (offsetX == 0 && offsetZ == 0) {
                    chunks[offsetX + NEIGHBORHOOD_RADIUS][offsetZ + NEIGHBORHOOD_RADIUS] = centerChunk;
                } else {
                    chunks[offsetX + NEIGHBORHOOD_RADIUS][offsetZ + NEIGHBORHOOD_RADIUS] = provider.getLoadedChunk(chunkX, chunkZ);
                }
            }
        }

        this.removalLightFilters.defaultReturnValue(-1);
        this.relighter = new SubChunkRelighter(this);
    }

    /**
     * Returns the relighter used by this chunk lighter.
     * @return the requested value
     */
    public SubChunkRelighter getRelighter() {
        return relighter;
    }

    /**
     * Processes queued work.
     */
    public void process() {
        try {
            relighter.process();
        } finally {
            removalLightFilters.clear();
        }
    }

    /**
     * Returns whether pending work remains.
     * @return the requested value
     */
    public boolean hasPendingWork() {
        return relighter.hasPendingWork();
    }

    /**
     * Queues subchunk light updates.
     *
     * @param sectionY value for this API
     * @param updates value for this API
     */
    public void queueUpdates(int sectionY, Collection<SubChunkLightUpdate> updates) {
        if (updates == null || updates.isEmpty()) {
            return;
        }

        int baseX = centerChunkX << 4;
        int baseY = sectionY << 4;
        int baseZ = centerChunkZ << 4;
        /*
         * First register every old attenuation value.
         *
         * This must happen before any update is queued because the removal
         * phase can reach another changed position from the same batch.
         */
        for (SubChunkLightUpdate update : updates) {
            if (!update.filterChanged()) {
                continue;
            }

            int x = baseX + update.localX();
            int y = baseY + update.localY();
            int z = baseZ + update.localZ();
            removalLightFilters.put(packPosition(x, y, z), Math.max(1, update.oldFilter()));
        }

        /*
         * Queue all updates before running any relighting phase.
         */
        for (SubChunkLightUpdate update : updates) {
            int x = baseX + update.localX();
            int y = baseY + update.localY();
            int z = baseZ + update.localZ();
            if (!isAvailable(x, y, z)) {
                continue;
            }

            switch (update.type()) {
                case BLOCK -> queueBlockUpdate(x, y, z, update);
                case SKY -> queueSkyUpdate(x, y, z, update);
            }
        }
    }

    private void queueBlockUpdate(int x, int y, int z, SubChunkLightUpdate update) {
        int currentLight = getBlockLight(x, y, z);
        boolean sourceDecreased = update.newLight() < update.oldLight();
        boolean sourceIncreased = update.newLight() > update.oldLight();
        boolean filterIncreased = update.newFilter() > update.oldFilter();
        /*
         * A weaker source invalidates paths that depended on the old source.
         */
        if (sourceDecreased) {
            if (currentLight <= update.oldLight()) {
                setBlockLight(x, y, z, update.newLight());
            }

            queueBlockRemoval(x, y, z, update.oldLight());
        }

        /*
         * Increasing attenuation can invalidate not only source light but
         * propagated light passing through this cell.
         *
         * In this case the currently stored light is the old contribution
         * that must be invalidated.
         */
        if (filterIncreased) {
            currentLight = getBlockLight(x, y, z);
            if (currentLight > 0) {
                setBlockLight(x, y, z, update.newLight());
                queueBlockRemoval(x, y, z, currentLight);
            }
        }

        /*
         * A stronger source immediately becomes a new addition seed.
         */
        if (sourceIncreased) {
            currentLight = getBlockLight(x, y, z);
            if (update.newLight() > currentLight) {
                setBlockLight(x, y, z, update.newLight());
            }
        }

        /*
         * A source that still exists after a decrease must be re-added after
         * the removal phase.
         */
        if (update.newLight() > 0) {
            currentLight = getBlockLight(x, y, z);
            if (currentLight < update.newLight()) {
                setBlockLight(x, y, z, update.newLight());
            }

            queueBlockAddition(x, y, z);
        }

        /*
         * Any attenuation change requires neighboring valid light to be
         * eligible for re-propagation.
         *
         * For decreased attenuation this opens the new route.
         * For increased attenuation this restores surviving alternate paths
         * after removal.
         */
        if (update.filterChanged()) {
            queueNeighborBlockAdditions(x, y, z);
        }
    }

    private void queueSkyUpdate(int x, int y, int z, SubChunkLightUpdate update) {
        int currentLight = getSkyLight(x, y, z);
        boolean sourceDecreased = update.newLight() < update.oldLight();
        boolean sourceIncreased = update.newLight() > update.oldLight();
        boolean filterIncreased = update.newFilter() > update.oldFilter();
        if (sourceDecreased) {
            if (currentLight <= update.oldLight()) {
                setSkyLight(x, y, z, update.newLight());
            }

            queueSkyRemoval(x, y, z, update.oldLight());
        }

        if (filterIncreased) {
            currentLight = getSkyLight(x, y, z);
            if (currentLight > 0) {
                setSkyLight(x, y, z, update.newLight());
                queueSkyRemoval(x, y, z, currentLight);
            }
        }

        if (sourceIncreased) {
            currentLight = getSkyLight(x, y, z);
            if (update.newLight() > currentLight) {
                setSkyLight(x, y, z, update.newLight());
            }
        }

        if (update.newLight() > 0) {
            currentLight = getSkyLight(x, y, z);
            if (currentLight < update.newLight()) {
                setSkyLight(x, y, z, update.newLight());
            }

            queueSkyAddition(x, y, z);
        }

        if (update.filterChanged()) {
            queueNeighborSkyAdditions(x, y, z);
        }
    }

    private void queueNeighborBlockAdditions(int x, int y, int z) {
        queueBlockAdditionIfAvailable(x - 1, y, z);
        queueBlockAdditionIfAvailable(x + 1, y, z);
        queueBlockAdditionIfAvailable(x, y - 1, z);
        queueBlockAdditionIfAvailable(x, y + 1, z);
        queueBlockAdditionIfAvailable(x, y, z - 1);
        queueBlockAdditionIfAvailable(x, y, z + 1);
    }

    private void queueNeighborSkyAdditions(int x, int y, int z) {
        queueSkyAdditionIfAvailable(x - 1, y, z);
        queueSkyAdditionIfAvailable(x + 1, y, z);
        queueSkyAdditionIfAvailable(x, y - 1, z);
        queueSkyAdditionIfAvailable(x, y + 1, z);
        queueSkyAdditionIfAvailable(x, y, z - 1);
        queueSkyAdditionIfAvailable(x, y, z + 1);
    }

    private void queueBlockAdditionIfAvailable(int x, int y, int z) {
        if (!isAvailable(x, y, z)) {
            return;
        }

        if (getBlockLight(x, y, z) == 0) {
            return;
        }

        queueBlockAddition(x, y, z);
    }

    private void queueSkyAdditionIfAvailable(int x, int y, int z) {
        if (!isAvailable(x, y, z)) {
            return;
        }

        if (getSkyLight(x, y, z) == 0) {
            return;
        }

        queueSkyAddition(x, y, z);
    }

    /**
     * Queues initial neighbor light sources.
     *
     * @param sectionY value for this API
     */
    public void queueInitialNeighborSources(int sectionY) {
        int sectionMinY = Math.max(sectionY << 4, minHeight);
        int sectionMaxY = Math.min((sectionY << 4) + 15, maxHeight);
        int baseX = centerChunkX << 4;
        int baseZ = centerChunkZ << 4;
        /*
         * -X / +X faces
         */
        for (int y = sectionMinY; y <= sectionMaxY; y++) {
            for (int z = 0; z < 16; z++) {
                queueInitialBoundaryPair(baseX, y, baseZ + z, baseX - 1, y, baseZ + z);
                queueInitialBoundaryPair(baseX + 15, y, baseZ + z, baseX + 16, y, baseZ + z);
            }
        }

        /*
         * -Z / +Z faces
         */
        for (int y = sectionMinY; y <= sectionMaxY; y++) {
            for (int x = 0; x < 16; x++) {
                queueInitialBoundaryPair(baseX + x, y, baseZ, baseX + x, y, baseZ - 1);
                queueInitialBoundaryPair(baseX + x, y, baseZ + 15, baseX + x, y, baseZ + 16);
            }
        }

        /*
         * -Y face
         */
        if (sectionMinY > minHeight) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    queueInitialBoundaryPair(
                            baseX + x,
                            sectionMinY,
                            baseZ + z,
                            baseX + x,
                            sectionMinY - 1,
                            baseZ + z);
                }
            }
        }

        /*
         * +Y face
         */
        if (sectionMaxY < maxHeight) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    queueInitialBoundaryPair(
                            baseX + x,
                            sectionMaxY,
                            baseZ + z,
                            baseX + x,
                            sectionMaxY + 1,
                            baseZ + z);
                }
            }
        }
    }

    private void queueInitialBoundaryPair(
            int firstX, int firstY, int firstZ, int secondX, int secondY, int secondZ) {
        if (!isAvailable(firstX, firstY, firstZ) || !isAvailable(secondX, secondY, secondZ)) {
            return;
        }

        /*
         * Queue only the brighter side so unchanged boundaries do not seed
         * redundant sky-light propagation.
         */
        int firstSky = getSkyLight(firstX, firstY, firstZ);
        int secondSky = getSkyLight(secondX, secondY, secondZ);
        if (firstSky > secondSky) {
            queueSkyAddition(firstX, firstY, firstZ);
        } else if (secondSky > firstSky) {
            queueSkyAddition(secondX, secondY, secondZ);
        }

        /*
         * BLOCK:
         *
         * A neighboring SubChunk may not have run its own original lighting
         * yet. Its intrinsic emitter is nevertheless a valid source.
         */
        int firstBlock = getBlockLight(firstX, firstY, firstZ);
        int firstEmission = getBlockEmission(firstX, firstY, firstZ);
        if (firstEmission > firstBlock) {
            setBlockLight(firstX, firstY, firstZ, firstEmission);
            firstBlock = firstEmission;
        }

        int secondBlock = getBlockLight(secondX, secondY, secondZ);
        int secondEmission = getBlockEmission(secondX, secondY, secondZ);
        if (secondEmission > secondBlock) {
            setBlockLight(secondX, secondY, secondZ, secondEmission);
            secondBlock = secondEmission;
        }

        if (firstBlock > secondBlock) {
            queueBlockAddition(firstX, firstY, firstZ);
        } else if (secondBlock > firstBlock) {
            queueBlockAddition(secondX, secondY, secondZ);
        }
    }

    /**
     * Queues sky light removal.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @param oldLight value for this API
     */
    public void queueSkyRemoval(int x, int y, int z, int oldLight) {
        relighter.queueSkyRemoval(x, y, z, oldLight);
    }

    /**
     * Queues sky light addition.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     */
    public void queueSkyAddition(int x, int y, int z) {
        relighter.queueSkyAddition(x, y, z);
    }

    /**
     * Queues block light removal.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     * @param oldLight value for this API
     */
    public void queueBlockRemoval(int x, int y, int z, int oldLight) {
        relighter.queueBlockRemoval(x, y, z, oldLight);
    }

    /**
     * Queues block light addition.
     *
     * @param x value for this API
     * @param y value for this API
     * @param z value for this API
     */
    public void queueBlockAddition(int x, int y, int z) {
        relighter.queueBlockAddition(x, y, z);
    }

    @Override
    public boolean isAvailable(int x, int y, int z) {
        if (y < minHeight || y > maxHeight) {
            return false;
        }

        return getChunk(x, z) != null;
    }

    @Override
    public int getLightFilter(int x, int y, int z) {
        IChunk chunk = getChunk(x, z);
        if (chunk == null || y < minHeight || y > maxHeight) {
            return 15;
        }

        if (chunk instanceof Chunk concreteChunk) {
            int packed = concreteChunk.getCombinedLightProperties(x & 0x0f, y, z & 0x0f);
            return BlockLightProperties.lightFilter(packed);
        }

        int localX = x & 0x0f;
        int localZ = z & 0x0f;
        BlockState layer0 = chunk.getBlockState(localX, y, localZ, 0);
        BlockState layer1 = chunk.getBlockState(localX, y, localZ, 1);
        return Math.max(
                BlockLightProperties.lightFilter(BlockLightProperties.packed(layer0)),
                BlockLightProperties.lightFilter(BlockLightProperties.packed(layer1)));
    }

    @Override
    public int getRemovalLightFilter(int x, int y, int z) {
        int oldFilter = removalLightFilters.get(packPosition(x, y, z));
        if (oldFilter >= 0) {
            return oldFilter;
        }

        return getLightFilter(x, y, z);
    }

    @Override
    public int getBlockEmission(int x, int y, int z) {
        IChunk chunk = getChunk(x, z);
        if (chunk == null || y < minHeight || y > maxHeight) {
            return 0;
        }

        if (chunk instanceof Chunk concreteChunk) {
            int packed = concreteChunk.getCombinedLightProperties(x & 0x0f, y, z & 0x0f);
            return BlockLightProperties.lightLevel(packed);
        }

        int localX = x & 0x0f;
        int localZ = z & 0x0f;
        BlockState layer0 = chunk.getBlockState(localX, y, localZ, 0);
        BlockState layer1 = chunk.getBlockState(localX, y, localZ, 1);
        return Math.max(
                BlockLightProperties.lightLevel(BlockLightProperties.packed(layer0)),
                BlockLightProperties.lightLevel(BlockLightProperties.packed(layer1)));
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        IChunk chunk = getChunk(x, z);
        if (chunk == null || y < minHeight || y > maxHeight) {
            return 0;
        }

        ChunkSection section = chunk.getSection(y >> 4);
        if (section == null) {
            return 0;
        }

        return section.lighting().getBlockLight(IChunk.index(x & 0x0f, y & 0x0f, z & 0x0f));
    }

    @Override
    public void setBlockLight(int x, int y, int z, int light) {
        ChunkSection section = getOrCreateSection(x, y, z);
        if (section == null) {
            return;
        }

        section.lighting().setBlockLight(IChunk.index(x & 0x0f, y & 0x0f, z & 0x0f), (byte) light);
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        IChunk chunk = getChunk(x, z);
        if (chunk == null || y < minHeight || y > maxHeight) {
            return 0;
        }

        return chunk.getBlockSkyLight(x & 0x0f, y, z & 0x0f);
    }

    @Override
    public void setSkyLight(int x, int y, int z, int light) {
        ChunkSection section = getOrCreateSection(x, y, z);
        if (section == null) {
            return;
        }

        section.lighting().setSkyLight(IChunk.index(x & 0x0f, y & 0x0f, z & 0x0f), (byte) light);
    }

    private IChunk getChunk(int blockX, int blockZ) {
        int chunkX = Math.floorDiv(blockX, 16);
        int chunkZ = Math.floorDiv(blockZ, 16);
        int offsetX = chunkX - centerChunkX;
        int offsetZ = chunkZ - centerChunkZ;
        if (offsetX < -NEIGHBORHOOD_RADIUS
                || offsetX > NEIGHBORHOOD_RADIUS
                || offsetZ < -NEIGHBORHOOD_RADIUS
                || offsetZ > NEIGHBORHOOD_RADIUS) {
            return null;
        }

        return chunks[offsetX + NEIGHBORHOOD_RADIUS][offsetZ + NEIGHBORHOOD_RADIUS];
    }

    private ChunkSection getOrCreateSection(int blockX, int blockY, int blockZ) {
        if (blockY < minHeight || blockY > maxHeight) {
            return null;
        }

        IChunk chunk = getChunk(blockX, blockZ);
        if (!(chunk instanceof Chunk concreteChunk)) {
            return null;
        }

        return concreteChunk.getOrCreateSectionForLighting(blockY >> 4);
    }

    private static long packPosition(int x, int y, int z) {
        return ((long) x & 0x3ffffffL) << 38 | ((long) z & 0x3ffffffL) << 12 | ((long) y & 0xfffL);
    }
}
