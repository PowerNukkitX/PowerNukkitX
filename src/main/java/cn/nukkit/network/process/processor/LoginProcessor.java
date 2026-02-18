package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.event.player.PlayerAnimationEvent;
import cn.nukkit.event.player.PlayerDuplicatedLoginEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class LoginProcessor extends DataPacketProcessor<LoginPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull LoginPacket pk) {
        Player player = playerHandle.player;
        if(!player.getSession().isAuthenticated()) {
            return;
        }

        PlayerDuplicatedLoginEvent event = new PlayerDuplicatedLoginEvent(player);
        player.getServer().getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            return;
        }

        player.close("§cPacket handling error");
    }
    @Override
    public Class<LoginPacket> getPacketClass() {
        return LoginPacket.class;
    }
}
