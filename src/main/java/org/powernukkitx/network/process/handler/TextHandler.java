package org.powernukkitx.network.process.handler;

import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
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
    private static final int MAX_CHAT_LENGTH = 256;

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

            if (chatMessage == null || chatMessage.isEmpty()) {
                return;
            }

            if (chatMessage.length() > MAX_CHAT_LENGTH) {
                log.warn("{} sent an oversized chat message ({} chars)", playerHandle.getUsername(), chatMessage.length());
                playerHandle.player.close("§cPacket handling error");
                return;
            }

            chatMessage = sanitizeChatMessage(chatMessage);

            if (chatMessage.isEmpty()) {
                return;
            }

            int breakLine = chatMessage.indexOf('\n');
            if (breakLine != -1) {
                chatMessage = chatMessage.substring(0, breakLine);
            }

            playerHandle.player.chat(chatMessage);
        }
    }

    private String sanitizeChatMessage(String input) {
        StringBuilder sb = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c < 0x20 && c != '\n' && c != '\t') {
                continue;
            }
            if (c >= 0x7F && c <= 0x9F) {
                continue;
            }

            if (Character.isHighSurrogate(c)) {
                if (i + 1 < input.length() && Character.isLowSurrogate(input.charAt(i + 1))) {
                    sb.append(c);
                    sb.append(input.charAt(i + 1));
                    i++;
                }
                continue;
            }
            if (Character.isLowSurrogate(c)) {
                continue;
            }

            if ((c >= 0x202A && c <= 0x202E) || (c >= 0x2066 && c <= 0x2069)) {
                continue;
            }

            if (Character.getType(c) == Character.FORMAT) {
                continue;
            }

            sb.append(c);
        }

        return sb.toString();
    }
}
