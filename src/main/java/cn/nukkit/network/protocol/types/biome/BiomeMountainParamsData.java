package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeMountainParamsData implements IBiomeDefinitionListObject {

    public int steepBlock;
    public boolean northSlopes;
    public boolean southSlopes;
    public boolean westSlopes;
    public boolean eastSlopes;
    public boolean topSlideEnabled;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeIntLE(steepBlock);
        byteBuf.writeBoolean(northSlopes);
        byteBuf.writeBoolean(southSlopes);
        byteBuf.writeBoolean(westSlopes);
        byteBuf.writeBoolean(eastSlopes);
        byteBuf.writeBoolean(topSlideEnabled);
    }

    @Override
    public void parse(CompoundTag tag) {
        steepBlock = tag.getInt("steepBlock");
        westSlopes = tag.getBoolean("westSlopes");
        topSlideEnabled = tag.getBoolean("topSlideEnabled");
        northSlopes = tag.getBoolean("northSlopes");
        southSlopes = tag.getBoolean("southSlopes");
        eastSlopes = tag.getBoolean("eastSlopes");
    }
}
