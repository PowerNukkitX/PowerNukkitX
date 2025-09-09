package cn.nukkit.level.structure;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.StructureMirror;
import cn.nukkit.network.protocol.types.StructureRotation;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractStructure {
    protected static final BlockState STATE_AIR = BlockAir.PROPERTIES.getDefaultState();
    protected static final BlockState STATE_UNKNOWN = BlockUnknown.PROPERTIES.getDefaultState();

    public static AbstractStructure fromNbt(CompoundTag tag) {
        if(tag.contains("format_version")) return Structure.fromNbtAsync(tag).join();
        if(tag.contains("DataVersion")) return JeStructure.fromNbt(tag);

        return null;
    }

    public abstract CompoundTag toNBT();
    public abstract void place(Position pos, boolean includeEntities, BlockManager blockManager);
    public abstract AbstractStructure rotate(StructureRotation rotation);
    public abstract AbstractStructure mirror(StructureMirror mirror);

    public void place(Position pos) {
        this.place(pos, true);
    }

    public void place(Position pos, boolean includeEntities) {
        this.place(pos, includeEntities, new BlockManager(pos.getLevel()));
    }
}
