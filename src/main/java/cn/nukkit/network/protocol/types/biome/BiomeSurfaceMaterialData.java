package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeSurfaceMaterialData implements IBiomeDefinitionListObject {

    public int topBlock;
    public int midBlock;
    public int seaFloorBlock;
    public int foundationBlock;
    public int seaBlock;
    public int seaFloorDepth;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeIntLE(topBlock);
        byteBuf.writeIntLE(midBlock);
        byteBuf.writeIntLE(seaFloorBlock);
        byteBuf.writeIntLE(foundationBlock);
        byteBuf.writeIntLE(seaBlock);
        byteBuf.writeIntLE(seaFloorDepth);
    }

    @Override
    public void parse(CompoundTag tag) {
        seaFloorBlock = tag.getInt("seaFloorBlock");
        midBlock = tag.getInt("midBlock");
        topBlock = tag.getInt("topBlock");
        foundationBlock = tag.getInt("foundationBlock");
        seaBlock = tag.getInt("seaBlock");
        seaFloorDepth = tag.getInt("seaFloorDepth");
    }
}
