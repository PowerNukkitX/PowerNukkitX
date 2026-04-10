package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.event.player.PlayerAnimationEvent;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;

/**
 * @author Kaooot
 */
public class AnimateHandler implements PacketHandler<AnimatePacket> {

    @Override
    public void handle(AnimatePacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        AnimatePacket.Action animation = packet.getAction();

        // prevent client send illegal packet to server and broadcast to other client and make other client crash
        if (animation == null // illegal action id
                || animation == AnimatePacket.Action.WAKE_UP // these actions are only for server to client
                || animation == AnimatePacket.Action.CRITICAL_HIT
                || animation == AnimatePacket.Action.MAGIC_CRITICAL_HIT) {
            return;
        }

        PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(player, packet);
        player.getServer().getPluginManager().callEvent(animationEvent);
        if (animationEvent.isCancelled()) {
            return;
        }
        animation = animationEvent.getAnimationType();

        switch (animation) {
            case ROW_RIGHT, ROW_LEFT -> {
                if (player.riding instanceof EntityBoat boat) {
                    boat.onPaddle(animation, 1); // TODO: Paddle time got removed from packet. Needs debugging!!
                }
                return;
            }
        }

        final AnimatePacket pk = new AnimatePacket();
        pk.setAction(animationEvent.getAnimationType());
        pk.setTargetRuntimeID(player.getId());
        pk.setData(animationEvent.getData());
        pk.setSwingSource(animationEvent.getSwingSource());
        Server.broadcastPacket(player.getViewers().values(), pk);
    }
}