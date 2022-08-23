package cn.nukkit.level.generator.populator.impl.structure.utils.template;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

//todo: 替换为核心内部实现
@PowerNukkitXOnly
@Since("1.19.21-r6")
public class BlockEntry {

    private final int id;
    private final int meta;

    public BlockEntry(int id) {
        this(id, 0);
    }

    public BlockEntry(int id, int meta) {
        this.id = id;
        this.meta = meta;
    }

    public int getId() {
        return this.id;
    }

    public int getMeta() {
        return this.meta;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BlockEntry) {
            BlockEntry o = (BlockEntry) obj;
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
        return String.format("BlockEntry(id=%s, meta=%s)", this.id, this.meta);
    }
}
