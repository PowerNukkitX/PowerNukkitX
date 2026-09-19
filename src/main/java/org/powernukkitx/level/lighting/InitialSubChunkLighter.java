package org.powernukkitx.level.lighting;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.LevelProvider;

import java.util.Arrays;

/**
 * Relights one subchunk using its loaded 3x3 chunk neighborhood.
 *
 * @author Curse
 */
public final class InitialSubChunkLighter {
    private static final int MAX_LIGHT = 15;
    private static final int CENTER = 1;
    private static final int NEIGHBORHOOD_SIZE = 3;
    private static final int SLOT_TABLE_SIZE = 3 * 4 * 4;
    private static final byte SKY_UNLOADED = 0;
    private static final byte SKY_ZERO = 1;
    private static final byte SKY_MAX = 2;
    private static final byte SKY_EXPLICIT = 3;
    private static final byte[] ZERO_PROPERTIES = new byte[ChunkSection.SIZE];
    private static final int WORK_ITEM_COUNT = 3 * 4 * 4 * ChunkSection.SIZE;
    private static final long[] OUTER_EDGE_TO_DO = createOuterEdgeToDo();
    private static final int[] NEIGHBOR_DELTAS = {
        SubChunkLightIndex.NEGATIVE_X,
        SubChunkLightIndex.POSITIVE_X,
        SubChunkLightIndex.NEGATIVE_Y,
        SubChunkLightIndex.POSITIVE_Y,
        SubChunkLightIndex.NEGATIVE_Z,
        SubChunkLightIndex.POSITIVE_Z
    };
    private final int minHeight;
    private final int minSectionY;
    private final int maxSectionY;
    private final IChunk[][] chunks = new IChunk[NEIGHBORHOOD_SIZE][NEIGHBORHOOD_SIZE];
    private final Slot[] slots = new Slot[SLOT_TABLE_SIZE];
    private final IntWorkQueue[] skySourceQueues = createQueues();
    private final IntWorkQueue[] skyPropagationQueues = createQueues();
    private final IntWorkQueue[] blockAdditionQueues = createQueues();
    private final long[] skyToDo = new long[(WORK_ITEM_COUNT + 63) >>> 6];
    private final long[] blockToDo = new long[(WORK_ITEM_COUNT + 63) >>> 6];
    private int skySourcePendingMask;
    private int skyPropagationPendingMask;
    private int blockPendingMask;

    /**
     * Creates a lighter centered on the supplied chunk.
     */
    public InitialSubChunkLighter(Chunk centerChunk) {
        this.minHeight = centerChunk.getDimensionData().getMinHeight();
        this.minSectionY = centerChunk.getDimensionData().getMinSectionY();
        this.maxSectionY = centerChunk.getDimensionData().getMaxSectionY();
        LevelProvider provider = centerChunk.getProvider();
        for (int subChunkX = 0; subChunkX < NEIGHBORHOOD_SIZE; subChunkX++) {
            int chunkX = centerChunk.getX() + subChunkX - CENTER;
            for (int subChunkZ = 0; subChunkZ < NEIGHBORHOOD_SIZE; subChunkZ++) {
                int chunkZ = centerChunk.getZ() + subChunkZ - CENTER;
                if (subChunkX == CENTER && subChunkZ == CENTER) {
                    chunks[subChunkX][subChunkZ] = centerChunk;
                } else {
                    chunks[subChunkX][subChunkZ] = provider.getLoadedChunk(chunkX, chunkZ);
                }
            }
        }

        for (int subChunkX = 0; subChunkX < NEIGHBORHOOD_SIZE; subChunkX++) {
            for (int subChunkZ = 0; subChunkZ < NEIGHBORHOOD_SIZE; subChunkZ++) {
                for (int subChunkY = 0; subChunkY < NEIGHBORHOOD_SIZE; subChunkY++) {
                    slots[slotIndex(subChunkX, subChunkY, subChunkZ)] = new Slot();
                }
            }
        }
    }

    /**
     * Relights the requested section and commits changed light data.
     */
    public boolean processSection(int sectionY) {
        bindNeighborhood(sectionY);
        Slot center = slots[slotIndex(CENTER, CENTER, CENTER)];
        if (!center.available || center.section == null) return false;

        clearWorkState();
        center.clearBlockLight();
        seedCenter(center);
        seedBoundarySources();
        processSkyAddition();
        processBlockAddition();
        commitTouchedSections();
        return true;
    }

    private void bindNeighborhood(int centerSectionY) {
        for (int subChunkY = 0; subChunkY < NEIGHBORHOOD_SIZE; subChunkY++) {
            int sectionY = centerSectionY + subChunkY - CENTER;
            boolean validY = sectionY >= minSectionY && sectionY <= maxSectionY;
            for (int subChunkZ = 0; subChunkZ < NEIGHBORHOOD_SIZE; subChunkZ++) {
                for (int subChunkX = 0; subChunkX < NEIGHBORHOOD_SIZE; subChunkX++) {
                    IChunk chunk = chunks[subChunkX][subChunkZ];
                    Slot slot = slots[slotIndex(subChunkX, subChunkY, subChunkZ)];
                    if (!validY || chunk == null) {
                        slot.bind(null, sectionY, null);
                        continue;
                    }

                    slot.bind(chunk, sectionY, chunk.getSection(sectionY));
                }
            }
        }
    }

    private void clearWorkState() {
        System.arraycopy(OUTER_EDGE_TO_DO, 0, skyToDo, 0, skyToDo.length);
        Arrays.fill(blockToDo, 0L);
        skySourcePendingMask = 0;
        skyPropagationPendingMask = 0;
        blockPendingMask = 0;
        for (int light = 0; light <= MAX_LIGHT; light++) {
            skySourceQueues[light].clear();
            skyPropagationQueues[light].clear();
            blockAdditionQueues[light].clear();
        }
    }

    private void seedCenter(Slot center) {
        byte[] centerProperties = center.getProperties();
        center.loadSkyIfNeeded();
        byte[] centerSky = center.skyMode == SKY_EXPLICIT ? center.skyLight : null;
        for (int cellIndex = 0; cellIndex < ChunkSection.SIZE; cellIndex++) {
            int localX = cellIndex >>> 8 & 0x0f;
            int localZ = cellIndex >>> 4 & 0x0f;
            int localY = cellIndex & 0x0f;
            int properties = centerProperties[cellIndex] & 0xff;
            int emission = properties & 0x0f;
            int skyLight = centerSky == null ? 0 : centerSky[cellIndex] & 0xff;
            boolean queueSky = centerSky != null && canSpreadSkyInsideCenter(centerSky, cellIndex, localX, localY, localZ, skyLight);
            if (emission <= 0 && !queueSky) continue;

            int index = SubChunkLightIndex.pack(CENTER, CENTER, CENTER, localX, localY, localZ);
            if (emission > 0) {
                center.blockLight[cellIndex] = (byte) emission;
                center.dirtyBlock = true;
                queueBlockAddition(index);
            }

            if (queueSky) {
                queueSkySource(index, skyLight);
            }
        }
    }

    private static boolean canSpreadSkyInsideCenter(byte[] skyLight, int cellIndex, int localX, int localY, int localZ, int sourceLight) {
        if (sourceLight <= 1) return false;

        int maximumCandidate = sourceLight - 1;
        return localX > 0 && (skyLight[cellIndex - 0x100] & 0xff) < maximumCandidate
                || localX < 15 && (skyLight[cellIndex + 0x100] & 0xff) < maximumCandidate
                || localY > 0 && (skyLight[cellIndex - 0x001] & 0xff) < maximumCandidate
                || localY < 15 && (skyLight[cellIndex + 0x001] & 0xff) < maximumCandidate
                || localZ > 0 && (skyLight[cellIndex - 0x010] & 0xff) < maximumCandidate
                || localZ < 15 && (skyLight[cellIndex + 0x010] & 0xff) < maximumCandidate;
    }

    private void seedBoundarySources() {
        // X boundaries
        for (int localY = 0; localY < 16; localY++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                seedBoundaryPair(
                        SubChunkLightIndex.pack(CENTER, CENTER, CENTER, 0, localY, localZ),
                        SubChunkLightIndex.pack(0, CENTER, CENTER, 15, localY, localZ));
                seedBoundaryPair(
                        SubChunkLightIndex.pack(CENTER, CENTER, CENTER, 15, localY, localZ),
                        SubChunkLightIndex.pack(2, CENTER, CENTER, 0, localY, localZ));
            }
        }

        // Z boundaries
        for (int localY = 0; localY < 16; localY++) {
            for (int localX = 0; localX < 16; localX++) {
                seedBoundaryPair(
                        SubChunkLightIndex.pack(CENTER, CENTER, CENTER, localX, localY, 0),
                        SubChunkLightIndex.pack(CENTER, CENTER, 0, localX, localY, 15));
                seedBoundaryPair(
                        SubChunkLightIndex.pack(CENTER, CENTER, CENTER, localX, localY, 15),
                        SubChunkLightIndex.pack(CENTER, CENTER, 2, localX, localY, 0));
            }
        }

        // Y boundaries
        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                seedBoundaryPair(
                        SubChunkLightIndex.pack(CENTER, CENTER, CENTER, localX, 0, localZ),
                        SubChunkLightIndex.pack(CENTER, 0, CENTER, localX, 15, localZ));
                seedBoundaryPair(
                        SubChunkLightIndex.pack(CENTER, CENTER, CENTER, localX, 15, localZ),
                        SubChunkLightIndex.pack(CENTER, 2, CENTER, localX, 0, localZ));
            }
        }
    }

    private void seedBoundaryPair(int first, int second) {
        Slot firstSlot = getSlot(first);
        Slot secondSlot = getSlot(second);
        if (firstSlot == null || !firstSlot.available || secondSlot == null || !secondSlot.available) {
            return;
        }

        int firstCellIndex = SubChunkLightIndex.cellIndex(first);
        int secondCellIndex = SubChunkLightIndex.cellIndex(second);
        int firstSky = firstSlot.getSkyLight(firstCellIndex);
        int secondSky = secondSlot.getSkyLight(secondCellIndex);
        if (firstSky > secondSky) {
            queueSkySource(first, firstSky);
        } else if (secondSky > firstSky) {
            queueSkySource(second, secondSky);
        }

        int firstBlock = ensureIntrinsicEmissionResolved(firstSlot, firstCellIndex);
        int secondBlock = ensureIntrinsicEmissionResolved(secondSlot, secondCellIndex);
        if (firstBlock > secondBlock) {
            queueBlockAdditionResolved(first, firstBlock);
        } else if (secondBlock > firstBlock) {
            queueBlockAdditionResolved(second, secondBlock);
        }
    }

    private int ensureIntrinsicEmissionResolved(Slot slot, int cellIndex) {
        slot.ensureBlockLoaded();
        int current = slot.blockLight[cellIndex] & 0xff;
        byte[] properties = slot.properties;
        if (properties == null) {
            properties = slot.getProperties();
        }

        int emission = properties[cellIndex] & 0x0f;
        if (emission > current) {
            slot.blockLight[cellIndex] = (byte) emission;
            slot.dirtyBlock = true;
            current = emission;
        }

        return current;
    }

    private void queueBlockAddition(int index) {
        if (!isAvailable(index)) return;

        int light = getBlockLight(index);
        if (light <= 0) return;

        if (!markToDo(blockToDo, index)) return;

        blockAdditionQueues[light].enqueue(index);
        blockPendingMask |= 1 << light;
    }

    private void queueBlockAdditionResolved(int index, int light) {
        if (light <= 0) return;
        if (!markToDo(blockToDo, index)) return;

        blockAdditionQueues[light].enqueue(index);
        blockPendingMask |= 1 << light;
    }

    private void queueSkySource(int index, int light) {
        if (light <= 1) return;

        skySourceQueues[light].enqueue(index);
        skySourcePendingMask |= 1 << light;
    }

    private void processSkyAddition() {
        while ((skySourcePendingMask | skyPropagationPendingMask) != 0) {
            int pendingMask = skySourcePendingMask | skyPropagationPendingMask;
            int light = 31 - Integer.numberOfLeadingZeros(pendingMask);
            processSkySources(light);
            processSkyPropagation(light);
        }
    }

    private void processSkySources(int light) {
        int lightMask = 1 << light;
        if ((skySourcePendingMask & lightMask) == 0) return;

        IntWorkQueue queue = skySourceQueues[light];
        while (!queue.isEmpty()) {
            int index = queue.dequeue();
            Slot slot = getSlot(index);
            if (slot == null || !slot.available) continue;

            if (!markToDo(skyToDo, index)) continue;

            int currentLight = slot.getSkyLight(SubChunkLightIndex.cellIndex(index));
            if (currentLight != light) continue;

            spreadSkyLight(index, currentLight);
        }

        skySourcePendingMask &= ~lightMask;
    }

    private void processSkyPropagation(int light) {
        int lightMask = 1 << light;
        if ((skyPropagationPendingMask & lightMask) == 0) return;

        IntWorkQueue queue = skyPropagationQueues[light];
        while (!queue.isEmpty()) {
            int index = queue.dequeue();
            Slot slot = slots[slotIndex(index)];
            int cellIndex = SubChunkLightIndex.cellIndex(index);
            slot.setQueuedSkyLight(cellIndex, light);
            spreadSkyLight(index, light);
        }

        skyPropagationPendingMask &= ~lightMask;
    }

    private void spreadSkyLight(int sourceIndex, int sourceLight) {
        if (sourceLight <= 1) return;

        int sourceWord = sourceIndex >>> 6;
        long sourceMask = 1L << (sourceIndex & 63);
        int toDoWord = sourceWord + 64;
        if ((skyToDo[toDoWord] & sourceMask) == 0L) {
            spreadSkyLightTo(sourceIndex + SubChunkLightIndex.POSITIVE_X, sourceLight, toDoWord, sourceMask);
        }

        toDoWord = sourceWord - 64;
        if ((skyToDo[toDoWord] & sourceMask) == 0L) {
            spreadSkyLightTo(sourceIndex + SubChunkLightIndex.NEGATIVE_X, sourceLight, toDoWord, sourceMask);
        }

        long verticalToDo = skyToDo[sourceWord];
        long positiveYMask = sourceMask << 1;
        if ((verticalToDo & positiveYMask) == 0L) {
            spreadSkyLightTo(sourceIndex + SubChunkLightIndex.POSITIVE_Y, sourceLight, sourceWord, positiveYMask);
        }

        long negativeYMask = sourceMask >>> 1;
        if ((verticalToDo & negativeYMask) == 0L) {
            spreadSkyLightTo(sourceIndex + SubChunkLightIndex.NEGATIVE_Y, sourceLight, sourceWord, negativeYMask);
        }

        toDoWord = sourceWord + 1;
        if ((skyToDo[toDoWord] & sourceMask) == 0L) {
            spreadSkyLightTo(sourceIndex + SubChunkLightIndex.POSITIVE_Z, sourceLight, toDoWord, sourceMask);
        }

        toDoWord = sourceWord - 1;
        if ((skyToDo[toDoWord] & sourceMask) == 0L) {
            spreadSkyLightTo(sourceIndex + SubChunkLightIndex.NEGATIVE_Z, sourceLight, toDoWord, sourceMask);
        }
    }

    private void spreadSkyLightTo(int neighborIndex, int sourceLight, int toDoWord, long toDoMask) {
        Slot slot = slots[slotIndex(neighborIndex)];
        if (slot == null || !slot.available) return;

        int cellIndex = SubChunkLightIndex.cellIndex(neighborIndex);
        int maximumCandidate = sourceLight - 1;
        int current = slot.getSkyLight(cellIndex);
        if (current >= maximumCandidate) return;

        byte[] properties = slot.properties;
        if (properties == null) {
            properties = slot.getProperties();
        }

        int filter = (properties[cellIndex] & 0xff) >>> 4;
        int candidate = sourceLight - (filter < 1 ? 1 : filter);
        if (candidate <= current) return;

        skyToDo[toDoWord] |= toDoMask;
        skyPropagationQueues[candidate].enqueue(neighborIndex);
        skyPropagationPendingMask |= 1 << candidate;
    }

    private void processBlockAddition() {
        while (blockPendingMask != 0) {
            int light = 31 - Integer.numberOfLeadingZeros(blockPendingMask);
            IntWorkQueue queue = blockAdditionQueues[light];
            while (!queue.isEmpty()) {
                int index = queue.dequeue();
                clearToDo(blockToDo, index);
                if (!isAvailable(index)) continue;

                int currentLight = getBlockLight(index);
                if (currentLight != light) {
                    if (currentLight > 0) queueBlockAddition(index);
                    continue;
                }

                spreadBlockLight(index, currentLight);
            }

            blockPendingMask &= ~(1 << light);
        }
    }

    private void spreadBlockLight(int sourceIndex, int sourceLight) {
        for (int delta : NEIGHBOR_DELTAS) {
            int neighborIndex = sourceIndex + delta;
            if (!isAvailable(neighborIndex)) continue;

            int candidate = sourceLight - attenuation(getFilter(neighborIndex));
            int emission = getEmission(neighborIndex);
            if (emission > candidate) {
                candidate = emission;
            }

            if (candidate <= 0) continue;

            int current = getBlockLight(neighborIndex);
            if (candidate <= current) continue;

            setBlockLight(neighborIndex, candidate);
            queueBlockAddition(neighborIndex);
        }
    }

    private boolean isAvailable(int index) {
        if (!SubChunkLightIndex.isInterior(index)) return false;

        Slot slot = getSlot(index);
        return slot != null && slot.available;
    }

    private int getFilter(int index) {
        Slot slot = getSlot(index);
        if (slot == null || !slot.available) return 15;

        byte[] properties = slot.properties;
        if (properties == null) {
            properties = slot.getProperties();
        }

        int value = properties[SubChunkLightIndex.cellIndex(index)] & 0xff;
        return value >>> 4;
    }

    private int getEmission(int index) {
        Slot slot = getSlot(index);
        if (slot == null || !slot.available) return 0;

        byte[] properties = slot.properties;
        if (properties == null) {
            properties = slot.getProperties();
        }

        return properties[SubChunkLightIndex.cellIndex(index)] & 0x0f;
    }

    private int getBlockLight(int index) {
        Slot slot = getSlot(index);
        if (slot == null || !slot.available) return 0;

        slot.ensureBlockLoaded();
        return slot.blockLight[SubChunkLightIndex.cellIndex(index)] & 0xff;
    }

    private void setBlockLight(int index, int light) {
        Slot slot = getSlot(index);
        if (slot == null || !slot.available) return;

        slot.ensureBlockLoaded();
        int cellIndex = SubChunkLightIndex.cellIndex(index);
        if ((slot.blockLight[cellIndex] & 0xff) == light) return;

        slot.blockLight[cellIndex] = (byte) light;
        slot.dirtyBlock = true;
    }

    private Slot getSlot(int index) {
        if (index < 0 || index >= WORK_ITEM_COUNT) return null;
        return slots[slotIndex(index)];
    }

    private static int slotIndex(int subChunkX, int subChunkY, int subChunkZ) {
        return subChunkX << 4 | subChunkZ << 2 | subChunkY;
    }

    private static int slotIndex(int index) {
        return (index >>> 12 & 0x30) | (index >>> 8 & 0x0c) | (index >>> 4 & 0x03);
    }

    private void commitTouchedSections() {
        for (Slot slot : slots) {
            if (slot != null) slot.commit();
        }
    }

    private static int attenuation(int filter) {
        if (filter < 1) return 1;
        return Math.min(filter, MAX_LIGHT);
    }

    private static boolean markToDo(long[] toDo, int index) {
        int workIndex = SubChunkLightIndex.workIndex(index);
        int word = workIndex >>> 6;
        long mask = 1L << (workIndex & 63);
        if ((toDo[word] & mask) != 0L) return false;

        toDo[word] |= mask;
        return true;
    }

    private static void clearToDo(long[] toDo, int index) {
        int workIndex = SubChunkLightIndex.workIndex(index);
        int word = workIndex >>> 6;
        long mask = 1L << (workIndex & 63);
        toDo[word] &= ~mask;
    }

    private static long[] createOuterEdgeToDo() {
        long[] result = new long[(WORK_ITEM_COUNT + 63) >>> 6];
        for (int globalX = 0; globalX < 48; globalX++) {
            for (int globalZ = 0; globalZ < 64; globalZ++) {
                for (int globalY = 0; globalY < 64; globalY++) {
                    if (globalX != 0
                            && globalX != 47
                            && globalZ != 0
                            && globalZ < 47
                            && globalY != 0
                            && globalY < 47) {
                        continue;
                    }

                    int index = globalX << 12 | globalZ << 6 | globalY;
                    result[index >>> 6] |= 1L << (index & 63);
                }
            }
        }

        return result;
    }

    private static IntWorkQueue[] createQueues() {
        IntWorkQueue[] queues = new IntWorkQueue[MAX_LIGHT + 1];
        for (int light = 0; light <= MAX_LIGHT; light++) {
            queues[light] = new IntWorkQueue(128);
        }

        return queues;
    }

    private final class Slot {
        private IChunk chunk;
        private ChunkSection section;
        private int sectionY;
        private boolean available;
        private byte[] properties;
        private byte[] skyLight;
        private byte[] blockLight;
        private byte skyMode = SKY_UNLOADED;
        private boolean blockLoaded;
        private boolean dirtySky;
        private boolean dirtyBlock;
        private void bind(IChunk chunk, int sectionY, ChunkSection section) {
            this.chunk = chunk;
            this.sectionY = sectionY;
            this.section = section;
            this.available = chunk != null;
            this.properties = section == null ? ZERO_PROPERTIES : section.lighting().getCombinedLightPropertiesCache();
            this.skyMode = SKY_UNLOADED;
            this.blockLoaded = false;
            this.dirtySky = false;
            this.dirtyBlock = false;
        }

        private byte[] getProperties() {
            byte[] result = properties;
            if (result != null) return result;

            result = section.getCombinedLightingProperties();
            properties = result;
            return result;
        }

        private void loadSkyIfNeeded() {
            if (skyMode != SKY_UNLOADED) return;

            if (section != null) {
                if (section.lighting().hasMaxSkyLight()) {
                    skyMode = SKY_MAX;
                    return;
                }

                if (!section.lighting().hasSkyLightStorage()) {
                    skyMode = SKY_ZERO;
                    return;
                }

                byte[] storage = getOrCreateSkyArray();
                section.lighting().copySkyLightTo(storage);
                skyMode = SKY_EXPLICIT;
                return;
            }

            loadImplicitSkyLight();
        }

        private void loadImplicitSkyLight() {
            if (chunk == null || !chunk.isOverWorld()) {
                skyMode = SKY_ZERO;
                return;
            }

            short[] heightMap = chunk.getHeightMapArray();
            int sectionMinY = sectionY << 4;
            int sectionMaxY = sectionMinY + 15;
            boolean allMax = true;
            boolean allZero = true;
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localX = 0; localX < 16; localX++) {
                    int height = heightMap[localZ << 4 | localX] + minHeight;
                    boolean columnAllMax = sectionMinY >= height;
                    boolean columnAllZero = sectionMaxY < height;
                    if (!columnAllMax) allMax = false;
                    if (!columnAllZero) allZero = false;
                    if (!allMax && !allZero) break;
                }

                if (!allMax && !allZero) break;
            }

            if (allMax) {
                skyMode = SKY_MAX;
                return;
            }

            if (allZero) {
                skyMode = SKY_ZERO;
                return;
            }

            byte[] storage = getOrCreateSkyArray();
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localX = 0; localX < 16; localX++) {
                    int height = heightMap[localZ << 4 | localX] + minHeight;
                    for (int localY = 0; localY < 16; localY++) {
                        int worldY = sectionMinY + localY;
                        storage[IChunk.index(localX, localY, localZ)] = (byte) (worldY >= height ? 15 : 0);
                    }
                }
            }

            skyMode = SKY_EXPLICIT;
        }

        private int getSkyLight(int cellIndex) {
            if (skyMode == SKY_UNLOADED) {
                loadSkyIfNeeded();
            }

            if (skyMode == SKY_MAX) return 15;
            if (skyMode == SKY_ZERO) return 0;

            return skyLight[cellIndex] & 0xff;
        }

        private void setQueuedSkyLight(int cellIndex, int light) {
            if (skyMode != SKY_EXPLICIT) {
                byte[] storage = getOrCreateSkyArray();
                Arrays.fill(storage, (byte) 0);
                skyMode = SKY_EXPLICIT;
            }

            skyLight[cellIndex] = (byte) light;
            dirtySky = true;
        }

        private byte[] getOrCreateSkyArray() {
            if (skyLight == null) {
                skyLight = new byte[ChunkSection.SIZE];
            }

            return skyLight;
        }

        private void ensureBlockLoaded() {
            if (blockLoaded) return;

            if (blockLight == null) {
                blockLight = new byte[ChunkSection.SIZE];
            }

            if (section != null) {
                section.lighting().copyBlockLightTo(blockLight);
            } else {
                Arrays.fill(blockLight, (byte) 0);
            }

            blockLoaded = true;
        }

        private void clearBlockLight() {
            if (blockLight == null) {
                blockLight = new byte[ChunkSection.SIZE];
            } else {
                Arrays.fill(blockLight, (byte) 0);
            }

            blockLoaded = true;
            dirtyBlock = true;
        }

        private void commit() {
            if (!dirtySky && !dirtyBlock) return;

            ChunkSection target = section;
            if (target == null && chunk instanceof Chunk concreteChunk) {
                target = concreteChunk.getOrCreateSectionForLighting(sectionY);
                section = target;
            }

            if (target == null) return;

            if (dirtyBlock) {
                target.lighting().replaceBlockLightFrom(blockLight);
            }

            if (dirtySky) {
                if (skyMode == SKY_MAX) {
                    target.lighting().setAllSkyLight((byte) 15);
                } else if (skyMode == SKY_ZERO) {
                    target.lighting().setAllSkyLight((byte) 0);
                } else {
                    target.lighting().replaceSkyLightFrom(skyLight);
                }
            }

            dirtyBlock = false;
            dirtySky = false;
        }
    }

    private static final class IntWorkQueue {
        private int[] elements;
        private int readIndex;
        private int writeIndex;
        private IntWorkQueue(int initialCapacity) {
            elements = new int[Math.max(1, initialCapacity)];
        }

        private void enqueue(int value) {
            if (readIndex == writeIndex) {
                readIndex = 0;
                writeIndex = 0;
            }

            if (writeIndex >= elements.length) {
                compactOrGrow();
            }

            elements[writeIndex++] = value;
        }

        private int dequeue() {
            return elements[readIndex++];
        }

        private boolean isEmpty() {
            return readIndex >= writeIndex;
        }

        private void clear() {
            readIndex = 0;
            writeIndex = 0;
        }

        private void compactOrGrow() {
            int size = writeIndex - readIndex;
            if (readIndex > 0) {
                System.arraycopy(elements, readIndex, elements, 0, size);
                readIndex = 0;
                writeIndex = size;
                return;
            }

            elements = Arrays.copyOf(elements, elements.length << 1);
        }
    }
}
