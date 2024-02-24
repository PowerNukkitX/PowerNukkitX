package cn.nukkit.network.protocol;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@ToString(doNotUseGetters = true)
public class CompressedBiomeDefinitionListPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.COMPRESSED_BIOME_DEFINITIONS_LIST;
    private CompoundTag definitions;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    protected static final byte[] COMPRESSED_INDICATOR = new byte[]{(byte) 0xe4, (byte) 0x92, 0x3f, 0x43, 0x4f, 0x4d, 0x50, 0x52, 0x45, 0x53, 0x53, 0x45, 0x44}; // __?COMPRESSED

    @Override
    public void encode(HandleByteBuf byteBuf) {//todo 计划实现
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
