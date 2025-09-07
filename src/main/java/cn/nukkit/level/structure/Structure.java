package cn.nukkit.level.structure;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.registry.mappings.JeBlockState;
import cn.nukkit.registry.mappings.MappingRegistries;
import com.google.common.base.Preconditions;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
//TODO: blockentities, entities
public class Structure {
    private static BlockState blockAir = new BlockAir().getBlockState();

    private int sizeX;
    private int sizeY;
    private int sizeZ;

    private List<StructureBlocks> blocks = new ArrayList<>();

    private Structure(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    private Structure(int sizeX, int sizeY, int sizeZ, List<StructureBlocks> blocks) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.blocks.addAll(blocks);
    }

    public static Structure fromNbt(CompoundTag nbt) {
        int sizeX = 0;
        int sizeY = 0;
        int sizeZ = 0;

        ListTag<IntTag> sizeNbt = nbt.getList("size", IntTag.class);
        if (sizeNbt != null && sizeNbt.size() == 3) {
            sizeX = sizeNbt.get(0).getData();
            sizeY = sizeNbt.get(1).getData();
            sizeZ = sizeNbt.get(2).getData();
        }

        ListTag<CompoundTag> blocksNbt = nbt.getList("blocks", CompoundTag.class);
        List<StructureBlocks> structureStates = new ArrayList<>();

        List<BlockState> palette = new ArrayList<>();

        //parse palette
        ListTag<CompoundTag> paletteNbt = nbt.getList("palette", CompoundTag.class);
        if (paletteNbt != null) {
            for (CompoundTag blockStateNbt : paletteNbt.getAll()) {
                String jeName = blockStateNbt.getString("Name");
                CompoundTag properties = blockStateNbt.getCompound("Properties");
                //reverse property order to match mapping
                CompoundTag reversedProperties = new CompoundTag();
                if (properties != null) {
                    List<String> keys = new ArrayList<>(properties.getTags().keySet());
                    for (int i = keys.size() - 1; i >= 0; i--) {
                        String key = keys.get(i);
                        reversedProperties.put(key, properties.get(key));
                    }
                    properties = reversedProperties;
                }

                String propertiesString = properties != null ? reversedProperties.toSNBT().replace("\"", "").replace(':', '=').replace('{', '[').replace('}', ']') : "";

                BlockState block = MappingRegistries.BLOCKS.getPNXBlock(new JeBlockState(jeName + propertiesString));
                if(block == null) {
                    block = blockAir;
                }
                palette.add(block);
            }
        }

        //parse blocks
        for (CompoundTag blockNbt : blocksNbt.getAll()) {
            ListTag<IntTag> location = blockNbt.getList("pos", IntTag.class);
            int x = location.get(0).getData();
            int y = location.get(1).getData();
            int z = location.get(2).getData();
            int state = blockNbt.getInt("state");
            structureStates.add(new StructureBlocks(x, y, z, palette.size() > state ? palette.get(state) : blockAir));
        }

        return new Structure(sizeX, sizeY, sizeZ, structureStates);
    }

    public void place(Position position) {
        Preconditions.checkArgument(position.getLevel() != null, "Position level cannot be null");

        for (StructureBlocks block : this.blocks) {
            int x = position.getFloorX() + block.x;
            int y = position.getFloorY() + block.y;
            int z = position.getFloorZ() + block.z;

            position.getLevel().setBlockStateAt(x,y,z, block.state);
        }
    }

    public record StructureBlocks(int x, int y, int z, BlockState state) {}
}
