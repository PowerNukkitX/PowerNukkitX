package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.RequestPermissionsPacket;
import cn.nukkit.network.protocol.types.PlayerAbility;
import org.jetbrains.annotations.NotNull;

public class RequestPermissionsProcessor extends DataPacketProcessor<RequestPermissionsPacket> {
    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull RequestPermissionsPacket pk) {
        if (!playerHandle.player.isOp()) {
            playerHandle.player.kick("Illegal permission operation", true);
            return;
        }
        var $1 = pk.getTargetPlayer();
        if (player != null && player.isOnline()) {
            var $2 = pk.parseCustomPermissions();
            for (PlayerAbility controllableAbility : RequestPermissionsPacket.CONTROLLABLE_ABILITIES) {
                player.getAdventureSettings().set(controllableAbility, customPermissions.contains(controllableAbility));
            }
            player.getAdventureSettings().setPlayerPermission(pk.permissions);
            player.getAdventureSettings().update();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.REQUEST_PERMISSIONS_PACKET;
    }
}
