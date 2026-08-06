package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEventPacket;

/**
 * @author Kaooot
 */
public class LevelSoundEventHandler implements PacketHandler<LevelSoundEventPacket> {

    private static final int MAX_IDENTIFIER_LENGTH = 64;

    @Override
    public void handle(LevelSoundEventPacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();
        if (!player.spawned || !player.isAlive() || player.isSpectator()) {
            return;
        }

        final PlayerHandle playerHandle = holder.getPlayerHandle();
        if (!playerHandle.packetRateLimiter.tryWorldInteraction()) {
            return;
        }

        SoundEvent soundEvent = packet.getSoundEvent();
        if (soundEvent == null || soundEvent == SoundEvent.UNDEFINED) {
            return;
        }

        String identifier = packet.getActorIdentifier();
        if (identifier == null) {
            identifier = "";
        } else if (identifier.length() > MAX_IDENTIFIER_LENGTH) {
            return;
        }

        final LevelSoundEventPacket pk = new LevelSoundEventPacket();
        pk.setSoundEvent(soundEvent);
        pk.setPosition(Vector3f.from(player.x, player.y, player.z));
        pk.setData(packet.getData());
        pk.setActorIdentifier(identifier);
        pk.setBaby(packet.isBaby());
        pk.setGlobal(false);
        pk.setActorUniqueId(player.getId());

        player.level.addChunkPacket(player.getChunkX(), player.getChunkZ(), pk);
    }
}
