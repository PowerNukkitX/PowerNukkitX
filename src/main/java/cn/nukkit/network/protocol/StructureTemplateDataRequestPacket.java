package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StructureTemplateDataRequestPacket extends DataPacket {
    public String name;
    public BlockVector3 position;
    public StructureSettings settings;
    public StructureTemplateRequestOperation operation;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        name = byteBuf.readString();
        position = byteBuf.readBlockVector3();
        settings = readStructureSettings(byteBuf);
        operation = StructureTemplateRequestOperation.values()[byteBuf.readByte()];
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(name);
        byteBuf.writeBlockVector3(position);
        writeStructureSettings(byteBuf, settings);
        byteBuf.writeByte(operation.ordinal());
    }

    private StructureSettings readStructureSettings(HandleByteBuf byteBuf) {
        var paletteName = byteBuf.readString();
        var isIgnoringEntities = byteBuf.readBoolean();
        var isIgnoringBlocks = byteBuf.readBoolean();
        var isNonTickingPlayersAndTickingAreasEnabled = byteBuf.readBoolean();
        var size = byteBuf.readBlockVector3();
        var offset = byteBuf.readBlockVector3();
        var lastEditedByEntityId = byteBuf.readVarLong();
        var rotation = byteBuf.readByte();
        var mirror = byteBuf.readByte();
        var animationMode = byteBuf.readByte();
        var animationSeconds = byteBuf.readFloatLE();
        var integrityValue = byteBuf.readFloatLE();
        var integritySeed = byteBuf.readIntLE();
        var pivot = byteBuf.readVector3f();
        return new StructureSettings(paletteName, isIgnoringEntities, isIgnoringBlocks, isNonTickingPlayersAndTickingAreasEnabled, size, offset,
                lastEditedByEntityId, Rotation.from(rotation), StructureMirror.from(mirror), StructureAnimationMode.from(animationMode),
                animationSeconds, integrityValue, integritySeed, pivot
        );
    }

    private void writeStructureSettings(HandleByteBuf byteBuf, StructureSettings settings) {
        byteBuf.writeString(settings.getPaletteName());
        byteBuf.writeBoolean(settings.isIgnoringEntities());
        byteBuf.writeBoolean(settings.isIgnoringBlocks());
        byteBuf.writeBoolean(settings.isNonTickingPlayersAndTickingAreasEnabled());
        byteBuf.writeBlockVector3(settings.getSize());
        byteBuf.writeBlockVector3(settings.getOffset());
        byteBuf.writeVarLong(settings.getLastEditedByEntityId());
        byteBuf.writeByte((byte) settings.getRotation().ordinal());
        byteBuf.writeByte((byte) settings.getMirror().ordinal());
        byteBuf.writeByte((byte) settings.getAnimationMode().ordinal());
        byteBuf.writeFloatLE(settings.getAnimationSeconds());
        byteBuf.writeFloatLE(settings.getIntegrityValue());
        byteBuf.writeIntLE(settings.getIntegritySeed());
        byteBuf.writeVector3f(settings.getPivot());
    }

    @Override
    public int pid() {
        return ProtocolInfo.STRUCTURE_DATA_REQUEST;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
