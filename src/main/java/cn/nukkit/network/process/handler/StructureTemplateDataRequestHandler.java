package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.level.structure.Structure;
import cn.nukkit.level.structure.StructureAPI;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.structure.StructureTemplateResponseType;
import org.cloudburstmc.protocol.bedrock.packet.StructureTemplateDataRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.StructureTemplateDataResponsePacket;

/**
 * @author Kaooot
 */
public class StructureTemplateDataRequestHandler implements PacketHandler<StructureTemplateDataRequestPacket> {

    @Override
    public void handle(StructureTemplateDataRequestPacket packet, PlayerSessionHolder holder, Server server) {
        final String structureName = packet.getStructureName();
        final StructureTemplateDataResponsePacket responsePacket = new StructureTemplateDataResponsePacket();
        responsePacket.setStructureName(structureName);

        final Structure structure = StructureAPI.load(structureName);

        if (structure == null) {
            responsePacket.setResponseType(StructureTemplateResponseType.NONE);
            holder.getPlayer().sendPacket(responsePacket);
            return;
        }

        responsePacket.setStructureNBT(structure.toNBT());
        responsePacket.setSave(true);
        responsePacket.setResponseType(switch (packet.getRequestedOperation()) {
            case QUERY_SAVED_STRUCTURE -> StructureTemplateResponseType.QUERY;
            case EXPORT_FROM_SAVED_MODE, EXPORT_FROM_LOAD_MODE -> StructureTemplateResponseType.EXPORT;
            default -> StructureTemplateResponseType.NONE;
        });

        holder.getPlayer().sendPacket(responsePacket);
    }
}