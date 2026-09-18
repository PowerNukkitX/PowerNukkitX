package org.powernukkitx.network.process.handler;

import lombok.extern.slf4j.Slf4j;
import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.level.structure.Structure;
import org.powernukkitx.level.structure.StructureAPI;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.structure.StructureTemplateResponseType;
import org.cloudburstmc.protocol.bedrock.packet.StructureTemplateDataRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.StructureTemplateDataResponsePacket;
import org.powernukkitx.plugin.InternalPlugin;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kaooot
 */

@Slf4j
public class StructureTemplateDataRequestHandler implements PacketHandler<StructureTemplateDataRequestPacket> {
    private static final Set<Long> PENDING_REQUESTS = ConcurrentHashMap.newKeySet();

    @Override
    public void handle(StructureTemplateDataRequestPacket packet, PlayerSessionHolder holder, Server server) {
        final Player player = holder.getPlayer();
        if (!player.spawned || !player.isOp() || !player.isCreative()) {
            return;
        }

        final long playerId = player.runtimeId();
        if (!PENDING_REQUESTS.add(playerId)) {
            return;
        }

        final String structureName = packet.getStructureName();
        StructureAPI.loadAsync(structureName)
            .thenApply(structure -> createResponse(packet, structure))
            .whenComplete((responsePacket, error) -> server.getScheduler().scheduleTask(InternalPlugin.INSTANCE, () -> {
                PENDING_REQUESTS.remove(playerId);
                if (error != null) {
                    log.debug("Failed to load structure {} for {}", structureName, player.getName(), error);
                    return;
                }
                if (player.isOnline()) {
                    player.sendPacket(responsePacket);
                }
            }));
    }

    private static StructureTemplateDataResponsePacket createResponse(StructureTemplateDataRequestPacket packet, Structure structure) {
        final StructureTemplateDataResponsePacket responsePacket = new StructureTemplateDataResponsePacket();
        responsePacket.setStructureName(packet.getStructureName());

        if (structure == null) {
            responsePacket.setResponseType(StructureTemplateResponseType.NONE);
            return responsePacket;
        }

        responsePacket.setStructureNBT(structure.toNBT().toNetwork());
        responsePacket.setSave(true);
        responsePacket.setResponseType(switch (packet.getRequestedOperation()) {
            case QUERY_SAVED_STRUCTURE -> StructureTemplateResponseType.QUERY;
            case EXPORT_FROM_SAVED_MODE, EXPORT_FROM_LOAD_MODE -> StructureTemplateResponseType.EXPORT;
            default -> StructureTemplateResponseType.NONE;
        });
        return responsePacket;
    }
}
