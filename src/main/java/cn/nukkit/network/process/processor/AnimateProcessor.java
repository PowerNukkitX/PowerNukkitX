package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.event.player.PlayerAnimationEvent;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;
import org.jetbrains.annotations.NotNull;

public class AnimateProcessor extends DataPacketProcessor<AnimatePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull AnimatePacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        AnimatePacket.Action animation = pk.getAction();

        // prevent client send illegal packet to server and broadcast to other client and make other client crash
        if (animation == null // illegal action id
                || animation == AnimatePacket.Action.WAKE_UP // these actions are only for server to client
                || animation == AnimatePacket.Action.CRITICAL_HIT
                || animation == AnimatePacket.Action.MAGIC_CRITICAL_HIT) {
            return;
        }

        org.cloudburstmc.protocol.bedrock.packet.AnimatePacket legacyPacket = new org.cloudburstmc.protocol.bedrock.packet.AnimatePacket();
        legacyPacket.setAction(org.cloudburstmc.protocol.bedrock.packet.AnimatePacket.Action.valueOf(animation.name()));
        legacyPacket.setData(pk.getData());
        legacyPacket.setSwingSource(org.cloudburstmc.protocol.bedrock.packet.AnimatePacket.SwingSource.valueOf(pk.getSwingSource().name()));

        PlayerAnimationEvent animationEvent = new PlayerAnimationEvent(player, legacyPacket);
        player.getServer().getPluginManager().callEvent(animationEvent);
        if (animationEvent.isCancelled()) {
            return;
        }
        var oldAnimation = animationEvent.getAnimationType();
        animation = AnimatePacket.Action.valueOf(oldAnimation.name());

        switch (oldAnimation) {
            case ROW_RIGHT, ROW_LEFT -> {
                if (player.riding instanceof EntityBoat boat) {
                    boat.onPaddle(oldAnimation, 1); // TODO: Paddle time got removed from packet. Needs debugging!!
                }
                return;
            }
        }

        if (oldAnimation == org.cloudburstmc.protocol.bedrock.packet.AnimatePacket.Action.SWING_ARM) {
            player.setItemCoolDown(PlayerHandle.getNoShieldDelay(), "shield");
        }

        pk = new AnimatePacket();
        pk.setRuntimeEntityId(player.getId());
        pk.setAction(animation);
        pk.setSwingSource(AnimatePacket.SwingSource.valueOf(animationEvent.getSwingSource().name()));
        pk.setData(animationEvent.getData());
        for (var viewer : player.getViewers().values()) {
            viewer.dataPacket(pk);
        }
    }

    @Override
    public Class<AnimatePacket> getPacketClass() {
        return AnimatePacket.class;
    }
}
