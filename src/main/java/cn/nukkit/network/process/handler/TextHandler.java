package cn.nukkit.network.process.handler;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.TextPacketType;
import org.cloudburstmc.protocol.bedrock.data.payload.text.AuthorAndMessage;
import org.cloudburstmc.protocol.bedrock.data.payload.text.TextPacketBody;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class TextHandler implements PacketHandler<TextPacket> {

    @Override
    public void handle(TextPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        if (!playerHandle.player.spawned || !playerHandle.player.isAlive()) {
            return;
        }

        if (!playerHandle.packetRateLimiter.tryChat()) {
            return;
        }

        boolean isXboxAuth = Server.getInstance().getSettings().baseSettings().xboxAuth();
        if (isXboxAuth && !packet.getSendersXUID().equals(playerHandle.player.getXUID())) {
            log.warn("{} sent TextPacket with invalid xuid : {} != {}", playerHandle.getUsername(), packet.getSendersXUID(), holder.getPlayer().getXUID());
            return;
        }

        final TextPacketBody body = packet.getBody();

        if (!(body instanceof AuthorAndMessage authorAndMessage)) {
            playerHandle.player.close("§cPacket handling error");
            return;
        }
        if (packet.getMessageType().equals(TextPacketType.CHAT)) {
            String chatMessage = authorAndMessage.getMessage();
            int breakLine = chatMessage.indexOf('\n');
            // Chat messages shouldn't contain break lines so ignore text afterwards
            if (breakLine != -1) {
                chatMessage = chatMessage.substring(0, breakLine);
            }
            playerHandle.player.chat(chatMessage);
        }
    }
}