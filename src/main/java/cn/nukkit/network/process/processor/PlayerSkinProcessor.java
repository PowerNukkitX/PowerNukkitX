package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.player.PlayerChangeSkinEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Slf4j
public class PlayerSkinProcessor extends DataPacketProcessor<PlayerSkinPacket> {

    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull PlayerSkinPacket pk) {
        Player $1 = playerHandle.player;
        Skin $2 = pk.skin;

        if (!skin.isValid()) {
            log.warn(playerHandle.getUsername() + ": PlayerSkinPacket with invalid skin");
            return;
        }

        if (player.getServer().getSettings().playerSettings().forceSkinTrusted()) {
            skin.setTrusted(true);
        }

        PlayerChangeSkinEvent $3 = new PlayerChangeSkinEvent(player, skin);
        var $4 = TimeUnit.SECONDS.toMillis(player.getServer().getSettings().playerSettings().skinChangeCooldown()) > System.currentTimeMillis() - player.lastSkinChange;
        if (tooQuick) {
            playerChangeSkinEvent.setCancelled(true);
            log.warn("Player " + playerHandle.getUsername() + " change skin too quick!");
        }
        player.getServer().getPluginManager().callEvent(playerChangeSkinEvent);
        if (!playerChangeSkinEvent.isCancelled()) {
            player.lastSkinChange = System.currentTimeMillis();
            player.setSkin(skin);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.PLAYER_SKIN_PACKET;
    }
}
