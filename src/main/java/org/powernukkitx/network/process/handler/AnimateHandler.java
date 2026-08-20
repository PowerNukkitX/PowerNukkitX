package org.powernukkitx.network.process.handler;

import lombok.extern.slf4j.Slf4j;
import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.event.player.PlayerAnimationEvent;
import org.powernukkitx.item.randomitem.RandomItem;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;

/**
 * @author Kaooot
 */
@Slf4j
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
       if (!isBroadcastable(animation)){
            return;
        }

        PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(player, packet);
        player.getServer().getPluginManager().callEvent(animationEvent);
        if (animationEvent.isCancelled()) {
            return;
        }
        animation = animationEvent.getAnimationType();

        // re-validate: a plugin may have changed the animation type via the event
        if (!isBroadcastable(animation)){
            log.warn("Plugin set an illegal animation type ({}) for player {}, skipping broadcast", animation, playerHandle.getUsername());
            return;
        }

        if (animation == AnimatePacket.Action.SWING) {
            player.interruptShieldBlockingForAttack();
        }

        final AnimatePacket pk = new AnimatePacket();
        pk.setAction(animation);
        pk.setTargetRuntimeID(player.getId());
        pk.setData(animationEvent.getData());
        pk.setSwingSource(animationEvent.getSwingSource());
        Server.broadcastPacket(player.getViewers().values(), pk);
    }

    private boolean isBroadcastable(AnimatePacket.Action action){
        return action != null
            && action != AnimatePacket.Action.WAKE_UP
            && action != AnimatePacket.Action.CRITICAL_HIT
            && action != AnimatePacket.Action.MAGIC_CRITICAL_HIT;
    }
}
