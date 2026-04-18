package cn.nukkit.level.structure;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.registry.mappings.JeBlockState;
import cn.nukkit.registry.mappings.MappingRegistries;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.structure.Mirror;
import org.cloudburstmc.protocol.bedrock.data.structure.Rotation;

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

    public static JeStructure fromNbt(NbtMap nbt) {
        List<Integer> sizeNbt = nbt.getList("size", NbtType.INT);
        int sizeX = 0, sizeY = 0, sizeZ = 0;
        if (sizeNbt != null && sizeNbt.size() == 3) {
            sizeX = sizeNbt.get(0);
            sizeY = sizeNbt.get(1);
            sizeZ = sizeNbt.get(2);
        }

        List<NbtMap> blocksNbt = nbt.getList("blocks", NbtType.COMPOUND);
        List<StructureBlockInstance> blockInstances = new ArrayList<>(blocksNbt != null ? blocksNbt.size() : 0);

        Map<String, StructureBlocks> blockCache = new HashMap<>();
        List<BlockState> palette = new ArrayList<>();
        List<NbtMap> paletteNbt = nbt.getList("palette", NbtType.COMPOUND);
        if (paletteNbt.size() == 0) {
            var palettesNbt = nbt.getList("palettes", NbtType.LIST);
            if (!palettesNbt.isEmpty() && palettesNbt.get(0) instanceof NbtList listTag) {
                boolean allCompound = true;
                for (Object tag : listTag) {
                    if (!(tag instanceof NbtMap)) {
                        allCompound = false;
                        break;
                    }
                }
                if (allCompound) {
                    List<NbtMap> compounds = new ArrayList<>();
                    for (Object tag : listTag) {
                        compounds.add((NbtMap) tag);
                    }
                    if (!compounds.isEmpty()) {
                        paletteNbt = new ObjectArrayList<>(compounds);
                    }
                }
            }
        }
        if (paletteNbt.size() != 0) {
            for (NbtMap blockStateNbt : paletteNbt) {
                String jeName = blockStateNbt.getString("Name");
                NbtMap properties = blockStateNbt.getCompound("Properties");

                StringBuilder sb = new StringBuilder();
                if (properties != null) {
                    List<String> keys = new ArrayList<>(properties.keySet());
                    sb.append("[");
                    for (int i = keys.size() - 1; i >= 0; i--) {
                        String key = keys.get(i);
                        sb.append(key).append("=").append(properties.get(key));
                        if (i != 0) sb.append(",");
                    }
                    sb.append("]");
                }
                String fullIdentifier = jeName + sb;

                BlockState state = blockCache.computeIfAbsent(fullIdentifier, id -> {
                    BlockState b = MappingRegistries.BLOCKS.getPNXBlock(new JeBlockState(id));
                    if (b == null) log.warn("Unknown block state in structure palette: {}", id);
                    return new StructureBlocks(b != null ? b : STATE_UNKNOWN);
                }).state;
                palette.add(state);
            }
        }

        if (blocksNbt != null) {
            for (NbtMap blockNbt : blocksNbt) {
                List<Integer> pos = blockNbt.getList("pos", NbtType.INT);
                int x = pos.get(0);
                int y = pos.get(1);
                int z = pos.get(2);
                int stateIndex = blockNbt.getInt("state");
                NbtMap nbtData = blockNbt.getCompound("nbt");
                BlockState state = stateIndex < palette.size() ? palette.get(stateIndex) : STATE_AIR;

                StructureBlocks cached = blockCache.computeIfAbsent(state.toString() + nbtData, k -> new StructureBlocks(state, nbtData));
                blockInstances.add(new StructureBlockInstance(x, y, z, cached));
            }
        }

        return new JeStructure(sizeX, sizeY, sizeZ, blockInstances);
    }

    public static CompletableFuture<JeStructure> fromNbtAsync(NbtMap nbt) {
        return CompletableFuture.supplyAsync(() -> fromNbt(nbt));
    }

    @Override
    public void preparePlace(Position position, BlockManager blockManager) {
        placeBlocks(position, blockManager, blockInstances, new BlockAccessor<>() {
            @Override
            public int x(StructureBlockInstance block) {
                return block.x;
            }

            @Override
            public int y(StructureBlockInstance block) {
                return block.y;
            }

            @Override
            public int z(StructureBlockInstance block) {
                return block.z;
            }

            @Override
            public BlockState state(StructureBlockInstance block) {
                return block.block.state;
            }
        });
    }

    public void place(Position position, boolean includeEntities, BlockManager blockManager) {
        preparePlace(position, blockManager);
        blockManager.applySubChunkUpdate();
    }

    public JeStructure rotate(Rotation rotation) {
        if (rotation == Rotation.NONE) return this;

        int newSizeX = rotatedSizeX(sizeX, sizeZ, rotation);
        int newSizeZ = rotatedSizeZ(sizeX, sizeZ, rotation);

        List<StructureBlockInstance> rotated = new ArrayList<>(blockInstances.size());
        for (StructureBlockInstance b : blockInstances) {
            int rx = rotateX(sizeX, sizeZ, b.x, b.z, rotation);
            int rz = rotateZ(sizeX, sizeZ, b.x, b.z, rotation);
            rotated.add(new StructureBlockInstance(rx, b.y, rz, b.block));
        }

        return new JeStructure(newSizeX, sizeY, newSizeZ, rotated);
    }

    public JeStructure mirror(Mirror mirror) {
        if (mirror == Mirror.NONE) return this;

        List<StructureBlockInstance> mirrored = new ArrayList<>(blockInstances.size());
        for (StructureBlockInstance b : blockInstances) {
            int mx = b.x, mz = b.z;
            switch (mirror) {
                case X -> mx = sizeX - 1 - b.x;
                case Z -> mz = sizeZ - 1 - b.z;
                case XZ -> {
                    mx = sizeX - 1 - b.x;
                    mz = sizeZ - 1 - b.z;
                }
            }
            mirrored.add(new StructureBlockInstance(mx, b.y, mz, b.block));
        }

        return new JeStructure(sizeX, sizeY, sizeZ, mirrored);
    }

    @Override
    public NbtMap toNBT() {
        return null;
    }

    /**
     * Reusable block instance for memory efficiency
     */
    public static class StructureBlocks {
        public final BlockState state;
        public final NbtMap compoundTag;

        public StructureBlocks(BlockState state) {
            this(state, null);
        }

        public StructureBlocks(BlockState state, NbtMap compoundTag) {
            this.state = state;
            this.compoundTag = compoundTag;
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
