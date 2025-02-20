package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestPermissionsPacket;
import cn.nukkit.network.protocol.types.PlayerAbility;
import org.jetbrains.annotations.NotNull;

public class RequestPermissionsProcessor extends DataPacketProcessor<RequestPermissionsPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestPermissionsPacket pk) {
        if (!playerHandle.player.isOp()) {
            PlayerHackDetectedEvent event = new PlayerHackDetectedEvent(playerHandle.player, PlayerHackDetectedEvent.HackType.PERMISSION_REQUEST);
            playerHandle.player.getServer().getPluginManager().callEvent(event);

            if(event.isKick())
                playerHandle.player.kick("Illegal permission operation", true);

            return;
        }
        var player = pk.getTargetPlayer();
        if (player != null && player.isOnline()) {
            var customPermissions = pk.parseCustomPermissions();
            for (PlayerAbility controllableAbility : RequestPermissionsPacket.CONTROLLABLE_ABILITIES) {
                player.getAdventureSettings().set(controllableAbility, customPermissions.contains(controllableAbility));
            }
            player.getAdventureSettings().setPlayerPermission(pk.permissions);
            player.getAdventureSettings().update();
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.REQUEST_PERMISSIONS_PACKET;
    }
}
