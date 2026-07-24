package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerAnimationEvent;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
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

        if (animation == AnimatePacket.Action.SWING) {
            player.interruptShieldBlockingForAttack();
        }

        final AnimatePacket pk = new AnimatePacket();
        pk.setAction(animationEvent.getAnimationType());
        pk.setTargetRuntimeID(player.getId());
        pk.setData(animationEvent.getData());
        pk.setSwingSource(animationEvent.getSwingSource());
        Server.broadcastPacket(player.getViewers().values(), pk); // TODO: fix it
    }
}
