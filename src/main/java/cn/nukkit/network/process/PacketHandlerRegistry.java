package cn.nukkit.network.process;

import cn.nukkit.network.process.handler.ClientToServerHandshakeHandler;
import cn.nukkit.network.process.handler.LoginHandler;
import cn.nukkit.network.process.handler.RequestChunkRadiusHandler;
import cn.nukkit.network.process.handler.RequestNetworkSettingsHandler;
import cn.nukkit.network.process.handler.ResourcePackClientResponseHandler;
import cn.nukkit.network.process.handler.SetLocalPlayerAsInitializedHandler;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.ClientToServerHandshakePacket;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestChunkRadiusPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;

import java.util.Map;

/**
 * @author Kaooot
 */
@UtilityClass
public class PacketHandlerRegistry {

    private final Map<Class<? extends BedrockPacket>, PacketHandler> MAP = new Object2ObjectOpenHashMap<>();

    static {
        register(RequestNetworkSettingsPacket.class, new RequestNetworkSettingsHandler());
        register(LoginPacket.class, new LoginHandler());
        register(ClientToServerHandshakePacket.class, new ClientToServerHandshakeHandler());
        register(ResourcePackClientResponsePacket.class, new ResourcePackClientResponseHandler());
        register(RequestChunkRadiusPacket.class, new RequestChunkRadiusHandler());
        register(SetLocalPlayerAsInitializedPacket.class, new SetLocalPlayerAsInitializedHandler());
    }

    private void register(Class<? extends BedrockPacket> clazz, PacketHandler packetHandler) {
        MAP.put(clazz, packetHandler);
    }

    public PacketHandler getPacketHandler(Class<? extends BedrockPacket> clazz) {
        if (MAP.containsKey(clazz)) {
            return MAP.get(clazz);
        }
        return null;
    }
}