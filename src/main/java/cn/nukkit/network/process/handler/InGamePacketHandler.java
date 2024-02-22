package cn.nukkit.network.process.handler;

import cn.nukkit.network.process.DataPacketManager;
import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.protocol.DataPacket;

public class InGamePacketHandler extends NetworkSessionPacketHandler {
    private final DataPacketManager manager;

    public InGamePacketHandler(NetworkSession session) {
        super(session);

        this.manager = new DataPacketManager();
    }

    public void managerHandle(DataPacket pk) {
        if (manager.canProcess(pk.pid())) {
            manager.processPacket(handle, pk);
        }
    }
}
