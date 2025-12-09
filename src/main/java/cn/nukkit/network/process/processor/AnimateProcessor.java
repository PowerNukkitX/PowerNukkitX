package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.event.player.PlayerAnimationEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class AnimateProcessor extends DataPacketProcessor<AnimatePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull AnimatePacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        AnimatePacket.Action animation = pk.action;

        // prevent client send illegal packet to server and broadcast to other client and make other client crash
        if (animation == null // illegal action id
                || animation == AnimatePacket.Action.WAKE_UP // these actions are only for server to client
                || animation == AnimatePacket.Action.CRITICAL_HIT
                || animation == AnimatePacket.Action.MAGIC_CRITICAL_HIT) {
            return;
        }

        PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(player, pk);
        player.getServer().getPluginManager().callEvent(animationEvent);
        if (animationEvent.isCancelled()) {
            return;
        }
        animation = animationEvent.getAnimationType();

        switch (animation) {
            case ROW_RIGHT, ROW_LEFT -> {
                if (player.riding instanceof EntityBoat boat) {
                    boat.onPaddle(animation, 1); // Paddle time got removed from packet. Needs debugging!!
                }
                return;
            }
        }

        if (animationEvent.getAnimationType() == AnimatePacket.Action.SWING_ARM) {
            player.setItemCoolDown(PlayerHandle.getNoShieldDelay(), "shield");
        }

        pk = new AnimatePacket();
        pk.eid = player.getId();
        pk.action = animationEvent.getAnimationType();
        pk.swingSource = animationEvent.getSwingSource();
        pk.data = animationEvent.getData();
        Server.broadcastPacket(player.getViewers().values(), pk);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ANIMATE_PACKET;
    }
}
