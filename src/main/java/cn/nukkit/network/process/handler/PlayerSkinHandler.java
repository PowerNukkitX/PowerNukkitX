package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerChangeSkinEvent;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.PlayerSkinPacket;

import java.util.concurrent.TimeUnit;

/**
 * @author Kaooot
 */
@Slf4j
public class PlayerSkinHandler implements PacketHandler<PlayerSkinPacket> {

    @Override
    public void handle(PlayerSkinPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        SerializedSkin skin = packet.getSerializedSkin();

        if (!skin.isValid()) {
            log.warn("{}: PlayerSkinPacket with invalid skin", playerHandle.getUsername());
            return;
        }

        if (player.getServer().getSettings().playerSettings().forceSkinTrusted()) {
            packet.setTrustedSkin(true);
        }

        PlayerChangeSkinEvent playerChangeSkinEvent = new PlayerChangeSkinEvent(player, skin);
        var tooQuick = TimeUnit.SECONDS.toMillis(player.getServer().getSettings().playerSettings().skinChangeCooldown()) > System.currentTimeMillis() - player.lastSkinChange;
        if (tooQuick) {
            playerChangeSkinEvent.setCancelled(true);
            log.warn("Player {} change skin too quick!", playerHandle.getUsername());
        }
        player.getServer().getPluginManager().callEvent(playerChangeSkinEvent);
        if (!playerChangeSkinEvent.isCancelled()) {
            player.lastSkinChange = System.currentTimeMillis();
            player.setSkin(skin);
        }
    }
}