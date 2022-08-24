package cn.nukkit.level.generator.populator.impl.structure.utils.template;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public abstract class AbstractLegacyStructureTemplate extends AbstractStructureTemplate {

    protected final List<StructureBlockInfo> blockInfoList = Lists.newArrayList();
    protected final List<StructureEntityInfo> entityInfoList = Lists.newArrayList();

    @Override
    public boolean isInvalid() {
        return this.blockInfoList.isEmpty() && this.entityInfoList.isEmpty() || super.isInvalid();
    }

    @Override
    public void clean() {
        this.blockInfoList.clear();
        this.entityInfoList.clear();
    }

    protected static class SimplePalette implements Iterable<BlockEntry> {

        public static final BlockEntry DEFAULT_BLOCK_STATE = new BlockEntry(0);

        private final IdMapper<BlockEntry> ids;

        protected SimplePalette() {
            this.ids = new IdMapper<>();
        }

        @Override
        public Iterator<BlockEntry> iterator() {
            return this.ids.iterator();
        }

        public BlockEntry stateFor(int id) {
            BlockEntry block = this.ids.byId(id);
            return block == null ? DEFAULT_BLOCK_STATE : block;
        }

        public void addMapping(BlockEntry block, int id) {
            this.ids.addMapping(block, id);
        }
    }

    public static class StructureBlockInfo {

        public final BlockVector3 pos;
        public final BlockEntry state;
        public final CompoundTag nbt;

        public StructureBlockInfo(BlockVector3 pos, BlockEntry state, CompoundTag nbt) {
            this.pos = pos;
            this.state = state;
            this.nbt = nbt;
        }

        @Override
        public String toString() {
            return String.format("StructureBlockInfo(pos=%s, state=%s, nbt=%s)", this.pos, this.state, this.nbt);
        }
    }

    public static class StructureEntityInfo {

        public final Vector3 pos;
        public final BlockVector3 blockPos;
        public final CompoundTag nbt;

        public StructureEntityInfo(Vector3 pos, BlockVector3 blockPos, CompoundTag nbt) {
            this.pos = pos;
            this.blockPos = blockPos;
            this.nbt = nbt;
        }

        @Override
        public String toString() {
            return String.format("StructureEntityInfo(pos=%s, blockPos=%s, nbt=%s)", this.pos, this.blockPos, this.nbt);
        }
    }
}
