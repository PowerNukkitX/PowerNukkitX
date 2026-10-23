package org.powernukkitx.level.structure;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.IntArrayTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.LongTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.BlockUpdateEntry;
import org.powernukkitx.utils.Identifier;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.StructureRotationUtil;

import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_EAST;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_NORTH;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_SOUTH;
import static org.powernukkitx.block.property.CommonBlockProperties.CONNECTION_WEST;

import org.cloudburstmc.protocol.bedrock.data.payload.structure.Mirror;
import org.cloudburstmc.protocol.bedrock.data.payload.structure.Rotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Bedrock structure capture, serialization, transformation, and placement.
 *
 * @author harry-xi | daoge_cmd
 * @author Curse
 */

@Getter
@ToString
@Slf4j
public class Structure extends AbstractStructure {
    private static final int FORMAT_VERSION = 2;

    private final BlockState[][][][] blockStates;
    private final Map<Vector3, CompoundTag> blockEntities;
    private final List<CompoundTag> entities;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final int x;
    private final int y;
    private final int z;
    @Getter(lombok.AccessLevel.NONE)
    @ToString.Exclude
    private final CompoundTag originalNbt;
    @Getter(lombok.AccessLevel.NONE)
    private final Integer neighborAwareBlockUpgradeVersion;

    public Structure(BlockState[][][][] blockStates,
                     Map<Vector3, CompoundTag> blockEntities,
                     List<CompoundTag> entities,
                     int sizeX, int sizeY, int sizeZ,
                     int x, int y, int z) {
        this(blockStates, blockEntities, entities, sizeX, sizeY, sizeZ, x, y, z, null, null);
    }

    private Structure(BlockState[][][][] blockStates,
                      Map<Vector3, CompoundTag> blockEntities,
                      List<CompoundTag> entities,
                      int sizeX, int sizeY, int sizeZ,
                      int x, int y, int z,
                      CompoundTag originalNbt, Integer neighborAwareBlockUpgradeVersion) {
        this.blockStates = blockStates;
        this.blockEntities = blockEntities;
        this.entities = entities;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.x = x;
        this.y = y;
        this.z = z;
        this.originalNbt = originalNbt == null ? null : originalNbt.copy();
        this.neighborAwareBlockUpgradeVersion = neighborAwareBlockUpgradeVersion;
    }

    /**
     * @see #create(Level, int, int, int, int, int, int, boolean)
     */
    public static Structure create(Level dimension, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
        return create(dimension, x, y, z, sizeX, sizeY, sizeZ, true);
    }

    /**
     * Pick a structure from the dimension.
     *
     * @param dimension    the dimension to pick the structure from
     * @param x            the x coordinate of the structure
     * @param y            the y coordinate of the structure
     * @param z            the z coordinate of the structure
     * @param sizeX        the size of the structure in x direction
     * @param sizeY        the size of the structure in y direction
     * @param sizeZ        the size of the structure in z direction
     * @param saveEntities whether to save the entities in the structure
     * @return the picked structure
     */
    public static Structure create(Level dimension, int x, int y, int z, int sizeX, int sizeY, int sizeZ, boolean saveEntities) {
        return create(dimension, x, y, z, sizeX, sizeY, sizeZ, saveEntities, true);
    }

    /**
     * Captures a structure with independent entity and block inclusion options.
     *
     * @param saveEntities whether to capture actors
     * @param includeBlocks whether to capture block states, block entities and pending ticks
     * @return captured structure
     */
    public static Structure create(Level dimension, int x, int y, int z, int sizeX, int sizeY, int sizeZ,
                                   boolean saveEntities, boolean includeBlocks) {
        BlockState[][][][] blockStates = new BlockState[2][sizeX][sizeY][sizeZ];
        Map<Vector3, CompoundTag> blockEntities = new HashMap<>();
        List<CompoundTag> entities = new ArrayList<>();

        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
                    if (!includeBlocks) continue;
                    blockStates[0][lx][ly][lz] = dimension.getBlockStateAt(x + lx, y + ly, z + lz, 0);
                    blockStates[1][lx][ly][lz] = dimension.getBlockStateAt(x + lx, y + ly, z + lz, 1);
                    final BlockEntity blockEntity = dimension.getBlockEntity(new Vector3(x + lx, y + ly, z + lz));
                    if (blockEntity != null) {
                        blockEntity.saveNBT();
                        final CompoundTag blockEntityData = blockEntity.getNbt().copy();
                        blockEntityData.putString("id", blockEntity.getSaveId());
                        final CompoundTag blockPositionData = new CompoundTag();
                        blockPositionData.putCompound("block_entity_data", blockEntityData);
                        blockEntities.put(new Vector3(lx, ly, lz), blockPositionData);
                    }
                }
            }
        }

        if (includeBlocks) capturePendingTicks(dimension, x, y, z, sizeX, sizeY, sizeZ, blockEntities);

        if (saveEntities) {
            Arrays.stream(dimension.getEntities()).forEach(entity -> {
                if (entity.getIdentifier().equals(EntityID.PLAYER)) {
                    // Skip player entity
                    return;
                }
                Location location = entity.getLocation();
                if (x <= location.x && x + sizeX > location.x &&
                        y <= location.y && y + sizeY > location.y &&
                        z <= location.z && z + sizeZ > location.z) {
                    entity.saveNBT();
                    CompoundTag actorNbt = entity.getNbt().copy();
                    if (actorNbt.containsCompound("internalComponents")) {
                        actorNbt.getCompound("internalComponents").remove("EntityStorageKeyComponent");
                    }
                    entities.add(actorNbt);
                }
            });
        }

        return new Structure(blockStates, blockEntities, entities, sizeX, sizeY, sizeZ, x, y, z);
    }

    /**
     * Captures a structure over multiple level ticks without reading the live world on worker threads.
     * The source area should remain unchanged until the future completes.
     *
     * @return future containing the captured structure
     */
    public static CompletableFuture<Structure> createGradually(Level dimension, int x, int y, int z,
            int sizeX, int sizeY, int sizeZ, boolean saveEntities, int positionsPerTick) {
        Preconditions.checkNotNull(dimension, "dimension cannot be null");
        Preconditions.checkArgument(sizeX > 0 && sizeY > 0 && sizeZ > 0, "invalid structure dimensions");
        Preconditions.checkArgument(positionsPerTick > 0, "positionsPerTick must be positive");

        int plane = Math.multiplyExact(sizeY, sizeZ);
        int total = Math.multiplyExact(sizeX, plane);
        BlockState[][][][] states = new BlockState[2][sizeX][sizeY][sizeZ];
        Map<Vector3, CompoundTag> tiles = new HashMap<>();
        List<CompoundTag> actors = new ArrayList<>();
        CompletableFuture<Structure> result = new CompletableFuture<>();

        class CaptureStep implements Runnable {
            private int cursor;

            @Override
            public void run() {
                if (result.isDone()) return;
                try {
                    int end = (int) Math.min((long) total, (long) cursor + positionsPerTick);
                    for (int index = cursor; index < end; index++) {
                        int lx = index / plane;
                        int ly = index % plane / sizeZ;
                        int lz = index % sizeZ;
                        int wx = x + lx, wy = y + ly, wz = z + lz;

                        states[0][lx][ly][lz] = dimension.getBlockStateAt(wx, wy, wz, 0);
                        states[1][lx][ly][lz] = dimension.getBlockStateAt(wx, wy, wz, 1);

                        BlockEntity tile = dimension.getBlockEntity(new Vector3(wx, wy, wz));
                        if (tile == null) continue;

                        tile.saveNBT();
                        CompoundTag data = tile.getNbt().copy();
                        data.putString("id", tile.getSaveId());
                        tiles.put(new Vector3(lx, ly, lz),
                                new CompoundTag().putCompound("block_entity_data", data));
                    }

                    cursor = end;
                    if (cursor < total) {
                        dimension.getScheduler().scheduleDelayedTask(this, 1);
                        return;
                    }

                    capturePendingTicks(dimension, x, y, z, sizeX, sizeY, sizeZ, tiles);

                    if (saveEntities) {
                        Arrays.stream(dimension.getEntities()).forEach(entity -> {
                            if (entity.getIdentifier().equals(EntityID.PLAYER)) return;
                            Location loc = entity.getLocation();
                            if (loc.x < x || loc.x >= (double) x + sizeX
                                    || loc.y < y || loc.y >= (double) y + sizeY
                                    || loc.z < z || loc.z >= (double) z + sizeZ) return;

                            entity.saveNBT();
                            CompoundTag actor = entity.getNbt().copy();
                            if (actor.containsCompound("internalComponents")) {
                                actor.getCompound("internalComponents").remove("EntityStorageKeyComponent");
                            }
                            actors.add(actor);
                        });
                    }

                    result.complete(new Structure(states, tiles, actors, sizeX, sizeY, sizeZ, x, y, z));
                } catch (Throwable e) {
                    result.completeExceptionally(e);
                }
            }
        }

        dimension.getScheduler().scheduleTask(new CaptureStep());
        return result;
    }

    private static void capturePendingTicks(Level level, int x, int y, int z, int sizeX, int sizeY, int sizeZ,
                                            Map<Vector3, CompoundTag> positionData) {
        int maxX = Math.addExact(x, sizeX);
        int maxY = Math.addExact(y, sizeY);
        int maxZ = Math.addExact(z, sizeZ);
        long currentTick = level.getCurrentTick();

        for (int cx = x >> 4; cx <= (maxX - 1) >> 4; cx++) {
            for (int cz = z >> 4; cz <= (maxZ - 1) >> 4; cz++) {
                IChunk chunk = level.getChunk(cx, cz, false);
                if (chunk == null) continue;

                Map<BlockUpdateEntry, Long> pending = chunk.getBlockUpdateScheduler().getPendingBlockUpdatesWithTime();
                for (var update : pending.entrySet()) {
                    Vector3 pos = update.getKey().pos;
                    int px = pos.getFloorX();
                    int py = pos.getFloorY();
                    int pz = pos.getFloorZ();
                    if (px < x || px >= maxX || py < y || py >= maxY || pz < z || pz >= maxZ) continue;

                    long remaining = Math.max(0L, update.getValue() - currentTick);
                    int delay = (int) Math.min(remaining, Integer.MAX_VALUE);
                    Vector3 local = new Vector3(px - x, py - y, pz - z);
                    CompoundTag data = positionData.computeIfAbsent(local, ignored -> new CompoundTag());
                    ListTag<CompoundTag> ticks = data.containsList("tick_queue_data", Tag.TAG_Compound)
                            ? data.getList("tick_queue_data", CompoundTag.class)
                            : new ListTag<>(Tag.TAG_Compound);

                    ticks.add(new CompoundTag().putInt("tick_delay", delay));
                    data.putList("tick_queue_data", ticks);
                }
            }
        }
    }

    /**
     * Load structure data from nbt.
     *
     * @param nbt the nbt data to load
     * @return the loaded structure
     */
    public static Structure fromNbt(CompoundTag nbt) {
        Preconditions.checkNotNull(nbt, "nbt cannot be null");
        Preconditions.checkArgument(nbt.get("format_version") instanceof IntTag, "Missing format_version");
        int formatVersion = nbt.getInt("format_version");
        Preconditions.checkArgument(formatVersion == 1 || formatVersion == 2, "Unsupported format_version: %s", formatVersion);

        Preconditions.checkArgument(nbt.containsList("size", Tag.TAG_Int), "Missing size");
        ListTag<IntTag> size = nbt.getList("size", IntTag.class);
        Preconditions.checkArgument(size.size() == 3, "size must contain three integers");

        int sizeX = size.get(0).getData();
        int sizeY = size.get(1).getData();
        int sizeZ = size.get(2).getData();
        Preconditions.checkArgument(sizeX > 0 && sizeY > 0 && sizeZ > 0, "Invalid structure dimensions");

        int volume = Math.multiplyExact(Math.multiplyExact(sizeX, sizeY), sizeZ);
        Preconditions.checkArgument(nbt.containsCompound("structure"), "Missing structure");
        CompoundTag structureNBT = nbt.getCompound("structure");

        Preconditions.checkArgument(structureNBT.containsList("block_indices", Tag.TAG_Int_Array),
                "block_indices must be a list of int arrays");
        ListTag<IntArrayTag> blockIndices = structureNBT.getList("block_indices", IntArrayTag.class);
        Preconditions.checkArgument(formatVersion == 1 ? blockIndices.size() == 2 :
                blockIndices.size() == 1 || blockIndices.size() == 2, "Invalid block_indices layer count");

        int[][] layers = new int[blockIndices.size()][];
        for (int layer = 0; layer < layers.length; layer++) {
            layers[layer] = blockIndices.get(layer).getData();
            Preconditions.checkArgument(layers[layer].length == volume, "Invalid block_indices layer %s length", layer);
        }

        Preconditions.checkArgument(structureNBT.containsCompound("palette"), "Missing palette");
        CompoundTag palettes = structureNBT.getCompound("palette");
        Preconditions.checkArgument(palettes.containsCompound("default"), "Missing default palette");
        CompoundTag palette = palettes.getCompound("default");
        Preconditions.checkArgument(palette.containsList("block_palette", Tag.TAG_Compound), "Missing block_palette");

        List<BlockState> blockPalette = new ArrayList<>();
        for (CompoundTag entry : palette.getList("block_palette", CompoundTag.class).getAll()) {
            BlockState state = ItemHelper.getBlockStateHelper(entry);
            blockPalette.add(state == null ? STATE_UNKNOWN : state);
        }

        BlockState[][][][] blockStates = new BlockState[2][sizeX][sizeY][sizeZ];
        for (int layer = 0; layer < 2; layer++) {
            for (int lx = 0; lx < sizeX; lx++) {
                for (int ly = 0; ly < sizeY; ly++) {
                    for (int lz = 0; lz < sizeZ; lz++) {
                        if (layer >= layers.length) {
                            blockStates[layer][lx][ly][lz] = STATE_STRUCTURE_VOID;
                            continue;
                        }
                        int index = layers[layer][indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz)];
                        Preconditions.checkArgument(index >= -1 && index < blockPalette.size(), "Invalid palette index: %s", index);
                        blockStates[layer][lx][ly][lz] = index == -1 ? STATE_STRUCTURE_VOID : blockPalette.get(index);
                    }
                }
            }
        }

        Map<Vector3, CompoundTag> blockEntities = new HashMap<>();
        CompoundTag positionData = palette.getCompound("block_position_data");
        for (var entry : positionData.getEntrySet()) {
            int index = Integer.parseInt(entry.getKey());
            Preconditions.checkArgument(index >= 0 && index < volume, "Invalid block_position_data index: %s", index);
            Preconditions.checkArgument(entry.getValue() instanceof CompoundTag, "Invalid block_position_data entry");
            blockEntities.put(posFromIndex(sizeX, sizeY, sizeZ, index), ((CompoundTag) entry.getValue()).copy());
        }

        Preconditions.checkArgument(nbt.containsList("structure_world_origin", Tag.TAG_Int), "Missing structure_world_origin");
        ListTag<IntTag> origin = nbt.getList("structure_world_origin", IntTag.class);
        Preconditions.checkArgument(origin.size() == 3, "structure_world_origin must contain three integers");

        Preconditions.checkArgument(!nbt.contains("neighbor_aware_block_upgrade_version") ||
                nbt.get("neighbor_aware_block_upgrade_version") instanceof IntTag, "Invalid neighbor upgrade version type");
        Integer upgradeVersion = nbt.contains("neighbor_aware_block_upgrade_version")
                ? nbt.getInt("neighbor_aware_block_upgrade_version") : null;
        Preconditions.checkArgument(upgradeVersion == null || upgradeVersion >= 0 && upgradeVersion <= 4,
                "Invalid neighbor_aware_block_upgrade_version");

        return new Structure(
                blockStates, blockEntities, structureNBT.getList("entities", CompoundTag.class).getAll(),
                sizeX, sizeY, sizeZ,
                origin.get(0).getData(), origin.get(1).getData(), origin.get(2).getData(),
                nbt, upgradeVersion
        );
    }

    public static CompletableFuture<Structure> fromNbtAsync(CompoundTag nbt) {
        return CompletableFuture.supplyAsync(() -> fromNbt(nbt));
    }

    private static int indexFormPos(int sizeX, int sizeY, int sizeZ, int x, int y, int z) {
        // sizeX is kept for better looking
        return x * sizeY * sizeZ + y * sizeZ + z;
    }

    private static Vector3 posFromIndex(int sizeX, int sizeY, int sizeZ, int index) {
        // sizeX is kept for better looking
        return new Vector3(index / (sizeY * sizeZ), index % (sizeY * sizeZ) / sizeZ, index % (sizeY * sizeZ) % sizeZ);
    }

    /**
     * Place the structure in the dimension.
     *
     * @param pos the position to place the structure at (level cannot be null)
     */
    @Override
    public void preparePlace(Position pos, BlockManager blockManager) {
        Preconditions.checkArgument(pos.getLevel() != null, "position level cannot be null");

        int x = pos.getFloorX();
        int y = pos.getFloorY();
        int z = pos.getFloorZ();

        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
                    BlockState l0 = blockStates[0][lx][ly][lz];
                    BlockState l1 = blockStates[1][lx][ly][lz];
                    if (l0 != null && l0 != STATE_STRUCTURE_VOID)
                        blockManager.setBlockStateIfChanged(x + lx, y + ly, z + lz, 0, l0);
                    if (l1 != null && l1 != STATE_STRUCTURE_VOID)
                        blockManager.setBlockStateIfChanged(x + lx, y + ly, z + lz, 1, l1);
                }
            }
        }
    }

    public void place(Position pos, boolean includeEntities, BlockManager blockManager) {
        preparePlace(pos, blockManager);

        Map<BlockState, Boolean> eligibleStates = new HashMap<>();
        Level level = pos.getLevel();
        int baseX = pos.getFloorX(), baseY = pos.getFloorY(), baseZ = pos.getFloorZ();

        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
                    BlockState state = blockStates[0][lx][ly][lz];
                    if (state == null || state == STATE_STRUCTURE_VOID) continue;

                    int worldX = baseX + lx, worldY = baseY + ly, worldZ = baseZ + lz;

                    if (!writesPrimaryLayer(lx - 1, ly, lz))
                        queueExistingNeighborAfterApply(level, blockManager, worldX - 1, worldY, worldZ);
                    if (!writesPrimaryLayer(lx + 1, ly, lz))
                        queueExistingNeighborAfterApply(level, blockManager, worldX + 1, worldY, worldZ);
                    if (!writesPrimaryLayer(lx, ly, lz - 1))
                        queueExistingNeighborAfterApply(level, blockManager, worldX, worldY, worldZ - 1);
                    if (!writesPrimaryLayer(lx, ly, lz + 1))
                        queueExistingNeighborAfterApply(level, blockManager, worldX, worldY, worldZ + 1);

                    if (state == STATE_UNKNOWN) continue;
                    if (!eligibleStates.computeIfAbsent(state, StructureNeighborUpdates::isEligible)) continue;
                    blockManager.queueNeighborAfterApply(worldX, worldY, worldZ, state.blockStateHash());
                }
            }
        }

        final int deltaX = pos.getFloorX() - this.x;
        final int deltaZ = pos.getFloorZ() - this.z;

        for (var entry : blockEntities.entrySet()) {
            CompoundTag data = entry.getValue();
            Vector3 local = entry.getKey();
            if (!writesPrimaryLayer(local.getFloorX(), local.getFloorY(), local.getFloorZ())) continue;

            int worldX = pos.getFloorX() + local.getFloorX();
            int worldY = pos.getFloorY() + local.getFloorY();
            int worldZ = pos.getFloorZ() + local.getFloorZ();

            if (data.containsList("tick_queue_data", Tag.TAG_Compound)) {
                for (CompoundTag tick : data.getList("tick_queue_data", CompoundTag.class).getAll()) {
                    int delay = tick.get("tick_delay") instanceof IntTag ? tick.getInt("tick_delay") : 0;
                    blockManager.queueTickAfterApply(worldX, worldY, worldZ, delay);
                }
            }

            if (!data.containsCompound("block_entity_data")) continue;
            CompoundTag storedData = data.getCompound("block_entity_data");
            if (storedData.getString("id").isEmpty()) continue;

            CompoundTag newNbt = storedData.copy();
            newNbt.putInt("x", worldX);
            newNbt.putInt("y", worldY);
            newNbt.putInt("z", worldZ);

            if (newNbt.contains("pairx")) {
                newNbt.putInt("pairx", newNbt.getInt("pairx") + deltaX);
            }
            if (newNbt.contains("pairz")) {
                newNbt.putInt("pairz", newNbt.getInt("pairz") + deltaZ);
            }

            blockManager.queueBlockEntityAfterApply(newNbt);
        }

        blockManager.applySubChunkUpdate(true);

        if (includeEntities) placeEntities(pos);
    }

    /**
     * Places this structure with independent block and entity placement options.
     *
     * @param pos destination position
     * @param includeEntities whether to place saved actors
     * @param ignoreBlocks whether to skip block placement
     */
    public void place(Position pos, boolean includeEntities, boolean ignoreBlocks) {
        if (ignoreBlocks) {
            if (includeEntities) placeEntities(pos);
        } else {
            place(pos, includeEntities, new BlockManager(pos.getLevel()));
        }
    }

    /**
     * Creates a placement view filtered using BDS integrity selection.
     * The original structure and its cached NBT remain unchanged.
     *
     * @param integrity placement integrity percentage
     * @param seed placement integrity seed
     * @return filtered structure for placement
     */
    public Structure filteredByIntegrity(float integrity, long seed) {
        if (!(integrity < 100f)) return this;

        BlockState[][][][] selected = new BlockState[2][sizeX][sizeY][sizeZ];
        Map<Vector3, CompoundTag> selectedData = new HashMap<>();

        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
                    int index = indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz);
                    if (!passesIntegrity(integrity, (int) seed + index)) continue;

                    selected[0][lx][ly][lz] = blockStates[0][lx][ly][lz];
                    selected[1][lx][ly][lz] = blockStates[1][lx][ly][lz];

                    Vector3 local = new Vector3(lx, ly, lz);
                    CompoundTag data = blockEntities.get(local);
                    if (data != null) selectedData.put(local, data);
                }
            }
        }

        return new Structure(selected, selectedData, entities, sizeX, sizeY, sizeZ,
                x, y, z, null, neighborAwareBlockUpgradeVersion);
    }

    /**
     * Creates a placement view that retains destination water for compatible blocks.
     * The original structure remains unchanged.
     *
     * @param destination structure placement origin
     * @return waterlogging-adjusted placement view
     */
    public Structure withWaterlogging(Position destination) {
        Preconditions.checkNotNull(destination.getLevel(), "destination level cannot be null");

        Level level = destination.getLevel();
        BlockState[][][][] selected = new BlockState[2][sizeX][sizeY][sizeZ];

        for (int layer = 0; layer < 2; layer++) {
            for (int lx = 0; lx < sizeX; lx++) {
                for (int ly = 0; ly < sizeY; ly++) {
                    System.arraycopy(blockStates[layer][lx][ly], 0, selected[layer][lx][ly], 0, sizeZ);
                }
            }
        }

        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
                    BlockState primary = blockStates[0][lx][ly][lz];
                    if (primary == null || primary == STATE_STRUCTURE_VOID) continue;

                    int waterloggingLevel = primary.toBlock().getWaterloggingLevel();
                    if (waterloggingLevel == 0) continue;

                    int worldX = destination.getFloorX() + lx;
                    int worldY = destination.getFloorY() + ly;
                    int worldZ = destination.getFloorZ() + lz;

                    BlockState water = level.getBlockStateAt(worldX, worldY, worldZ, 0);
                    if (!isCompatibleWater(water, waterloggingLevel)) {
                        water = level.getBlockStateAt(worldX, worldY, worldZ, 1);
                    }
                    if (isCompatibleWater(water, waterloggingLevel)) selected[1][lx][ly][lz] = water;
                }
            }
        }

        return new Structure(selected, blockEntities, entities, sizeX, sizeY, sizeZ,
                x, y, z, null, neighborAwareBlockUpgradeVersion);
    }

    private static boolean isCompatibleWater(BlockState state, int waterloggingLevel) {
        if (state == null) return false;
        String identifier = state.getIdentifier();
        return BlockID.WATER.equals(identifier)
                || waterloggingLevel > 1 && BlockID.FLOWING_WATER.equals(identifier);
    }

    private static boolean passesIntegrity(float integrity, int seed) {
        if (!(integrity > 0f)) return false;

        int state = seed, state1 = 0;
        for (int i = 1; i <= 397; i++) {
            state = 1812433253 * (state ^ (state >>> 30)) + i;
            if (i == 1) state1 = state;
        }

        int y = (seed & 0x80000000) | (state1 & 0x7fffffff);
        int random = state ^ (y >>> 1) ^ ((y & 1) == 0 ? 0 : 0x9908b0df);
        random ^= random >>> 11;
        random ^= (random << 7) & 0x9d2c5680;
        random ^= (random << 15) & 0xefc60000;
        random ^= random >>> 18;

        float percentage = (float) ((random & 0xffffffffL) * 0x1.0p-32) * 100f;
        return integrity > percentage;
    }

    void placeEntities(Position pos) {
        Map<Long, Long> actorIds = new HashMap<>();
        for (CompoundTag source : entities) {
            CompoundTag entityNbt = source.copy();
            if (!(entityNbt.get("Pos") instanceof ListTag<?> posList)) continue;
            if (!(entityNbt.get("identifier") instanceof StringTag identifierTag)) continue;

            String identifier = identifierTag.parseValue();
            if (identifier == null || identifier.isEmpty() || !Identifier.isValid(identifier)) continue;

            float origX = actorFloat(posList, 0);
            float origY = actorFloat(posList, 1);
            float origZ = actorFloat(posList, 2);
            if (!Float.isFinite(origX) || !Float.isFinite(origY) || !Float.isFinite(origZ)) continue;

            double newX = origX - this.x + pos.getFloorX();
            double newY = origY - this.y + pos.getFloorY();
            double newZ = origZ - this.z + pos.getFloorZ();

            if (!Double.isFinite(newX) || !Double.isFinite(newY) || !Double.isFinite(newZ)) continue;

            long sourceId = entityNbt.get("UniqueID") instanceof LongTag tag ? tag.getData() : 0;
            if (sourceId != 0 && sourceId != -1) {
                entityNbt.putLong("UniqueID",
                        actorIds.computeIfAbsent(sourceId, id -> pos.getLevel().getNewUniqueID()));
            } else {
                entityNbt.remove("UniqueID");
            }

            remapActorReference(entityNbt, "LeasherID", actorIds, pos.getLevel());
            remapActorReference(entityNbt, "TargetID", actorIds, pos.getLevel());

            if (entityNbt.containsList("LinksTag", Tag.TAG_Compound)) {
                for (CompoundTag link : entityNbt.getList("LinksTag", CompoundTag.class).getAll()) {
                    remapActorReference(link, "entityID", actorIds, pos.getLevel());
                }
            }

            entityNbt.putList("Pos", new ListTag<FloatTag>()
                    .add(new FloatTag((float) newX))
                    .add(new FloatTag((float) newY))
                    .add(new FloatTag((float) newZ))
            ).putList("Motion", new ListTag<FloatTag>()
                    .add(new FloatTag(0))
                    .add(new FloatTag(0))
                    .add(new FloatTag(0)));

            IChunk chunk = new Position(newX, newY, newZ, pos.getLevel()).getChunk();
            if (chunk == null) {
                log.warn("Cannot restore structure actor {} at ({}, {}, {}): destination chunk unavailable",
                        identifier, newX, newY, newZ);
                continue;
            }

            Entity e = Entity.createEntity(identifier, chunk, entityNbt);
            if (e != null) {
                e.spawnToAll();
            }
        }
    }

    /**
     * Applies the BDS flattened animation work interval, preserving the existing placement pipeline.
     * The work index is ordered Y, X, Z (Z fastest), including skipped positions.
     *
     * @param destination placement origin
     * @param from inclusive work index
     * @param to exclusive work index
     */
    public void placeSegment(Position destination, int from, int to) {
        Preconditions.checkArgument(destination.getLevel() != null, "position level cannot be null");
        int plane = Math.multiplyExact(sizeX, sizeZ);
        int total = Math.multiplyExact(plane, sizeY);
        Preconditions.checkArgument(from >= 0 && to >= from && to <= total, "invalid structure segment");
        if (from == to || plane == 0) return;

        Level level = destination.getLevel();
        BlockManager blockManager = new BlockManager(level);
        Map<BlockState, Boolean> eligibleStates = new HashMap<>();
        int baseX = destination.getFloorX(), baseY = destination.getFloorY(), baseZ = destination.getFloorZ();
        int deltaX = baseX - x, deltaZ = baseZ - z;

        for (int index = from; index < to; index++) {
            int ly = index / plane;
            int lx = (index % plane) / sizeZ;
            int lz = index % sizeZ;
            BlockState primary = blockStates[0][lx][ly][lz];
            BlockState secondary = blockStates[1][lx][ly][lz];
            if ((primary == null || primary == STATE_STRUCTURE_VOID)
                    && (secondary == null || secondary == STATE_STRUCTURE_VOID)) continue;

            int worldX = baseX + lx, worldY = baseY + ly, worldZ = baseZ + lz;
            if (primary != null && primary != STATE_STRUCTURE_VOID)
                blockManager.setBlockStateIfChanged(worldX, worldY, worldZ, 0, primary);
            if (secondary != null && secondary != STATE_STRUCTURE_VOID)
                blockManager.setBlockStateIfChanged(worldX, worldY, worldZ, 1, secondary);
            if (primary == null || primary == STATE_STRUCTURE_VOID) continue;

            if (!writesInSegment(lx - 1, ly, lz, from, to))
                queueExistingNeighborAfterApply(level, blockManager, worldX - 1, worldY, worldZ);
            if (!writesInSegment(lx + 1, ly, lz, from, to))
                queueExistingNeighborAfterApply(level, blockManager, worldX + 1, worldY, worldZ);
            if (!writesInSegment(lx, ly, lz - 1, from, to))
                queueExistingNeighborAfterApply(level, blockManager, worldX, worldY, worldZ - 1);
            if (!writesInSegment(lx, ly, lz + 1, from, to))
                queueExistingNeighborAfterApply(level, blockManager, worldX, worldY, worldZ + 1);
            if (primary != STATE_UNKNOWN && eligibleStates.computeIfAbsent(primary, StructureNeighborUpdates::isEligible))
                blockManager.queueNeighborAfterApply(worldX, worldY, worldZ, primary.blockStateHash());

            CompoundTag data = blockEntities.get(new Vector3(lx, ly, lz));
            if (data == null) continue;
            if (data.containsList("tick_queue_data", Tag.TAG_Compound)) {
                for (CompoundTag tick : data.getList("tick_queue_data", CompoundTag.class).getAll()) {
                    int delay = tick.get("tick_delay") instanceof IntTag ? tick.getInt("tick_delay") : 0;
                    blockManager.queueTickAfterApply(worldX, worldY, worldZ, delay);
                }
            }
            if (!data.containsCompound("block_entity_data")) continue;
            CompoundTag saved = data.getCompound("block_entity_data");
            if (saved.getString("id").isEmpty()) continue;

            CompoundTag nbt = saved.copy().putInt("x", worldX).putInt("y", worldY).putInt("z", worldZ);
            if (nbt.contains("pairx")) nbt.putInt("pairx", nbt.getInt("pairx") + deltaX);
            if (nbt.contains("pairz")) nbt.putInt("pairz", nbt.getInt("pairz") + deltaZ);
            blockManager.queueBlockEntityAfterApply(nbt);
        }
        blockManager.applySubChunkUpdate(true);
    }

    private boolean writesInSegment(int lx, int ly, int lz, int from, int to) {
        if (!writesPrimaryLayer(lx, ly, lz)) return false;
        int index = ly * sizeX * sizeZ + lx * sizeZ + lz;
        return index >= from && index < to;
    }

    /**
     * Places a structure over multiple level ticks, restoring actors after the block work completes.
     *
     * @param destination placement origin
     * @param includeEntities whether to restore saved actors
     * @param positionsPerTick maximum structure positions processed per tick
     * @return future completed when placement finishes
     */
    public CompletableFuture<Void> placeGradually(Position destination, boolean includeEntities, int positionsPerTick) {
        Preconditions.checkNotNull(destination, "destination cannot be null");
        Preconditions.checkNotNull(destination.getLevel(), "destination level cannot be null");
        Preconditions.checkArgument(positionsPerTick > 0, "positionsPerTick must be positive");

        int total = Math.multiplyExact(Math.multiplyExact(sizeX, sizeY), sizeZ);
        CompletableFuture<Void> result = new CompletableFuture<>();
        Level level = destination.getLevel();

        class PlacementStep implements Runnable {
            private int cursor;

            @Override
            public void run() {
                if (result.isDone()) return;
                try {
                    int end = (int) Math.min((long) total, (long) cursor + positionsPerTick);
                    placeSegment(destination, cursor, end);
                    cursor = end;

                    if (cursor < total) {
                        level.getScheduler().scheduleDelayedTask(this, 1);
                    } else {
                        if (includeEntities) placeEntities(destination);
                        result.complete(null);
                    }
                } catch (Throwable e) {
                    result.completeExceptionally(e);
                }
            }
        }

        level.getScheduler().scheduleTask(new PlacementStep());
        return result;
    }

    private boolean writesPrimaryLayer(int x, int y, int z) {
        if (x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ) return false;
        BlockState state = blockStates[0][x][y][z];
        return state != null && state != STATE_STRUCTURE_VOID;
    }

    private static void queueExistingNeighborAfterApply(Level level, BlockManager blockManager, int x, int y, int z) {
        if (!level.isYInRange(y)) return;

        IChunk chunk = level.getChunkIfLoaded(x >> 4, z >> 4);
        if (chunk == null || !chunk.isInitiated()) return;

        BlockState state = chunk.getBlockState(x & 15, y, z & 15, 0);
        if (state != null && StructureNeighborUpdates.isEligible(state)) {
            blockManager.queueNeighborAfterApply(x, y, z, state.blockStateHash());
        }
    }

    private static void remapActorReference(CompoundTag tag, String key, Map<Long, Long> actorIds, Level level) {
        if (!(tag.get(key) instanceof LongTag original)) return;

        long oldId = original.getData();
        if (oldId == -1 || oldId == 0) return;

        long newId = actorIds.computeIfAbsent(oldId, id -> level.getNewUniqueID());
        tag.putLong(key, newId);
    }

    /**
     * Save the structure data to nbt.
     *
     * @return the nbt data of the structure
     */
    public CompoundTag toNBT() {
        if (originalNbt != null) {
            return originalNbt.copy();
        }

        CompoundTag nbt = new CompoundTag();

        // Set format version
        nbt.putInt("format_version", FORMAT_VERSION);
        if (neighborAwareBlockUpgradeVersion != null) {
            nbt.putInt("neighbor_aware_block_upgrade_version", neighborAwareBlockUpgradeVersion);
        }

        // Set size
        ListTag<IntTag> sizeList = new ListTag<>();
        sizeList.add(new IntTag(this.sizeX));
        sizeList.add(new IntTag(this.sizeY));
        sizeList.add(new IntTag(this.sizeZ));
        nbt.putList("size", sizeList);

        // Create structure NBT
        CompoundTag structureNBT = new CompoundTag();

        // Create block palette from blockStates
        Map<BlockState, Integer> blockStateToIndex = new HashMap<>();
        List<BlockState> uniqueBlockStates = new ArrayList<>();
        int paletteIndex = 0;

        // Collect unique block states
        for (int layer = 0; layer < 2; layer++) {
            for (int x = 0; x < this.sizeX; x++) {
                for (int y = 0; y < this.sizeY; y++) {
                    for (int z = 0; z < this.sizeZ; z++) {
                        BlockState state = this.blockStates[layer][x][y][z];
                        if (state != null && state != STATE_STRUCTURE_VOID && !(layer == 1 && state == STATE_AIR)
                                && !blockStateToIndex.containsKey(state)) {
                            blockStateToIndex.put(state, paletteIndex++);
                            uniqueBlockStates.add(state);
                        }
                    }
                }
            }
        }

        // Store the primary block layer and the optional populated secondary layer.
        int volume = Math.multiplyExact(Math.multiplyExact(sizeX, sizeY), sizeZ);
        int[] primary = new int[volume];
        int[] secondary = new int[volume];
        Arrays.fill(primary, -1);
        Arrays.fill(secondary, -1);
        boolean hasSecondary = false;

        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
                    int index = indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz);
                    BlockState first = blockStates[0][lx][ly][lz];
                    BlockState second = blockStates[1][lx][ly][lz];

                    if (first != null && first != STATE_STRUCTURE_VOID) {
                        primary[index] = blockStateToIndex.get(first);
                    }
                    if (second != null && second != STATE_STRUCTURE_VOID && second != STATE_AIR) {
                        secondary[index] = blockStateToIndex.get(second);
                        hasSecondary = true;
                    }
                }
            }
        }

        ListTag<IntArrayTag> blockIndices = new ListTag<>(Tag.TAG_Int_Array);
        blockIndices.add(new IntArrayTag(primary));
        if (hasSecondary) {
            blockIndices.add(new IntArrayTag(secondary));
        }
        structureNBT.putList("block_indices", blockIndices);

        // Create palette
        CompoundTag paletteCompound = new CompoundTag();
        CompoundTag defaultPalette = new CompoundTag();

        // Create block_palette
        ListTag<CompoundTag> blockPaletteList = new ListTag<>();
        for (BlockState state : uniqueBlockStates) {
            CompoundTag blockStateTag = CompoundTag.fromNetwork(state.getBlockStateTag());
            blockPaletteList.add(blockStateTag);
        }
        defaultPalette.putList("block_palette", blockPaletteList);

        // Create block_position_data for block entities
        CompoundTag blockEntityNBT = new CompoundTag();
        for (Map.Entry<Vector3, CompoundTag> entry : this.blockEntities.entrySet()) {
            Vector3 pos = entry.getKey();
            int index = indexFormPos(this.sizeX, this.sizeY, this.sizeZ, (int) pos.x, (int) pos.y, (int) pos.z);
            blockEntityNBT.putCompound(String.valueOf(index), entry.getValue());
        }
        defaultPalette.putCompound("block_position_data", blockEntityNBT);

        paletteCompound.putCompound("default", defaultPalette);
        structureNBT.putCompound("palette", paletteCompound);

        // Add entities
        ListTag<CompoundTag> entitiesList = new ListTag<>();
        for (CompoundTag entity : this.entities) {
            entitiesList.add(entity);
        }
        structureNBT.putList("entities", entitiesList);

        nbt.putCompound("structure", structureNBT);

        // Set structure_world_origin to the world position the structure was captured at
        ListTag<IntTag> originList = new ListTag<>();
        originList.add(new IntTag(this.x));
        originList.add(new IntTag(this.y));
        originList.add(new IntTag(this.z));
        nbt.putList("structure_world_origin", originList);

        return nbt;
    }

    /**
     * Rotate this structure around the Y-axis by 90, 180, or 270 degrees.
     *
     * @param rotation the rotation to apply
     * @return a new rotated Structure instance
     */
    public Structure rotate(Rotation rotation) {
        if (rotation == Rotation.NONE) {
            return this;
        }

        int newSizeX = (rotation == Rotation.ROTATE_180) ? sizeX : sizeZ;
        int newSizeZ = (rotation == Rotation.ROTATE_180) ? sizeZ : sizeX;

        BlockState[][][][] rotatedStates = new BlockState[2][newSizeX][sizeY][newSizeZ];
        Map<Vector3, CompoundTag> rotatedBlockEntities = new HashMap<>();

        for (int layer = 0; layer < 2; layer++) {
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    for (int z = 0; z < sizeZ; z++) {
                        BlockState state = blockStates[layer][x][y][z];
                        if (state == null) continue;

                        int rx = x, rz = z;
                        switch (rotation) {
                            case ROTATE_90 -> {
                                rx = z;
                                rz = sizeX - 1 - x;
                            }
                            case ROTATE_180 -> {
                                rx = sizeX - 1 - x;
                                rz = sizeZ - 1 - z;
                            }
                            case ROTATE_270 -> {
                                rx = sizeZ - 1 - z;
                                rz = x;
                            }
                        }

                        rotatedStates[layer][rx][y][rz] = rotateBlockState(state, rotation);
                    }
                }
            }
        }

        // Rotate blockEntities
        for (var entry : blockEntities.entrySet()) {
            Vector3 pos = entry.getKey();
            int x = (int) pos.x;
            int y = (int) pos.y;
            int z = (int) pos.z;

            int rx = x, rz = z;
            switch (rotation) {
                case ROTATE_90 -> {
                    rx = z;
                    rz = sizeX - 1 - x;
                }
                case ROTATE_180 -> {
                    rx = sizeX - 1 - x;
                    rz = sizeZ - 1 - z;
                }
                case ROTATE_270 -> {
                    rx = sizeZ - 1 - z;
                    rz = x;
                }
            }

            rotatedBlockEntities.put(new Vector3(rx, y, rz), rotateBlockEntityData(entry.getValue(), rotation));
        }

        List<CompoundTag> rotatedEntities = transformEntities(rotation, Mirror.NONE);

        return new Structure(rotatedStates, rotatedBlockEntities, rotatedEntities,
                newSizeX, sizeY, newSizeZ, x, y, z,
                transformOriginalNbt(rotation, Mirror.NONE, rotatedEntities), neighborAwareBlockUpgradeVersion);
    }

    /**
     * Mirror this structure along the X-axis, Z-axis, or both.
     *
     * @param mirror the mirror operation to apply
     * @return a new mirrored Structure instance
     */
    public Structure mirror(Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return this;
        }

        BlockState[][][][] mirroredStates = new BlockState[2][sizeX][sizeY][sizeZ];
        Map<Vector3, CompoundTag> mirroredBlockEntities = new HashMap<>();

        for (int layer = 0; layer < 2; layer++) {
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    for (int z = 0; z < sizeZ; z++) {
                        BlockState state = blockStates[layer][x][y][z];
                        if (state == null) continue;

                        int mx = x;
                        int mz = z;

                        switch (mirror) {
                            case X -> mx = sizeX - 1 - x;
                            case Z -> mz = sizeZ - 1 - z;
                            case XZ -> {
                                mx = sizeX - 1 - x;
                                mz = sizeZ - 1 - z;
                            }
                        }

                        mirroredStates[layer][mx][y][mz] = mirrorBlockState(state, mirror);
                    }
                }
            }
        }

        // Mirror blockEntities
        for (var entry : blockEntities.entrySet()) {
            Vector3 pos = entry.getKey();
            int x = (int) pos.x;
            int y = (int) pos.y;
            int z = (int) pos.z;

            int mx = x;
            int mz = z;

            switch (mirror) {
                case X -> mx = sizeX - 1 - x;
                case Z -> mz = sizeZ - 1 - z;
                case XZ -> {
                    mx = sizeX - 1 - x;
                    mz = sizeZ - 1 - z;
                }
            }

            mirroredBlockEntities.put(new Vector3(mx, y, mz), mirrorBlockEntityData(entry.getValue(), mirror));
        }

        List<CompoundTag> mirroredEntities = transformEntities(Rotation.NONE, mirror);

        return new Structure(mirroredStates, mirroredBlockEntities, mirroredEntities,
                sizeX, sizeY, sizeZ, x, y, z,
                transformOriginalNbt(Rotation.NONE, mirror, mirroredEntities), neighborAwareBlockUpgradeVersion);
    }

    private List<CompoundTag> transformEntities(Rotation rotation, Mirror mirror) {
        List<CompoundTag> transformed = new ArrayList<>(entities.size());

        for (CompoundTag source : entities) {
            CompoundTag actor = source.copy();

            if (actor.get("Pos") instanceof ListTag<?> position) {
                float sourceX = actorFloat(position, 0);
                float sourceY = actorFloat(position, 1);
                float sourceZ = actorFloat(position, 2);

                if (Float.isFinite(sourceX) && Float.isFinite(sourceY) && Float.isFinite(sourceZ)) {
                    float localX = sourceX - x;
                    float localZ = sourceZ - z;

                    if (mirror == Mirror.X || mirror == Mirror.XZ) localX = sizeX - localX;
                    if (mirror == Mirror.Z || mirror == Mirror.XZ) localZ = sizeZ - localZ;

                    float newX = localX;
                    float newZ = localZ;

                    switch (rotation) {
                        case ROTATE_90 -> {
                            newX = localZ;
                            newZ = sizeX - localX;
                        }
                        case ROTATE_180 -> {
                            newX = sizeX - localX;
                            newZ = sizeZ - localZ;
                        }
                        case ROTATE_270 -> {
                            newX = sizeZ - localZ;
                            newZ = localX;
                        }
                        default -> {}
                    }

                    actor.putList("Pos", new ListTag<FloatTag>()
                            .add(new FloatTag(x + newX))
                            .add(new FloatTag(sourceY))
                            .add(new FloatTag(z + newZ)));
                }
            }

            if (actor.get("Rotation") instanceof ListTag<?> angles) {
                float yaw = actorFloat(angles, 0);
                float pitch = actorFloat(angles, 1);

                    switch (mirror) {
                        case X -> {
                            yaw = 180f - yaw;
                            pitch = -pitch;
                        }
                        case Z -> {
                            yaw = -yaw;
                            pitch = -pitch;
                        }
                        case XZ -> yaw += 180f;
                        default -> {}
                    }

                    yaw += switch (rotation) {
                        case ROTATE_90 -> 90f;
                        case ROTATE_180 -> 180f;
                        case ROTATE_270 -> 270f;
                        default -> 0f;
                    };

                actor.putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(yaw % 360f))
                        .add(new FloatTag(pitch % 360f)));
            }

            transformed.add(actor);
        }

        return transformed;
    }

    private static float actorFloat(ListTag<?> values, int index) {
        return index < values.size() && values.get(index) instanceof FloatTag tag ? tag.getData() : 0f;
    }

    private CompoundTag transformOriginalNbt(Rotation rotation, Mirror mirror, List<CompoundTag> transformedEntities) {
        if (originalNbt == null) return null;

        int newSizeX = rotation == Rotation.NONE || rotation == Rotation.ROTATE_180 ? sizeX : sizeZ;
        int newSizeZ = rotation == Rotation.NONE || rotation == Rotation.ROTATE_180 ? sizeZ : sizeX;

        CompoundTag result = originalNbt.copy();
        result.putList("size", new ListTag<IntTag>()
                .add(new IntTag(newSizeX))
                .add(new IntTag(sizeY))
                .add(new IntTag(newSizeZ)));

        CompoundTag structure = result.getCompound("structure");
        ListTag<IntArrayTag> sourceLayers = structure.getList("block_indices", IntArrayTag.class);
        ListTag<IntArrayTag> transformedLayers = new ListTag<>(Tag.TAG_Int_Array);

        for (IntArrayTag sourceLayer : sourceLayers.getAll()) {
            int[] sourceIndices = sourceLayer.getData();
            int[] indices = new int[sourceIndices.length];

            for (int lx = 0; lx < sizeX; lx++) {
                for (int ly = 0; ly < sizeY; ly++) {
                    for (int lz = 0; lz < sizeZ; lz++) {
                        int tx = transformLocalX(lx, lz, rotation, mirror);
                        int tz = transformLocalZ(lx, lz, rotation, mirror);
                        int sourceIndex = indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz);
                        int targetIndex = indexFormPos(newSizeX, sizeY, newSizeZ, tx, ly, tz);
                        indices[targetIndex] = sourceIndices[sourceIndex];
                    }
                }
            }

            transformedLayers.add(new IntArrayTag(indices));
        }
        structure.putList("block_indices", transformedLayers);

        CompoundTag palettes = structure.getCompound("palette");
        for (var paletteEntry : palettes.getEntrySet()) {
            if (!(paletteEntry.getValue() instanceof CompoundTag palette)) continue;

            if (palette.containsList("block_palette", Tag.TAG_Compound)) {
                ListTag<CompoundTag> transformedPalette = new ListTag<>(Tag.TAG_Compound);

                for (CompoundTag source : palette.getList("block_palette", CompoundTag.class).getAll()) {
                    CompoundTag entry = source.copy();
                    BlockState state = ItemHelper.getBlockStateHelper(source);

                    if (state != null && hasTransformableState(state) && entry.containsCompound("states")) {
                        BlockState changed = rotation != Rotation.NONE
                                ? rotateBlockState(state, rotation)
                                : mirrorBlockState(state, mirror);

                        if (changed != null && changed != state) {
                            CompoundTag properties = entry.getCompound("states");
                            CompoundTag updated = CompoundTag.fromNetwork(changed.getBlockStateTag()).getCompound("states");
                            for (var property : updated.getEntrySet()) {
                                if (properties.contains(property.getKey())) {
                                    properties.put(property.getKey(), property.getValue().copy());
                                }
                            }
                        }
                    }

                    transformedPalette.add(entry);
                }

                palette.putList("block_palette", transformedPalette);
            }

            if (palette.containsCompound("block_position_data")) {
                CompoundTag sourcePositions = palette.getCompound("block_position_data");
                CompoundTag transformedPositions = new CompoundTag();

                for (var positionEntry : sourcePositions.getEntrySet()) {
                    if (!(positionEntry.getValue() instanceof CompoundTag data)) continue;

                    int index = Integer.parseInt(positionEntry.getKey());
                    Vector3 local = posFromIndex(sizeX, sizeY, sizeZ, index);
                    int tx = transformLocalX(local.getFloorX(), local.getFloorZ(), rotation, mirror);
                    int tz = transformLocalZ(local.getFloorX(), local.getFloorZ(), rotation, mirror);
                    int targetIndex = indexFormPos(newSizeX, sizeY, newSizeZ, tx, local.getFloorY(), tz);

                    CompoundTag transformedData = rotation != Rotation.NONE
                            ? rotateBlockEntityData(data, rotation)
                            : mirrorBlockEntityData(data, mirror);

                    transformedPositions.putCompound(String.valueOf(targetIndex), transformedData);
                }

                palette.putCompound("block_position_data", transformedPositions);
            }
        }

        ListTag<CompoundTag> serializedEntities = new ListTag<>(Tag.TAG_Compound);
        for (CompoundTag entity : transformedEntities) {
            serializedEntities.add(entity.copy());
        }
        structure.putList("entities", serializedEntities);

        return result;
    }

    private int transformLocalX(int localX, int localZ, Rotation rotation, Mirror mirror) {
        if (rotation != Rotation.NONE) return rotateX(sizeX, sizeZ, localX, localZ, rotation);
        return mirror == Mirror.X || mirror == Mirror.XZ ? sizeX - 1 - localX : localX;
    }

    private int transformLocalZ(int localX, int localZ, Rotation rotation, Mirror mirror) {
        if (rotation != Rotation.NONE) return rotateZ(sizeX, sizeZ, localX, localZ, rotation);
        return mirror == Mirror.Z || mirror == Mirror.XZ ? sizeZ - 1 - localZ : localZ;
    }

    private static BlockState rotateBlockState(BlockState state, Rotation rotation) {
        if (state == null || !hasTransformableState(state)) return state;
        BlockState result = switch (rotation) {
            case ROTATE_90 -> StructureRotationUtil.counterclockwise90(state);
            case ROTATE_180 -> StructureRotationUtil.clockwise180(state);
            case ROTATE_270 -> StructureRotationUtil.clockwise90(state);
            default -> state;
        };
        return transformConnections(state, result, rotation, Mirror.NONE);
    }

    private static BlockState mirrorBlockState(BlockState state, Mirror mirror) {
        if (state == null || !hasTransformableState(state)) return state;
        BlockState result = switch (mirror) {
            case X -> StructureRotationUtil.mirrorX(state);
            case Z -> StructureRotationUtil.mirrorZ(state);
            case XZ -> StructureRotationUtil.clockwise180(state);
            default -> state;
        };
        return transformConnections(state, result, Rotation.NONE, mirror);
    }

    private static BlockState transformConnections(BlockState source, BlockState result, Rotation rotation, Mirror mirror) {
        Boolean n = source.getPropertyValue(CONNECTION_NORTH);
        Boolean e = source.getPropertyValue(CONNECTION_EAST);
        Boolean s = source.getPropertyValue(CONNECTION_SOUTH);
        Boolean w = source.getPropertyValue(CONNECTION_WEST);
        if (n == null || e == null || s == null || w == null) return result;

        boolean north = n, east = e, south = s, west = w;

        switch (rotation) {
            case ROTATE_90 -> {
                north = e;
                east = s;
                south = w;
                west = n;
            }
            case ROTATE_180 -> {
                north = s;
                east = w;
                south = n;
                west = e;
            }
            case ROTATE_270 -> {
                north = w;
                east = n;
                south = e;
                west = s;
            }
            default -> {
                switch (mirror) {
                    case X -> {
                        east = w;
                        west = e;
                    }
                    case Z -> {
                        north = s;
                        south = n;
                    }
                    case XZ -> {
                        north = s;
                        east = w;
                        south = n;
                        west = e;
                    }
                    default -> {}
                }
            }
        }

        return result.setPropertyValues(result.toBlock().getProperties(),
                CONNECTION_NORTH.createValue(north),
                CONNECTION_EAST.createValue(east),
                CONNECTION_SOUTH.createValue(south),
                CONNECTION_WEST.createValue(west));
    }

    private static boolean hasTransformableState(BlockState state) {
        return state != STATE_AIR
                && state != STATE_UNKNOWN
                && state != STATE_STRUCTURE_VOID;
    }

    private CompoundTag rotateBlockEntityData(CompoundTag blockPositionData, Rotation rotation) {
        final CompoundTag copy = blockPositionData.copy();
        final CompoundTag data = copy.getCompound("block_entity_data");
        if (data.contains("x") && data.contains("z")) {
            rotateCoordPair(data, "x", "z", rotation);
        }
        if (data.contains("pairx") && data.contains("pairz")) {
            rotateCoordPair(data, "pairx", "pairz", rotation);
        }
        transformStructureBlockOffset(data, rotation, Mirror.NONE);
        return copy;
    }

    private void rotateCoordPair(CompoundTag data, String xKey, String zKey, Rotation rotation) {
        final int relX = data.getInt(xKey) - this.x;
        final int relZ = data.getInt(zKey) - this.z;
        int rx = relX;
        int rz = relZ;
        switch (rotation) {
            case ROTATE_90 -> {
                rx = relZ;
                rz = sizeX - 1 - relX;
            }
            case ROTATE_180 -> {
                rx = sizeX - 1 - relX;
                rz = sizeZ - 1 - relZ;
            }
            case ROTATE_270 -> {
                rx = sizeZ - 1 - relZ;
                rz = relX;
            }
            default -> {
            }
        }
        data.putInt(xKey, this.x + rx);
        data.putInt(zKey, this.z + rz);
    }

    private CompoundTag mirrorBlockEntityData(CompoundTag blockPositionData, Mirror mirror) {
        final CompoundTag copy = blockPositionData.copy();
        final CompoundTag data = copy.getCompound("block_entity_data");
        if (data.contains("x") && data.contains("z")) {
            mirrorCoordPair(data, "x", "z", mirror);
        }
        if (data.contains("pairx") && data.contains("pairz")) {
            mirrorCoordPair(data, "pairx", "pairz", mirror);
        }
        transformStructureBlockOffset(data, Rotation.NONE, mirror);
        return copy;
    }

    private void transformStructureBlockOffset(CompoundTag data, Rotation rotation, Mirror mirror) {
        if (!BlockEntity.STRUCTURE_BLOCK.equals(data.getString("id"))
                || !data.contains("xStructureOffset") || !data.contains("zStructureOffset")
                || !data.contains("xStructureSize") || !data.contains("zStructureSize")) return;

        int ox = data.getInt("xStructureOffset");
        int oz = data.getInt("zStructureOffset");
        int sx = data.getInt("xStructureSize");
        int sz = data.getInt("zStructureSize");

        switch (rotation) {
            case ROTATE_90 -> {
                int oldX = ox;
                ox = oz;
                oz = 1 - oldX - sx;
                int oldSizeX = sx;
                sx = sz;
                sz = oldSizeX;
            }
            case ROTATE_180 -> {
                ox = 1 - ox - sx;
                oz = 1 - oz - sz;
            }
            case ROTATE_270 -> {
                int oldX = ox;
                ox = 1 - oz - sz;
                oz = oldX;
                int oldSizeX = sx;
                sx = sz;
                sz = oldSizeX;
            }
            default -> {}
        }

        if (mirror == Mirror.X || mirror == Mirror.XZ) ox = 1 - ox - sx;
        if (mirror == Mirror.Z || mirror == Mirror.XZ) oz = 1 - oz - sz;

        data.putInt("xStructureOffset", ox);
        data.putInt("zStructureOffset", oz);
        data.putInt("xStructureSize", sx);
        data.putInt("zStructureSize", sz);
    }

    private void mirrorCoordPair(CompoundTag data, String xKey, String zKey, Mirror mirror) {
        final int relX = data.getInt(xKey) - this.x;
        final int relZ = data.getInt(zKey) - this.z;
        int mx = relX;
        int mz = relZ;
        switch (mirror) {
            case X -> mx = sizeX - 1 - relX;
            case Z -> mz = sizeZ - 1 - relZ;
            case XZ -> {
                mx = sizeX - 1 - relX;
                mz = sizeZ - 1 - relZ;
            }
            default -> {
            }
        }
        data.putInt(xKey, this.x + mx);
        data.putInt(zKey, this.z + mz);
    }
}
