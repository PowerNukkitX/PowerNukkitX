package org.powernukkitx.network.process.handler;

import org.powernukkitx.AdventureSettings;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerHackDetectedEvent;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.AbilitiesIndex;
import org.cloudburstmc.protocol.bedrock.packet.RequestPermissionsPacket;

/**
 * @author Kaooot
 */
public class RequestPermissionsHandler implements PacketHandler<RequestPermissionsPacket> {

    @Override
    public void handle(RequestPermissionsPacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        if (!playerHandle.player.isOp()) {
            PlayerHackDetectedEvent event = new PlayerHackDetectedEvent(playerHandle.player, PlayerHackDetectedEvent.HackType.PERMISSION_REQUEST);
            playerHandle.player.getServer().getPluginManager().callEvent(event);

            if (event.isKick())
                playerHandle.player.kick("Illegal permission operation", true);

            return;
        }
        var player = server.getOnlinePlayers().values()
                .stream()
                .filter(p -> p.getId() == packet.getTargetPlayerId())
                .findAny()
                .orElse(null);
        if (player != null && player.isOnline()) {
            var customPermissions = packet.getCommandPermissionFlags();
            for (AbilitiesIndex controllableAbility : AdventureSettings.CONTROLLABLE_ABILITIES) {
                player.getAdventureSettings().set(controllableAbility, customPermissions.contains(controllableAbility));
            }
            player.getAdventureSettings().setPlayerPermission(packet.getPlayerPermissionLevel());
            player.getAdventureSettings().update();
        }
    }
}