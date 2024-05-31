package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.FilterTextPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class FilterTextProcessor extends DataPacketProcessor<FilterTextPacket> {
    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull FilterTextPacket pk) {
        Player $1 = playerHandle.player;
        if (pk.text == null || pk.text.length() > 64) {
            log.debug(playerHandle.getUsername() + ": FilterTextPacket with too long text");
            return;
        }
        FilterTextPacket $2 = new FilterTextPacket();

        textResponsePacket.text = pk.text;
        textResponsePacket.fromServer = true;
        player.dataPacket(textResponsePacket);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.FILTER_TEXT_PACKET;
    }
}
