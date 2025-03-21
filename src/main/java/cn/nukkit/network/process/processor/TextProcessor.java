package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.TextPacket;
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
        if(isXboxAuth && !pk.xboxUserId.equals(playerHandle.getLoginChainData().getXUID())) {
            log.warn("{} sent TextPacket with invalid xuid : {} != {}", playerHandle.getUsername(), pk.xboxUserId, playerHandle.getLoginChainData().getXUID());
            return;
        }

        if(pk.parameters.length > 1) {
            playerHandle.player.close("§cPacket handling error");
            return;
        }

        if (pk.type == TextPacket.TYPE_CHAT) {
            String chatMessage = pk.message;
            int breakLine = chatMessage.indexOf('\n');
            // Chat messages shouldn't contain break lines so ignore text afterwards
            if (breakLine != -1) {
                chatMessage = chatMessage.substring(0, breakLine);
            }
            playerHandle.player.chat(chatMessage);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.TEXT_PACKET;
    }
}
