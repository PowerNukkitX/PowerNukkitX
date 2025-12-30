package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeMesaSurfaceData implements IBiomeDefinitionListObject {

    public int clayMaterial;
    public int hardClayMaterial;
    public boolean brycePillars;
    public boolean hasForest;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeIntLE(clayMaterial);
        byteBuf.writeIntLE(hardClayMaterial);
        byteBuf.writeBoolean(brycePillars);
        byteBuf.writeBoolean(hasForest);
    }

    @Override
    public void parse(CompoundTag tag) {
        clayMaterial = tag.getInt("clayMaterial");
        hasForest = tag.getBoolean("hasForest");
        hardClayMaterial = tag.getInt("hardClayMaterial");
        brycePillars = tag.getBoolean("brycePillars");
    }
}
