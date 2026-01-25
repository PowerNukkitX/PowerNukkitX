package cn.nukkit.level.structure;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.types.StructureMirror;
import cn.nukkit.network.protocol.types.Rotation;
import cn.nukkit.registry.mappings.JeBlockState;
import cn.nukkit.registry.mappings.MappingRegistries;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@Slf4j
//TODO: blockentities, entities
public class JeStructure extends AbstractStructure {

    private int sizeX;
    private int sizeY;
    private int sizeZ;

    // store block positions and reference the same StructureBlock instance for identical block states
    private List<StructureBlockInstance> blockInstances;

    private JeStructure(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.blockInstances = new ArrayList<>();
    }

    private JeStructure(int sizeX, int sizeY, int sizeZ, List<StructureBlockInstance> blocks) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.blockInstances = new ArrayList<>(blocks.size());
        this.blockInstances.addAll(blocks);
    }

    public static JeStructure fromNbt(CompoundTag nbt) {
        ListTag<IntTag> sizeNbt = nbt.getList("size", IntTag.class);
        int sizeX = 0, sizeY = 0, sizeZ = 0;
        if (sizeNbt != null && sizeNbt.size() == 3) {
            sizeX = sizeNbt.get(0).getData();
            sizeY = sizeNbt.get(1).getData();
            sizeZ = sizeNbt.get(2).getData();
        }

        ListTag<CompoundTag> blocksNbt = nbt.getList("blocks", CompoundTag.class);
        List<StructureBlockInstance> blockInstances = new ArrayList<>(blocksNbt != null ? blocksNbt.size() : 0);

        Map<String, StructureBlocks> blockCache = new HashMap<>();
        List<BlockState> palette = new ArrayList<>();
        ListTag<CompoundTag> paletteNbt = nbt.getList("palette", CompoundTag.class);
        if (paletteNbt.size() == 0) {
            var palettesNbt = nbt.getList("palettes", ListTag.class);
            if (!palettesNbt.getAll().isEmpty() && palettesNbt.get(0) instanceof ListTag<?> listTag) {
                boolean allCompound = true;
                for (Tag tag : listTag.getAll()) {
                    if (!(tag instanceof CompoundTag)) {
                        allCompound = false;
                        break;
                    }
                }
                if (allCompound) {
                    List<CompoundTag> compounds = new ArrayList<>();
                    for (Tag tag : listTag.getAll()) {
                        compounds.add((CompoundTag) tag);
                    }
                    if (!compounds.isEmpty()) {
                        paletteNbt = new ListTag<>(compounds);
                    }
                }
            }
        }
        if (paletteNbt.size() != 0) {
            for (CompoundTag blockStateNbt : paletteNbt.getAll()) {
                String jeName = blockStateNbt.getString("Name");
                CompoundTag properties = blockStateNbt.getCompound("Properties");

                StringBuilder sb = new StringBuilder();
                if (properties != null) {
                    List<String> keys = new ArrayList<>(properties.getTags().keySet());
                    sb.append("[");
                    for (int i = keys.size() - 1; i >= 0; i--) {
                        String key = keys.get(i);
                        sb.append(key).append("=").append(((StringTag) properties.get(key)).parseValue());
                        if (i != 0) sb.append(",");
                    }
                    sb.append("]");
                }
                String fullIdentifier = jeName + sb;

                BlockState state = blockCache.computeIfAbsent(fullIdentifier, id -> {
                    BlockState b = MappingRegistries.BLOCKS.getPNXBlock(new JeBlockState(id));
                    if(b == null) log.warn("Unknown block state in structure palette: {}", id);
                    return new StructureBlocks(b != null ? b : STATE_UNKNOWN);
                }).state;
                palette.add(state);
            }
        }

        if (blocksNbt != null) {
            for (CompoundTag blockNbt : blocksNbt.getAll()) {
                ListTag<IntTag> pos = blockNbt.getList("pos", IntTag.class);
                int x = pos.get(0).getData();
                int y = pos.get(1).getData();
                int z = pos.get(2).getData();
                int stateIndex = blockNbt.getInt("state");
                BlockState state = stateIndex < palette.size() ? palette.get(stateIndex) : STATE_AIR;

                StructureBlocks cached = blockCache.computeIfAbsent(state.toString(), k -> new StructureBlocks(state));
                blockInstances.add(new StructureBlockInstance(x, y, z, cached));
            }
        }

        return new JeStructure(sizeX, sizeY, sizeZ, blockInstances);
    }

    public static CompletableFuture<JeStructure> fromNbtAsync(CompoundTag nbt) {
        return CompletableFuture.supplyAsync(() -> fromNbt(nbt));
    }

    @Override
    public void preparePlace(Position position, BlockManager blockManager) {
        int baseX = position.getFloorX();
        int baseY = position.getFloorY();
        int baseZ = position.getFloorZ();

        for (StructureBlockInstance b : blockInstances) {
            if(b.block.state == STATE_STRUCTURE_VOID) continue;
            blockManager.setBlockStateAt(baseX + b.x, baseY + b.y, baseZ + b.z, b.block.state);
        }
    }

    public void place(Position position, boolean includeEntities, BlockManager blockManager) {
        preparePlace(position, blockManager);
        blockManager.applySubChunkUpdate();
    }

    public JeStructure rotate(Rotation rotation) {
        if (rotation == Rotation.NONE) return this;

        int newSizeX = (rotation == Rotation.ROTATE_180) ? sizeX : sizeZ;
        int newSizeZ = (rotation == Rotation.ROTATE_180) ? sizeZ : sizeX;

        List<StructureBlockInstance> rotated = new ArrayList<>(blockInstances.size());
        for (StructureBlockInstance b : blockInstances) {
            int rx = b.x, rz = b.z;
            switch (rotation) {
                case ROTATE_90 -> { rx = b.z; rz = sizeX - 1 - b.x; }
                case ROTATE_180 -> { rx = sizeX - 1 - b.x; rz = sizeZ - 1 - b.z; }
                case ROTATE_270 -> { rx = sizeZ - 1 - b.z; rz = b.x; }
            }
            rotated.add(new StructureBlockInstance(rx, b.y, rz, b.block));
        }

        return new JeStructure(newSizeX, sizeY, newSizeZ, rotated);
    }

    public JeStructure mirror(StructureMirror mirror) {
        if (mirror == StructureMirror.NONE) return this;

        List<StructureBlockInstance> mirrored = new ArrayList<>(blockInstances.size());
        for (StructureBlockInstance b : blockInstances) {
            int mx = b.x, mz = b.z;
            switch (mirror) {
                case X -> mx = sizeX - 1 - b.x;
                case Z -> mz = sizeZ - 1 - b.z;
                case XZ -> { mx = sizeX - 1 - b.x; mz = sizeZ - 1 - b.z; }
            }
            mirrored.add(new StructureBlockInstance(mx, b.y, mz, b.block));
        }

        return new JeStructure(sizeX, sizeY, sizeZ, mirrored);
    }

    @Override
    public CompoundTag toNBT() { return null; }

    /**
     * Reusable block instance for memory efficiency
     */
    public static class StructureBlocks {
        public final BlockState state;

        public StructureBlocks(BlockState state) {
            this.state = state;
        }
    }

    /**
     * A single position instance pointing to a shared StructureBlocks object
     */
    public static class StructureBlockInstance {
        public final int x, y, z;
        public final StructureBlocks block;

        public StructureBlockInstance(int x, int y, int z, StructureBlocks block) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.block = block;
        }
    }
}
