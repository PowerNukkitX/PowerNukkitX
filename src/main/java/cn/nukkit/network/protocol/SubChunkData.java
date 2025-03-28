package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubChunkData {

    private Vector3 position;
    private ByteBuf data;
    private SubChunkRequestResult result;
    private HeightMapDataType heightMapType;
    private ByteBuf heightMapData;
    private boolean cacheEnabled;
    private long blobId;


    public enum SubChunkRequestResult {
        UNDEFINED,
        SUCCESS,
        CHUNK_NOT_FOUND,
        INVALID_DIMENSION,
        PLAYER_NOT_FOUND,
        INDEX_OUT_OF_BOUNDS,
        SUCCESS_ALL_AIR
    }

    public enum HeightMapDataType {
        NO_DATA,
        HAS_DATA,
        TOO_HIGH,
        TOO_LOW
    }
}
