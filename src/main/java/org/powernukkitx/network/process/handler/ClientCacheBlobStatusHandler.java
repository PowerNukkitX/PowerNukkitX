package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.cache.ClientBlobCacheManager;

import org.cloudburstmc.protocol.bedrock.packet.ClientCacheBlobStatusPacket;
import org.cloudburstmc.protocol.bedrock.packet.ClientCacheMissResponsePacket;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles client responses to advertised blob-cache entries.
 *
 * @author Curse
 */
@Slf4j
public class ClientCacheBlobStatusHandler implements PacketHandler<ClientCacheBlobStatusPacket> {

    /**
     * Upper bound on the number of blobs re-sent for one status packet. The client may report up to
     * 1536 misses, and a client that keeps reporting the same IDs makes the server resend the same
     * subchunk payloads indefinitely, so only the first misses of a packet are served.
     */
    private static final int MAX_MISSING_BLOBS_PER_PACKET = 128;

    @Override
    public void handle(ClientCacheBlobStatusPacket packet, PlayerSessionHolder holder, Server server) {
        if (!ClientBlobCacheManager.isEnabled(holder.getSession())) return;

        final ClientCacheMissResponsePacket response = new ClientCacheMissResponsePacket();

        for (int i = 0; i < packet.getFoundIds().size(); i++) {
            ClientBlobCacheManager.acknowledge(holder.getSession(), packet.getFoundIds().getLong(i));
        }

        final LongOpenHashSet handledIds = new LongOpenHashSet();
        for (int i = 0; i < packet.getMissingIds().size() && handledIds.size() < MAX_MISSING_BLOBS_PER_PACKET; i++) {
            final long id = packet.getMissingIds().getLong(i);
            if (!handledIds.add(id)) {
                continue;
            }

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
