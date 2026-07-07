package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.entity.data.human.Skin;
import org.powernukkitx.event.player.PlayerChangeSkinEvent;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.utils.SkinUtils;
import lombok.extern.slf4j.Slf4j;
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
        if (player.getServer().getSettings().playerSettings().forceSkinTrusted()) {
            packet.setTrustedSkin(true);
        }
        Skin skin = new Skin(packet.getSerializedSkin(), packet.isTrustedSkin());

        if (!player.spawned || !player.isAlive()) {
            log.debug("Player {} tried to update skin while not spawned or dead", playerHandle.getUsername());
            return;
        }

        if (!SkinUtils.isValid(skin.getSkin())) {
            log.warn("{}: PlayerSkinPacket with invalid skin", playerHandle.getUsername());
            return;
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