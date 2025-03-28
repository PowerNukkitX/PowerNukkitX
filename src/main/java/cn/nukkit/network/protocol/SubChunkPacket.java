package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class SubChunkPacket extends DataPacket {

    protected static final int HEIGHT_MAP_LENGTH = 256;

    public boolean cacheEnabled;
    public int dimensionType;
    public Vector3 centerPos;
    public List<SubChunkData> subChunks = new ObjectArrayList<>();


    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(cacheEnabled);
        byteBuf.writeVarInt(dimensionType);
        byteBuf.writeVector3i(centerPos);

        byteBuf.writeIntLE(subChunks.size());
        subChunks.forEach(subChunk -> this.serializeSubChunk(byteBuf, subChunk));
    }

    protected void serializeSubChunk(HandleByteBuf byteBuf, SubChunkData subChunk) {
        byteBuf.writeByte(centerPos.getFloorX());
        byteBuf.writeByte(centerPos.getFloorY());
        byteBuf.writeByte(centerPos.getFloorZ());
        byteBuf.writeByte(subChunk.getResult().ordinal());
        if(subChunk.getResult() != SubChunkData.SubChunkRequestResult.SUCCESS_ALL_AIR) {
            byteBuf.writeByteBuf(subChunk.getData());
        }
        byteBuf.writeByte(subChunk.getHeightMapType().ordinal());
        if(subChunk.getHeightMapType() == SubChunkData.HeightMapDataType.HAS_DATA) {
            ByteBuf heightMapBuf = subChunk.getHeightMapData();
            byteBuf.writeBytes(heightMapBuf, heightMapBuf.readerIndex(), HEIGHT_MAP_LENGTH);
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.SUB_CHUNK_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {

    }

    @Override
    public String toString() {
        return "SubChunkPacket{" +
                "cacheEnabled=" + cacheEnabled +
                ", dimensionType=" + dimensionType +
                ", centerPos=" + centerPos +
                ", subChunks=" + subChunks +
                '}';
    }
}
