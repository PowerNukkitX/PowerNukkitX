package cn.nukkit.network.process.processor;

import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestPermissionsPacket;
import cn.nukkit.network.protocol.types.PlayerAbility;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class RequestPermissionsProcessor extends DataPacketProcessor<RequestPermissionsPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestPermissionsPacket pk) {
        Player player = playerHandle.getPlayer();
        if (!player.isOp()) {
            player.kick("Illegal permission operation", true);
            return;
        }
        var targetPlayer = pk.getTargetPlayer();
        if (targetPlayer != null && targetPlayer.isOnline()) {
            var customPermissions = pk.parseCustomPermissions();
            for (PlayerAbility controllableAbility : RequestPermissionsPacket.CONTROLLABLE_ABILITIES) {
                targetPlayer
                        .getAdventureSettings()
                        .set(controllableAbility, customPermissions.contains(controllableAbility));
            }
            targetPlayer.getAdventureSettings().setPlayerPermission(pk.permissions);
            targetPlayer.getAdventureSettings().update();
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.REQUEST_PERMISSIONS_PACKET);
    }
}
