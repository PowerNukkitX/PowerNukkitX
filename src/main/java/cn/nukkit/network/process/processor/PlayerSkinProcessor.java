package cn.nukkit.network.process.processor;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.player.PlayerChangeSkinEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class PlayerSkinProcessor extends DataPacketProcessor<PlayerSkinPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerSkinPacket pk) {
        Player player = playerHandle.getPlayer();
        Skin skin = pk.skin;

        if (!skin.isValid()) {
            log.warn(playerHandle.getUsername() + ": PlayerSkinPacket with invalid skin");
            return;
        }

        if (player.getServer().isForceSkinTrusted()) {
            skin.setTrusted(true);
        }

        PlayerChangeSkinEvent playerChangeSkinEvent = new PlayerChangeSkinEvent(player, skin);
        var tooQuick = TimeUnit.SECONDS.toMillis(player.getServer().getPlayerSkinChangeCooldown())
                > System.currentTimeMillis() - player.lastSkinChange;
        if (tooQuick) {
            playerChangeSkinEvent.cancel();
            log.warn("Player " + playerHandle.getUsername() + " change skin too quick!");
        }
        playerChangeSkinEvent.call();
        if (!playerChangeSkinEvent.isCancelled()) {
            player.lastSkinChange = System.currentTimeMillis();
            player.setSkin(skin);
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.PLAYER_SKIN_PACKET);
    }
}
