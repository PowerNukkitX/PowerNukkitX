package cn.nukkit.level.generator.populator.impl.structure.utils.block.state;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.generator.populator.impl.structure.utils.math.Rotation;

//todo: 替换为自带的BlockState
@PowerNukkitXOnly
@Since("1.19.21-r2")
public class BlockState {

    public static final BlockState AIR = new BlockState(0);

    private final int id;
    private final int meta;

    public BlockState(int id) {
        this(id, 0);
    }

    public BlockState(int id, int meta) {
        this.id = id;
        this.meta = meta;
    }

    public static BlockState fromFullId(int fullId) {
        return new BlockState(fullId >> 4, fullId & 0xf);
    }

    public static BlockState fromHash(int hash) {
        return new BlockState(hash >> 6, hash & 0x3f);
    }

    public int getId() {
        return this.id;
    }

    public int getMeta() {
        return this.meta;
    }

    public int getFullId() {
        return (this.id << 4) | (this.meta & 0xf);
    }

    public int getRuntimeId() {
        return GlobalBlockPalette.getOrCreateRuntimeId(this.id, this.meta);
    }

    public Block getBlock() {
        return Block.get(this.id, this.meta);
    }

    public BlockState rotate(Rotation rot) {
        switch (rot) {
            case CLOCKWISE_90:
                return new BlockState(this.id, Rotation.clockwise90(this.id, this.meta));
            case CLOCKWISE_180:
                return new BlockState(this.id, Rotation.clockwise180(this.id, this.meta));
            case COUNTERCLOCKWISE_90:
                return new BlockState(this.id, Rotation.counterclockwise90(this.id, this.meta));
            case NONE:
            default:
                return this;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BlockState o) {
            return this.id == o.id && this.meta == o.meta;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id << 6 | this.meta;
    }

    @Override
    public String toString() {
        return String.format("BlockState(id=%s, meta=%s)", this.id, this.meta);
    }
}
