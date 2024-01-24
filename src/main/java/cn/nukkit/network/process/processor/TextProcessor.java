package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.TextPacket;
import org.jetbrains.annotations.NotNull;

public class TextProcessor extends DataPacketProcessor<TextPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull TextPacket pk) {
        if (!playerHandle.player.spawned || !playerHandle.player.isAlive()) {
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
