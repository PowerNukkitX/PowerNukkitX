package cn.nukkit.network.process.handler;

import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.DataPacketManager;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;

public class InGamePacketHandler extends BedrockSessionPacketHandler {
    private final DataPacketManager manager;

    public InGamePacketHandler(BedrockSession session) {
        super(session);

        this.manager = new DataPacketManager();
    }

    public void managerHandle(DataPacket pk) {
        if (manager.canProcess(pk.pid())) {
            manager.processPacket(handle, pk);
        }
    }

    @Override
    public void handle(SetLocalPlayerAsInitializedPacket pk) {
        handle.onPlayerLocallyInitialized();
    }

    public DataPacketManager getManager() {
        return manager;
    }
}
