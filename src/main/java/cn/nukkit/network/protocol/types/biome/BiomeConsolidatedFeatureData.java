package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeConsolidatedFeatureData implements IBiomeDefinitionListObject {

    public BiomeScatterParamData scatter;
    public int feature;
    public int identifier;
    public int pass;
    public boolean internalUse;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        scatter.encode(byteBuf);
        byteBuf.writeShortLE(feature);
        byteBuf.writeShortLE(identifier);
        byteBuf.writeShortLE(pass);
        byteBuf.writeBoolean(internalUse);
    }

    @Override
    public void parse(CompoundTag tag) {
        scatter = new BiomeScatterParamData();
        scatter.parse(tag.getCompound("scatter"));
        identifier = tag.getShort("identifier");
        feature = tag.getShort("feature");
        pass = tag.getShort("pass");
        internalUse = tag.getBoolean("canUseInternalFeature");
    }


}
