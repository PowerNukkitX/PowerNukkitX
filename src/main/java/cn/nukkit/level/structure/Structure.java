package cn.nukkit.level.structure;

Implimport cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStructureVoid;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author harry-xi | daoge_cmd
 * @source <a href="https://github.com/AllayMC/Allay/blob/e86e4c950a360aa725fca913452d01d3037cea5a/api/src/main/java/org/allaymc/api/utils/Structure.java">AllayMC</a>
 */

@Slf4j
public record Structure(
        // layer-x-y-z
        BlockState[][][][] blockStates,
        Map<Vector3, CompoundTag> blockEntities,
        List<CompoundTag> entities,
        int sizeX, int sizeY, int sizeZ,
        int x, int y, int z
) {
    private static final int FORMAT_VERSION = 1;
    private static final BlockState STRUCTURE_VOID_DEFAULT_STATE = BlockStructureVoid.PROPERTIES.getDefaultState();

    /**
     * @see #pick(Level, int, int, int, int, int, int, boolean)
     */
    public static Structure pick(Level dimension, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
        return pick(dimension, x, y, z, sizeX, sizeY, sizeZ, true);
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
    public static Structure pick(Level dimension, int x, int y, int z, int sizeX, int sizeY, int sizeZ, boolean saveEntities) {
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
                        blockEntities.put(new Vector3(lx, ly, lz), blockEntity.namedTag);
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

        BlockState[][][][] blockStates = new BlockState[2][sizeX][sizeY][sizeZ];
        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
                    if (layer0.get(indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz)).getData() == -1) {
                        blockStates[0][lx][ly][lz] = STRUCTURE_VOID_DEFAULT_STATE;
                    } else {
                        blockStates[0][lx][ly][lz] = blockPalette.get(layer0.get(indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz)).getData());
                    }
                    if (layer1.get(indexFormPos(sizeX, sizeY, sizeZ, lx, ly, lz)).getData() == -1) {
                        blockStates[1][lx][ly][lz] = STRUCTURE_VOID_DEFAULT_STATE;
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
    public void place(Position pos) {
        Preconditions.checkArgument(pos.getLevel() != null, "position level cannot be null");

        int x = pos.getFloorX();
        int y = pos.getFloorY();
        int z = pos.getFloorZ();

        for (int lx = 0; lx < sizeX; lx++) {
            for (int ly = 0; ly < sizeY; ly++) {
                for (int lz = 0; lz < sizeZ; lz++) {
                    if (!blockStates[0][lx][ly][lz].equals(STRUCTURE_VOID_DEFAULT_STATE)) {
                        pos.getLevel().setBlockStateAt(x + lx, y + ly, z + lz, 0, blockStates[0][lx][ly][lz]);
                    }
                    if (!blockStates[1][lx][ly][lz].equals(STRUCTURE_VOID_DEFAULT_STATE)) {
                        pos.getLevel().setBlockStateAt(x + lx, y + ly, z + lz, 1, blockStates[1][lx][ly][lz]);
                    }
                }
            }
        }

        for (var entry : blockEntities.entrySet()) {
            // Block entity should also being spawned when placing block
            // if the block entity is implemented
            BlockEntity blockEntity = pos.getLevel().getBlockEntity(new Vector3(entry.getKey().x + x, entry.getKey().y + y, entry.getKey().z + z));
            if (blockEntity == null) {
                // Block entity not implemented maybe
                continue;
            }
            // No need to put the new position data into the nbt, as
            // the block entity have spawned and already have the new
            // position data, so just remove the old position data.
            CompoundTag oldNbt = entry.getValue();
            oldNbt.remove("x");
            oldNbt.remove("y");
            oldNbt.remove("z");
            blockEntity.namedTag = oldNbt;
            blockEntity.saveNBT();

            if(blockEntity instanceof BlockEntitySpawnable bsp) bsp.spawnToAll();
        }
        for (var nbt : entities) {
            //TODO: spawn entity from nbt
            log.warn("Spawning entity from structure is not implemented yet. Entity NBT: {}", nbt);
        }
    }

    /**
     * Save the structure data to nbt.
     *
     * @return the nbt data of the structure
     */
    public CompoundTag toNBT() {
        int capacity = sizeX * sizeY * sizeZ;
        Integer[] layer0 = new Integer[capacity];
        Integer[] layer1 = new Integer[capacity];
        BlockStatePalette palette = new BlockStatePalette();
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    layer0[indexFormPos(sizeX, sizeY, sizeZ, x, y, z)] = palette.getIndexOf(blockStates[0][x][y][z]);
                    layer1[indexFormPos(sizeX, sizeY, sizeZ, x, y, z)] = palette.getIndexOf(blockStates[1][x][y][z]);
                }
            }
        }
        CompoundTag blockEntityNBT = new CompoundTag();
        for (var index : blockEntities.entrySet()) {
            var pos = index.getKey();
            blockEntityNBT.putCompound(
                    Integer.toString(indexFormPos(sizeX, sizeY, sizeZ, (int) pos.x, (int) pos.y, (int) pos.z)),
                    index.getValue()
            );
        }

        ListTag layer0Tag = new ListTag();
        layer0Tag.setAll(Arrays.asList(layer0));

        ListTag layer1Tag = new ListTag();
        layer1Tag.setAll(Arrays.asList(layer1));

        return new CompoundTag()
                .putInt("format_version", FORMAT_VERSION)
                .putList("size", new ListTag().add(new IntTag(sizeX)).add(new IntTag(sizeY)).add(new IntTag(sizeZ)))
                .putCompound("structure",
                        new CompoundTag()
                                .putList(
                                        "block_indices",
                                        new ListTag().add(layer0Tag).add(layer1Tag))
                                .putList("entities", new ListTag(entities))
                                .putCompound("palette", new CompoundTag().putCompound(
                                        "default", new CompoundTag()
                                                .putList(
                                                        "block_palette",
                                                        new ListTag(palette.getPalette()
                                                                .stream()
                                                                .map(BlockState::getBlockStateTag)
                                                                .toList()))
                                                .putCompound("block_position_data", blockEntityNBT)

                                ))
                )
                .putList("structure_world_origin", new ListTag().add(new IntTag(x)).add(new IntTag(y)).add(new IntTag(z)));
    }

    private static class BlockStatePalette {
        @Getter
        private final List<BlockState> palette = new ArrayList<>();

        public int getIndexOf(BlockState block) {
            if (block.equals(STRUCTURE_VOID_DEFAULT_STATE)) {
                return -1;
            }
            if (palette.contains(block)) {
                return palette.indexOf(block);
            } else {
                int index = palette.size();
                palette.add(block);
                return index;
            }
        }
    }
}