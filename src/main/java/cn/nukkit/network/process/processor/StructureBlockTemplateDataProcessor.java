package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.level.structure.Structure;
import cn.nukkit.level.structure.StructureAPI;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.packet.StructureTemplateDataRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.StructureTemplateDataResponsePacket;
import org.cloudburstmc.protocol.bedrock.data.structure.StructureTemplateResponseType;
import org.jetbrains.annotations.NotNull;

public class StructureBlockTemplateDataProcessor extends DataPacketProcessor<StructureTemplateDataRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull StructureTemplateDataRequestPacket pk) {
        StructureTemplateDataResponsePacket resp = new StructureTemplateDataResponsePacket();
        resp.setName(pk.getName());
        Structure structure = StructureAPI.load(pk.getName());

        if (structure == null) {
            resp.setSave(false);
            resp.setType(StructureTemplateResponseType.NONE);

            playerHandle.player.dataPacket(resp);
            return;
        }

        resp.setTag(NbtMap.EMPTY);
        resp.setSave(true);
        resp.setType(switch (pk.getOperation()) {
            case QUERY_SAVED_STRUCTURE -> StructureTemplateResponseType.QUERY;
            case EXPORT_FROM_SAVED_MODE -> StructureTemplateResponseType.EXPORT;
            case EXPORT_FROM_LOAD_MODE -> StructureTemplateResponseType.EXPORT;
            case IMPORT, NONE -> StructureTemplateResponseType.NONE;
        });

        playerHandle.player.dataPacket(resp);
    }

    @Override
    public Class<StructureTemplateDataRequestPacket> getPacketClass() {
        return StructureTemplateDataRequestPacket.class;
    }
}
