package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.RequestPermissionsPacket;
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
        var player = playerHandle.player.getServer().getOnlinePlayers().values().stream()
                .filter(target -> target.getId() == pk.getUniqueEntityId())
                .findFirst()
                .orElse(null);
        if (player != null && player.isOnline()) {
            player.getAdventureSettings().setPlayerPermission(pk.getPermissions());
            player.getAdventureSettings().update();
        }
    }

    @Override
    public Class<RequestPermissionsPacket> getPacketClass() {
        return RequestPermissionsPacket.class;
    }
}
