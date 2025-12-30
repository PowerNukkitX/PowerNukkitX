package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.level.structure.Structure;
import cn.nukkit.level.structure.StructureAPI;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.StructureTemplateDataRequestPacket;
import cn.nukkit.network.protocol.StructureTemplateDataResponsePacket;
import cn.nukkit.network.protocol.types.StructureTemplateRequestOperation;
import cn.nukkit.network.protocol.types.StructureTemplateResponseType;
import org.jetbrains.annotations.NotNull;

public class StructureBlockTemplateDataProcessor extends DataPacketProcessor<StructureTemplateDataRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull StructureTemplateDataRequestPacket pk) {
        StructureTemplateDataResponsePacket resp = new StructureTemplateDataResponsePacket();
        resp.name = pk.getName();
        Structure structure = StructureAPI.load(pk.getName());

        if (structure == null) {
            resp.success = false;
            resp.responseType = StructureTemplateResponseType.FAILURE;

            playerHandle.player.dataPacket(resp);
            return;
        }

        resp.data = structure.toNBT();
        resp.success = true;
        resp.responseType = switch(pk.operation) {
            case QUERY_SAVED_STRUCTURE -> StructureTemplateResponseType.QUERY;
            case EXPORT_FROM_SAVE_MODE -> StructureTemplateResponseType.EXPORT;
            case EXPORT_FROM_LOAD_MODE -> StructureTemplateResponseType.EXPORT;
            case NONE -> StructureTemplateResponseType.FAILURE;
        };

        playerHandle.player.dataPacket(resp);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.STRUCTURE_DATA_REQUEST;
    }
}
