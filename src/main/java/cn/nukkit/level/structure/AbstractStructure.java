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
    public abstract AbstractStructure rotate(StructureRotation rotation);
    public abstract AbstractStructure mirror(StructureMirror mirror);

    public void place(Position pos) {
        this.place(pos, true);
    }

    public void place(Position pos, boolean includeEntities) {
        this.place(pos, includeEntities, new BlockManager(pos.getLevel()));
    }
}