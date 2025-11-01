package cn.nukkit.level.structure;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStructureVoid;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.types.StructureMirror;
import cn.nukkit.network.protocol.types.Rotation;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
    private static final BlockState STRUCTURE_VOID_DEFAULT_STATE = BlockStructureVoid.PROPERTIES.getDefaultState();

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
                    BlockEntity blockEntity = dimension.getBlockEntity(new Vector3(x + lx, y + ly, z + lz));
                    if (blockEntity != null) {
                        // Vanilla save the original position data for block entity (and entity),
                        // which is useless as when we place the structure in different position,
                        // the old position data is not useful anymore. However, we still save it
                        // to follow the vanilla behavior for best compatibility.
                        blockEntity.saveNBT();
                        blockEntities.put(new Vector3(lx, ly, lz), blockEntity.getCleanedNBT().putString("id", blockEntity.getSaveId()));
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
                    entities.add(entity.namedTag);
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

        List<IntTag> layer0 = blockIndices.get(0).getAll();
        List<IntTag> layer1 = blockIndices.get(1).getAll();
        CompoundTag palette = structureNBT.getCompound("palette").getCompound("default");
        CompoundTag blockEntityNBT = palette.getCompound("block_position_data");
        List<BlockState> blockPalette = palette.getList("block_palette", CompoundTag.class).getAll().stream().map(NBTIO::getBlockStateHelper).toList();
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
                    if(l0 != STATE_STRUCTURE_VOID) blockManager.setBlockStateAt(x + lx, y + ly, z + lz, 0, l0);
                    if(l1 != STATE_STRUCTURE_VOID) blockManager.setBlockStateAt(x + lx, y + ly, z + lz, 1, l1);

                }
            }
        }
    }

    public void place(Position pos, boolean includeEntities, BlockManager blockManager) {
        preparePlace(pos, blockManager);
        blockManager.applySubChunkUpdate();

        for (var entry : blockEntities.entrySet()) {
            BlockEntity blockEntity = pos.getLevel().getBlockEntity(new Vector3(entry.getKey().x + x, entry.getKey().y + y, entry.getKey().z + z));
            if (blockEntity != null) {
                log.warn("Overwriting existing block entity at {}, {}, {}", entry.getKey().x + x, entry.getKey().y + y, entry.getKey().z + z);
                blockEntity.getLevel().removeBlockEntity(blockEntity);
            }

            CompoundTag oldNbt = entry.getValue();
            oldNbt.putInt("x", (int) (entry.getKey().x + x));
            oldNbt.putInt("y", (int) (entry.getKey().y + y));
            oldNbt.putInt("z", (int) (entry.getKey().z + z));

            BlockEntity.createBlockEntity(oldNbt.getString("id"), new Position(entry.getKey().x + x, entry.getKey().y + y, entry.getKey().z + z, pos.getLevel()), oldNbt);
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
            CompoundTag blockStateTag = new CompoundTag(new HashMap(state.getBlockStateTag().getTags()));            // Remove version field if it exists to match expected format
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

        // Set structure_world_origin (using 0, 0, 0)
        ListTag<IntTag> originList = new ListTag<>();
        originList.add(new IntTag(0));
        originList.add(new IntTag(0));
        originList.add(new IntTag(0));
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

                        rotatedStates[layer][rx][y][rz] = state;
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

            rotatedBlockEntities.put(new Vector3(rx, y, rz), entry.getValue());
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
    public Structure mirror(StructureMirror mirror) {
        if (mirror == StructureMirror.NONE) {
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

                        mirroredStates[layer][mx][y][mz] = state;
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

            mirroredBlockEntities.put(new Vector3(mx, y, mz), entry.getValue());
        }

        List<CompoundTag> mirroredEntities = new ArrayList<>(entities);

        return new Structure(mirroredStates, mirroredBlockEntities, mirroredEntities,
                sizeX, sizeY, sizeZ, x, y, z);
    }
}