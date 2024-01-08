package cn.nukkit.network.protocol;

import cn.nukkit.Nukkit;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.registry.Registries;
import com.google.common.io.ByteStreams;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

@ToString
public class BiomeDefinitionListPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.BIOME_DEFINITION_LIST_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.put(Registries.BIOME.getBiomeDefinitionListPacketData());
    }
}
