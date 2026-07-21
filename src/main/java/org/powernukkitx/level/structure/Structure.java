package org.powernukkitx.level.structure;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.payload.structure.Mirror;
import org.cloudburstmc.protocol.bedrock.data.payload.structure.Rotation;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockStructureVoid;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.StructureRotationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author harry-xi | daoge_cmd
 * @source <a href="https://github.com/AllayMC/Allay/blob/e86e4c950a360aa725fca913452d01d3037cea5a/api/src/main/java/org/allaymc/api/utils/Structure.java">AllayMC</a>
 */

@Slf4j
@Getter
@ToString
public class Structure extends AbstractStructure {
    private static final int FORMAT_VERSION = 1;
    public static final BlockState STRUCTURE_VOID_DEFAULT_STATE = BlockStructureVoid.PROPERTIES.getDefaultState();

    private final BlockState[][][][] blockStates;
    private final Map<Vector3, CompoundTag> blockEntities;
    private final List<CompoundTag> entities;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final int x;
    private final int y;
    private final int z;

    public Structure(BlockState[][][][] blockStates,
                     Map<Vector3, CompoundTag> blockEntities,
                     List<CompoundTag> entities,
                     int sizeX, int sizeY, int sizeZ,
                     int x, int y, int z) {
        this.blockStates = blockStates;
        this.blockEntities = blockEntities;
        this.entities = entities;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.x = x;
        this.y = y;
        this.z = z;
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
        BlockState[][][][] blockStates = new BlockState[2][sizeX][sizeY][sizeZ];
        Map<Vector3, CompoundTag> blockEntities = new HashMap<>();
        List<CompoundTag> entities = new ArrayList<>();

        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
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
                    // Position data for entity is also saved, see the comment above
                    entity.saveNBT();
                    entities.add(entity.getNbt());
                }
            });
        }

        return new Structure(blockStates, blockEntities, entities, sizeX, sizeY, sizeZ, x, y, z);
    }

    /**
     * Load structure data from nbt.
     *
     * @param nbt the nbt data to load
     * @return the loaded structure
     */
    public static Structure fromNbt(CompoundTag nbt) {
        Preconditions.checkNotNull(nbt, "nbt cannot be null");
        Preconditions.checkArgument(nbt.getInt("format_version") == FORMAT_VERSION, "format_version should be " + FORMAT_VERSION);
        Preconditions.checkArgument(nbt.getList("size", IntTag.class).size() == 3, "size of size list should be 3");

        ListTag<IntTag> size = nbt.getList("size", IntTag.class);
        int sizeX = size.get(0).getData();
        int sizeY = size.get(1).getData();
        int sizeZ = size.get(2).getData();
        CompoundTag structureNBT = nbt.getCompound("structure");
        ListTag<ListTag> blockIndices = structureNBT.getList("block_indices", ListTag.class);

        Preconditions.checkArgument(blockIndices.size() == 2, "block_indices should have 2 layer");
        Preconditions.checkArgument(blockIndices.get(0).size() == sizeX * sizeY * sizeZ, "size of layer0 incorrect, it should be" + sizeX * sizeY * sizeZ);
        Preconditions.checkArgument(blockIndices.get(1).size() == sizeX * sizeY * sizeZ, "size of layer1 incorrect, it should be" + sizeX * sizeY * sizeZ);

        @SuppressWarnings("unchecked")
        List<IntTag> layer0 = ((ListTag<IntTag>) blockIndices.get(0)).getAll();
        @SuppressWarnings("unchecked")
        List<IntTag> layer1 = ((ListTag<IntTag>) blockIndices.get(1)).getAll();
        CompoundTag palette = structureNBT.getCompound("palette").getCompound("default");
        CompoundTag blockEntityNBT = palette.getCompound("block_position_data");
        List<BlockState> blockPalette = palette.getList("block_palette", CompoundTag.class).getAll().stream().map(ItemHelper::getBlockStateHelper).toList();
        //replace null block states with unknown state
        blockPalette = blockPalette.stream().map(bs -> bs == null ? STATE_UNKNOWN : bs).toList();

        BlockState[][][][] blockStates = new BlockState[2][sizeX][sizeY][sizeZ];
        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
                    if (layer0.get(indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz)).getData() == -1) {
                        blockStates[0][lx][ly][lz] = STATE_AIR;
                    } else {
                        blockStates[0][lx][ly][lz] = blockPalette.get(layer0.get(indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz)).getData());
                    }
                    if (layer1.get(indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz)).getData() == -1) {
                        blockStates[1][lx][ly][lz] = STATE_AIR;
                    } else {
                        blockStates[1][lx][ly][lz] = blockPalette.get(layer1.get(indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz)).getData());
                    }
                }
            }
        }

        Map<Vector3, CompoundTag> blockEntities = new HashMap<>();
        for (var index : blockEntityNBT.getTags().keySet()) {
            blockEntities.put(
                    posFromIndex(sizeX, sizeY, sizeZ, Integer.parseInt(index)),
                    blockEntityNBT.getCompound(index)
            );
        }

        Preconditions.checkArgument(nbt.getList("structure_world_origin", IntTag.class).size() == 3, "size of structure_world_origin list should be 3");

        ListTag<IntTag> origin = nbt.getList("structure_world_origin", IntTag.class);
        return new Structure(
                blockStates, blockEntities,
                structureNBT.getList("entities", CompoundTag.class).getAll(),
                sizeX, sizeY, sizeZ,
                origin.get(0).getData(), origin.get(1).getData(), origin.get(2).getData()
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
                    if (l0.equals(STRUCTURE_VOID_DEFAULT_STATE)) l0 = STATE_AIR;
                    if (l1.equals(STRUCTURE_VOID_DEFAULT_STATE)) l1 = STATE_AIR;
                    if (l0 != STATE_STRUCTURE_VOID) blockManager.setBlockStateAt(x + lx, y + ly, z + lz, 0, l0);
                    if (l1 != STATE_STRUCTURE_VOID) blockManager.setBlockStateAt(x + lx, y + ly, z + lz, 1, l1);

                }
            }
        }
    }

    public void place(Position pos, boolean includeEntities, BlockManager blockManager) {
        preparePlace(pos, blockManager);
        blockManager.applySubChunkUpdate();

        final int deltaX = pos.getFloorX() - this.x;
        final int deltaY = pos.getFloorY() - this.y;
        final int deltaZ = pos.getFloorZ() - this.z;

        for (var entry : blockEntities.entrySet()) {
            final CompoundTag storedData = entry.getValue().getCompound("block_entity_data");
            if (storedData.getString("id").isEmpty()) {
                continue;
            }

            final int newPosX = storedData.contains("x") ?
                storedData.getInt("x") + deltaX : entry.getKey().getFloorX() + pos.getFloorX();
            final int newPosY = storedData.contains("y") ?
                storedData.getInt("y") + deltaY : entry.getKey().getFloorY() + pos.getFloorY();
            final int newPosZ = storedData.contains("z") ?
                storedData.getInt("z") + deltaZ : entry.getKey().getFloorZ() + pos.getFloorZ();

            final BlockEntity existing = pos.getLevel().getBlockEntity(new Vector3(newPosX, newPosY, newPosZ));
            if (existing != null) {
                existing.getLevel().removeBlockEntity(existing);
            }

            final CompoundTag newNbt = storedData.copy();
            newNbt.putInt("x", newPosX);
            newNbt.putInt("y", newPosY);
            newNbt.putInt("z", newPosZ);

            if (newNbt.contains("pairx")) {
                newNbt.putInt("pairx", newNbt.getInt("pairx") + deltaX);
            }
            if (newNbt.contains("pairz")) {
                newNbt.putInt("pairz", newNbt.getInt("pairz") + deltaZ);
            }

            BlockEntity.createBlockEntity(newNbt.getString("id"), new Position(newPosX, newPosY, newPosZ, pos.getLevel()), newNbt);
        }

        if(!includeEntities) {
            return;
        }

        for (var nbt : entities) {
            CompoundTag entityNbt = new CompoundTag(new HashMap<>(nbt.getTags()));

            List<Double> posList = new ArrayList<>();

            if(entityNbt.getList("Pos").get(0).getId() == Tag.TAG_Double) {
                posList.add(entityNbt.getList("Pos", DoubleTag.class).get(0).getData());
                posList.add(entityNbt.getList("Pos", DoubleTag.class).get(1).getData());
                posList.add(entityNbt.getList("Pos", DoubleTag.class).get(2).getData());
            } else if(entityNbt.getList("Pos").get(0).getId() == Tag.TAG_Float) {
                posList.add((double) entityNbt.getList("Pos", FloatTag.class).get(0).getData());
                posList.add((double) entityNbt.getList("Pos", FloatTag.class).get(1).getData());
                posList.add((double) entityNbt.getList("Pos", FloatTag.class).get(2).getData());
            } else {
                log.error("Unknown Pos tag type: {}", entityNbt.getList("Pos").get(0).getId());
                continue;
            }

            double origX = posList.get(0);
            double origY = posList.get(1);
            double origZ = posList.get(2);

            double relX = origX - this.x;
            double relY = origY - this.y;
            double relZ = origZ - this.z;

            double newX = relX + x;
            double newY = relY + y;
            double newZ = relZ + z;

            entityNbt.putList("Pos", new ListTag<DoubleTag>()
                    .add(new DoubleTag(newX))
                    .add(new DoubleTag(newY))
                    .add(new DoubleTag(newZ))
            ).putList("Motion", new ListTag<DoubleTag>()
                    .add(new DoubleTag(0))
                    .add(new DoubleTag(0))
                    .add(new DoubleTag(0)));

            if(!entityNbt.contains("Rotation")) {
                entityNbt.putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(0))
                        .add(new FloatTag(0)));
            }

            Entity e = Entity.createEntity(
                    entityNbt.getString("identifier"),
                    new Position(newX, newY, newZ, pos.getLevel()).getChunk(),
                    entityNbt
            );
            if (e != null) {
                e.spawnToAll();
            }
        }
    }

    /**
     * Save the structure data to nbt.
     *
     * @return the nbt data of the structure
     */
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        // Set format version
        nbt.putInt("format_version", FORMAT_VERSION);

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
                        if (state != null && !state.equals(STRUCTURE_VOID_DEFAULT_STATE) && !blockStateToIndex.containsKey(state)) {
                            blockStateToIndex.put(state, paletteIndex++);
                            uniqueBlockStates.add(state);
                        }
                    }
                }
            }
        }

        // Create block_indices
        ListTag<ListTag> blockIndices = new ListTag<>();

        // Layer 0
        ListTag<IntTag> layer0List = new ListTag<>();
        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    BlockState state = this.blockStates[0][x][y][z];
                    if (state == null || state.equals(STRUCTURE_VOID_DEFAULT_STATE)) {
                        layer0List.add(new IntTag(-1));
                    } else {
                        layer0List.add(new IntTag(blockStateToIndex.get(state)));
                    }
                }
            }
        }
        blockIndices.add(layer0List);

        // Layer 1
        ListTag<IntTag> layer1List = new ListTag<>();
        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    BlockState state = this.blockStates[1][x][y][z];
                    if (state == null || state.equals(STRUCTURE_VOID_DEFAULT_STATE)) {
                        layer1List.add(new IntTag(-1));
                    } else {
                        layer1List.add(new IntTag(blockStateToIndex.get(state)));
                    }
                }
            }
        }
        blockIndices.add(layer1List);

        structureNBT.putList("block_indices", blockIndices);

        // Create palette
        CompoundTag paletteCompound = new CompoundTag();
        CompoundTag defaultPalette = new CompoundTag();

        // Create block_palette
        ListTag<CompoundTag> blockPaletteList = new ListTag<>();
        for (BlockState state : uniqueBlockStates) {
            CompoundTag blockStateTag = CompoundTag.fromNetwork(state.getBlockStateTag());
            blockStateTag.remove("version");
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

        List<CompoundTag> rotatedEntities = new ArrayList<>(entities);

        return new Structure(rotatedStates, rotatedBlockEntities, rotatedEntities,
                newSizeX, sizeY, newSizeZ, x, y, z);
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

        List<CompoundTag> mirroredEntities = new ArrayList<>(entities);

        return new Structure(mirroredStates, mirroredBlockEntities, mirroredEntities,
                sizeX, sizeY, sizeZ, x, y, z);
    }

    private static BlockState rotateBlockState(BlockState state, Rotation rotation) {
        if (state == null || !hasTransformableState(state)) {
            return state;
        }
        return switch (rotation) {
            case ROTATE_90 -> StructureRotationUtil.counterclockwise90(state);
            case ROTATE_180 -> StructureRotationUtil.clockwise180(state);
            case ROTATE_270 -> StructureRotationUtil.clockwise90(state);
            default -> state;
        };
    }

    private static BlockState mirrorBlockState(BlockState state, Mirror mirror) {
        if (state == null || !hasTransformableState(state)) {
            return state;
        }
        return switch (mirror) {
            case X -> StructureRotationUtil.mirrorX(state);
            case Z -> StructureRotationUtil.mirrorZ(state);
            case XZ -> StructureRotationUtil.clockwise180(state);
            default -> state;
        };
    }

    private static boolean hasTransformableState(BlockState state) {
        return state != STATE_AIR
                && state != STATE_STRUCTURE_VOID
                && state != STATE_UNKNOWN
                && !state.equals(STRUCTURE_VOID_DEFAULT_STATE);
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
        return copy;
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
