package cn.nukkit.network.protocol.types.biome;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;

public class BiomeCoordinateData implements IBiomeDefinitionListObject {

    public int minValueType;
    public int minValue;
    public int maxValueType;
    public int maxValue;
    public int gridOffset;
    public int gridStepSize;
    public int distribution;

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(minValueType);
        byteBuf.writeShortLE(minValue);
        byteBuf.writeVarInt(maxValueType);
        byteBuf.writeShortLE(maxValue);
        byteBuf.writeIntLE(gridOffset);
        byteBuf.writeIntLE(gridStepSize);
        byteBuf.writeVarInt(distribution);
    }

    @Override
    public void parse(CompoundTag tag) {
        maxValueType = tag.getInt("maxValueType");
        minValue = tag.getShort("minValue");
        minValueType = tag.getInt("minValueType");
        gridOffset = tag.getInt("gridOffset");
        maxValue = tag.getShort("maxValue");
        gridStepSize = tag.getInt("gridStepSize");
        distribution = tag.getInt("distribution");
    }
}
