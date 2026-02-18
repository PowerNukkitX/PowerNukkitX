package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.FilterTextPacket;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class FilterTextProcessor extends DataPacketProcessor<FilterTextPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull FilterTextPacket pk) {
        Player player = playerHandle.player;
        if (pk.getText() == null || pk.getText().length() > 64) {
            log.debug("{}: FilterTextPacket with too long text", playerHandle.getUsername());
            return;
        }
        FilterTextPacket textResponsePacket = new FilterTextPacket();

        textResponsePacket.setText(pk.getText());
        textResponsePacket.setFromServer(true);
        player.dataPacket(textResponsePacket);
    }
    @Override
    public Class<FilterTextPacket> getPacketClass() {
        return FilterTextPacket.class;
    }
}
