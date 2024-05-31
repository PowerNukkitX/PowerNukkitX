package cn.nukkit.network.process.handler;

import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.DataPacketManager;
import cn.nukkit.network.protocol.DataPacket;

public class InGamePacketHandler extends BedrockSessionPacketHandler {
    private final DataPacketManager manager;
    /**
     * @deprecated 
     */
    

    public InGamePacketHandler(BedrockSession session) {
        super(session);

        this.manager = new DataPacketManager();
    }
    /**
     * @deprecated 
     */
    

    public void managerHandle(DataPacket pk) {
        if (manager.canProcess(pk.pid())) {
            manager.processPacket(handle, pk);
        }
    }

    public DataPacketManager getManager() {
        return manager;
    }
}
