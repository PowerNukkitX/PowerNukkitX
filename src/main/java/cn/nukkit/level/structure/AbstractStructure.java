package cn.nukkit.level.structure;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStructureVoid;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.StructureMirror;
import cn.nukkit.network.protocol.types.Rotation;

public abstract class AbstractStructure {
    protected static final BlockState STATE_AIR = BlockAir.STATE;
    protected static final BlockState STATE_UNKNOWN = BlockUnknown.PROPERTIES.getDefaultState();
    protected static final BlockState STATE_STRUCTURE_VOID = BlockStructureVoid.PROPERTIES.getDefaultState();
    protected String name;

    public static AbstractStructure fromNbt(CompoundTag tag) {
        if(tag.contains("format_version")) return Structure.fromNbtAsync(tag).join();
        if(tag.contains("PNX")) return PNXStructure.fromNbtAsync(tag).join();
        if(tag.contains("DataVersion")) return JeStructure.fromNbtAsync(tag).join();
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract CompoundTag toNBT();
    public abstract void place(Position pos, boolean includeEntities, BlockManager blockManager);
    public abstract void preparePlace(Position pos, BlockManager blockManager);
    public abstract AbstractStructure rotate(Rotation rotation);
    public abstract AbstractStructure mirror(StructureMirror mirror);

    protected interface BlockAccessor<T> {
        int x(T block);
        int y(T block);
        int z(T block);
        BlockState state(T block);
    }

    protected static <T> void placeBlocks(Position position, BlockManager blockManager, Iterable<T> blocks, BlockAccessor<T> accessor) {
        int baseX = position.getFloorX();
        int baseY = position.getFloorY();
        int baseZ = position.getFloorZ();
        for (T block : blocks) {
            BlockState state = accessor.state(block);
            if (state == STATE_STRUCTURE_VOID) {
                continue;
            }
            blockManager.setBlockStateAt(baseX + accessor.x(block), baseY + accessor.y(block), baseZ + accessor.z(block), state);
        }
    }

    protected static int rotatedSizeX(int sizeX, int sizeZ, Rotation rotation) {
        return rotation == Rotation.ROTATE_180 ? sizeX : sizeZ;
    }

    protected static int rotatedSizeZ(int sizeX, int sizeZ, Rotation rotation) {
        return rotation == Rotation.ROTATE_180 ? sizeZ : sizeX;
    }

    protected static int rotateX(int sizeX, int sizeZ, int x, int z, Rotation rotation) {
        return switch (rotation) {
            case ROTATE_90 -> z;
            case ROTATE_180 -> sizeX - 1 - x;
            case ROTATE_270 -> sizeZ - 1 - z;
            default -> x;
        };
    }

    protected static int rotateZ(int sizeX, int sizeZ, int x, int z, Rotation rotation) {
        return switch (rotation) {
            case ROTATE_90 -> sizeX - 1 - x;
            case ROTATE_180 -> sizeZ - 1 - z;
            case ROTATE_270 -> x;
            default -> z;
        };
    }

    public void place(Position pos) {
        this.place(pos, true);
    }

    public void place(Position pos, boolean includeEntities) {
        this.place(pos, includeEntities, new BlockManager(pos.getLevel()));
    }
}
