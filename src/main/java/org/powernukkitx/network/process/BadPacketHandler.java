package org.powernukkitx.network.process;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.PacketSerializeException;
import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerHackDetectedEvent;

/**
 * @author Kaooot
 */
@RequiredArgsConstructor
public class BadPacketHandler extends ChannelInboundHandlerAdapter {

    private final BedrockServerSession session;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        while (throwable != null) {
            if (throwable instanceof PacketSerializeException e) {
                for (final Player player : Server.getInstance().getOnlinePlayers().values()) {
                    if (player.getSession().getSocketAddress().equals(this.session.getSocketAddress())) {
                        final PlayerHackDetectedEvent event = new PlayerHackDetectedEvent(
                            player,
                            PlayerHackDetectedEvent.HackType.BAD_PACKET
                        );

                        Server.getInstance().getPluginManager().callEvent(event);

                        if (event.isKick()) {
                            player.close("Bad Packet");
                        }

                        Server.getInstance().getLogger().warning(player.getName() + " sent a bad packet", e);
                        return;
                    }
                }
                this.session.close("Bad Packet");
            }
            throwable = throwable.getCause();
        }
    }
}
