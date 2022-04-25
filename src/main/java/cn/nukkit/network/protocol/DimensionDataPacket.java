package cn.nukkit.network.protocol;

import cn.nukkit.level.DimensionData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;


public class DimensionDataPacket extends DataPacket {
    private final List<DimensionData> definitions = new ObjectArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.DIMENSION_DATA_PACKET;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {

    }
}
