package cn.nukkit.network.protocol;

import cn.nukkit.block.property.enums.StructureBlockType;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.StructureAnimationMode;
import cn.nukkit.network.protocol.types.StructureEditorData;
import cn.nukkit.network.protocol.types.StructureMirror;
import cn.nukkit.network.protocol.types.StructureRedstoneSaveMode;
import cn.nukkit.network.protocol.types.StructureRotation;
import cn.nukkit.network.protocol.types.StructureSettings;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StructureBlockUpdatePacket extends DataPacket {
    public BlockVector3 blockPosition;
    public StructureEditorData editorData;
    public boolean powered;
    public boolean waterlogged;

    @Override
    public int pid() {
        return ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.blockPosition = byteBuf.readBlockVector3();
        this.editorData = readEditorData(byteBuf);
        this.powered = byteBuf.readBoolean();
        this.waterlogged = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBlockVector3(blockPosition);
        this.writeEditorData(byteBuf, editorData);
        byteBuf.writeBoolean(powered);
        byteBuf.writeBoolean(waterlogged);
    }

    private StructureEditorData readEditorData(HandleByteBuf byteBuf) {
        var name = byteBuf.readString();
        var dataField = byteBuf.readString();
        var isIncludingPlayers = byteBuf.readBoolean();
        var isBoundingBoxVisible = byteBuf.readBoolean();
        var type = byteBuf.readVarInt();
        var structureSettings = readStructureSettings(byteBuf);
        var redstoneSaveMode = byteBuf.readVarInt();
        return new StructureEditorData(name, dataField, isIncludingPlayers, isBoundingBoxVisible, StructureBlockType.from(type), structureSettings,
                StructureRedstoneSaveMode.from(redstoneSaveMode));
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
                lastEditedByEntityId, StructureRotation.from(rotation), StructureMirror.from(mirror), StructureAnimationMode.from(animationMode),
                animationSeconds, integrityValue, integritySeed, pivot
        );
    }

    private void writeEditorData(HandleByteBuf byteBuf, StructureEditorData editorData) {
        byteBuf.writeString(editorData.getName());
        byteBuf.writeString(editorData.getDataField());
        byteBuf.writeBoolean(editorData.isIncludingPlayers());
        byteBuf.writeBoolean(editorData.isBoundingBoxVisible());
        byteBuf.writeVarInt(editorData.getType().ordinal());
        writeStructureSettings(byteBuf, editorData.getSettings());
        byteBuf.writeVarInt(editorData.getRedstoneSaveMode().ordinal());
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

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
