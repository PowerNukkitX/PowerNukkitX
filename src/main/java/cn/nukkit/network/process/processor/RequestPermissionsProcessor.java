package cn.nukkit.network.process.processor;

import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestPermissionsPacket;
import cn.nukkit.network.protocol.types.PlayerAbility;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class RequestPermissionsProcessor extends DataPacketProcessor<RequestPermissionsPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestPermissionsPacket pk) {
        if (!playerHandle.player.isOp()) {
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
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.REQUEST_PERMISSIONS_PACKET);
    }
}
