package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class TextProcessor extends DataPacketProcessor<TextPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull TextPacket pk) {
        if (!playerHandle.player.spawned || !playerHandle.player.isAlive()) {
            return;
        }

        boolean isXboxAuth = Server.getInstance().getSettings().baseSettings().xboxAuth();
        if (isXboxAuth && !pk.getXuid().equals(playerHandle.getLoginChainData().getXUID())) {
            log.warn("{} sent TextPacket with invalid xuid : {} != {}", playerHandle.getUsername(), pk.getXuid(), playerHandle.getLoginChainData().getXUID());
            return;
        }

        if (pk.getParameters().size() > 1) {
            playerHandle.player.close("§cPacket handling error");
            return;
        }

        if (pk.getType() == TextPacket.Type.CHAT) {
            String chatMessage = pk.getMessage();
            int breakLine = chatMessage.indexOf('\n');
            // Chat messages shouldn't contain break lines so ignore text afterwards
            if (breakLine != -1) {
                chatMessage = chatMessage.substring(0, breakLine);
            }
            playerHandle.player.chat(chatMessage);
        }
    }

    @Override
    public Class<TextPacket> getPacketClass() {
        return TextPacket.class;
    }
}
