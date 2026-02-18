package cn.nukkit.network.process.handler;

import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.DataPacketManager;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

public class InGamePacketHandler extends BedrockSessionPacketHandler {
    private final DataPacketManager manager;

    public InGamePacketHandler(BedrockSession session) {
        super(session);

        this.manager = new DataPacketManager();
    }

    public void managerHandle(BedrockPacket pk) {
        if (manager.canProcess(pk)) {
            manager.processPacket(handle, pk);
        }
    }

    public DataPacketManager getManager() {
        return manager;
    }
}
