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

/**
 * @author Kaooot
 */

@Slf4j
public class StructureTemplateDataRequestHandler implements PacketHandler<StructureTemplateDataRequestPacket> {

    @Override
    public void handle(StructureTemplateDataRequestPacket packet, PlayerSessionHolder holder, Server server) {
        final Player player = holder.getPlayer();
        if (player == null || !player.spawned || !player.isOp() || !player.isCreative()) {
            return;
        }

        final String structureName = packet.getStructureName();
        if (structureName == null || structureName.isBlank()) return;

        StructureAPI.loadAsync(structureName)
            .thenApply(structure -> structure == null ? null : createResponse(packet, structure))
            .whenComplete((responsePacket, error) -> {
                if (error != null) {
                    log.debug("Failed to load structure {} for {}", structureName, player.getName(), error);
                    return;
                }
                if (responsePacket == null) return;
                server.getScheduler().scheduleTask(InternalPlugin.INSTANCE, () -> {
                    if (player.isOnline()) player.sendPacket(responsePacket);
                });
            });
    }

    private static StructureTemplateDataResponsePacket createResponse(StructureTemplateDataRequestPacket packet, Structure structure) {
        final StructureTemplateDataResponsePacket responsePacket = new StructureTemplateDataResponsePacket();
        responsePacket.setStructureName(packet.getStructureName());
        responsePacket.setSave(true);

        responsePacket.setStructureNBT(structure.toNBT().toNetwork());
        responsePacket.setResponseType(switch (packet.getRequestedOperation()) {
            case QUERY_SAVED_STRUCTURE -> StructureTemplateResponseType.QUERY;
            case EXPORT_FROM_SAVED_MODE, EXPORT_FROM_LOAD_MODE -> StructureTemplateResponseType.EXPORT;
            default -> StructureTemplateResponseType.NONE;
        });
        return responsePacket;
    }
}
