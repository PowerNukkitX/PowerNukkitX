package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.cache.ClientBlobCacheManager;

import org.cloudburstmc.protocol.bedrock.packet.ClientCacheStatusPacket;


/**
 * Handles the client packet that announces blob-cache support.
 *
 * @author Curse
 */
public class ClientCacheStatusHandler implements PacketHandler<ClientCacheStatusPacket> {

    @Override
    public void handle(ClientCacheStatusPacket packet, PlayerSessionHolder holder, Server server) {
        ClientBlobCacheManager.setEnabled(holder.getSession(), packet.isCacheSupported());
    }

    @Override
    public boolean runsOnNetworkThread() {
        return true;
    }
}
