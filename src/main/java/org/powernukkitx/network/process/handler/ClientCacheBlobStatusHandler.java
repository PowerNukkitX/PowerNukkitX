package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.cache.ClientBlobCacheManager;

import org.cloudburstmc.protocol.bedrock.packet.ClientCacheBlobStatusPacket;
import org.cloudburstmc.protocol.bedrock.packet.ClientCacheMissResponsePacket;

import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles client responses to advertised blob-cache entries.
 *
 * @author Curse
 */
@Slf4j
public class ClientCacheBlobStatusHandler implements PacketHandler<ClientCacheBlobStatusPacket> {

    @Override
    public void handle(ClientCacheBlobStatusPacket packet, PlayerSessionHolder holder, Server server) {
        if (!ClientBlobCacheManager.isEnabled(holder.getSession())) return;

        final ClientCacheMissResponsePacket response = new ClientCacheMissResponsePacket();

        for (int i = 0; i < packet.getFoundIds().size(); i++) {
            ClientBlobCacheManager.acknowledge(holder.getSession(), packet.getFoundIds().getLong(i));
        }

        for (int i = 0; i < packet.getMissingIds().size(); i++) {
            final long id = packet.getMissingIds().getLong(i);
            final byte[] blob = ClientBlobCacheManager.get(holder.getSession(), id);
            ClientBlobCacheManager.acknowledge(holder.getSession(), id);

            if (blob == null) {
                log.debug("Client requested unknown cache blob {}", Long.toUnsignedString(id));
                continue;
            }

            response.getMissingBlobs().put(id, Unpooled.wrappedBuffer(blob));
        }

        holder.getSession().sendPacketImmediately(response);
    }

    @Override
    public boolean runsOnNetworkThread() {
        return true;
    }
}
