package cn.nukkit.network.protocol;

import cn.nukkit.blockproperty.value.StructureBlockType;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.types.*;
import lombok.ToString;

@ToString
public class StructureBlockUpdatePacket extends DataPacket {
    public BlockVector3 blockPosition;
    public StructureEditorData editorData;
    public boolean powered;
    public boolean waterlogged;

    @Override
    public byte pid() {
        return ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET;
    }

    @Override
    public void decode() {
        this.blockPosition = this.getBlockVector3();
        this.editorData = readEditorData();
        this.powered = this.getBoolean();
        this.waterlogged = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(blockPosition);
        this.writeEditorData(editorData);
        this.putBoolean(powered);
        this.putBoolean(waterlogged);
    }

    private StructureEditorData readEditorData() {
        var name = this.getString();
        var dataField = this.getString();
        var isIncludingPlayers = this.getBoolean();
        var isBoundingBoxVisible = this.getBoolean();
        var type = this.getVarInt();
        var structureSettings = readStructureSettings();
        var redstoneSaveMode = this.getVarInt();
        return new StructureEditorData(name, dataField, isIncludingPlayers, isBoundingBoxVisible, StructureBlockType.from(type), structureSettings,
                StructureRedstoneSaveMode.from(redstoneSaveMode));
    }

    private StructureSettings readStructureSettings() {
        var paletteName = this.getString();
        var isIgnoringEntities = this.getBoolean();
        var isIgnoringBlocks = this.getBoolean();
        var isNonTickingPlayersAndTickingAreasEnabled = this.getBoolean();
        var size = this.getBlockVector3();
        var offset = this.getBlockVector3();
        var lastEditedByEntityId = this.getVarLong();
        var rotation = this.getByte();
        var mirror = this.getByte();
        var animationMode = this.getByte();
        var animationSeconds = this.getLFloat();
        var integrityValue = this.getLFloat();
        var integritySeed = this.getLInt();
        var pivot = this.getVector3f();
        return new StructureSettings(paletteName, isIgnoringEntities, isIgnoringBlocks, isNonTickingPlayersAndTickingAreasEnabled, size, offset,
                lastEditedByEntityId, StructureRotation.from(rotation), StructureMirror.from(mirror), StructureAnimationMode.from(animationMode),
                animationSeconds, integrityValue, integritySeed, pivot
        );
    }

    private void writeEditorData(StructureEditorData editorData) {
        this.putString(editorData.getName());
        this.putString(editorData.getDataField());
        this.putBoolean(editorData.isIncludingPlayers());
        this.putBoolean(editorData.isBoundingBoxVisible());
        this.putVarInt(editorData.getType().ordinal());
        writeStructureSettings(editorData.getSettings());
        this.putVarInt(editorData.getRedstoneSaveMode().ordinal());
    }

    private void writeStructureSettings(StructureSettings settings) {
        this.putString(settings.getPaletteName());
        this.putBoolean(settings.isIgnoringEntities());
        this.putBoolean(settings.isIgnoringBlocks());
        this.putBoolean(settings.isNonTickingPlayersAndTickingAreasEnabled());
        this.putBlockVector3(settings.getSize());
        this.putBlockVector3(settings.getOffset());
        this.putVarLong(settings.getLastEditedByEntityId());
        this.putByte((byte) settings.getRotation().ordinal());
        this.putByte((byte) settings.getMirror().ordinal());
        this.putByte((byte) settings.getAnimationMode().ordinal());
        this.putLFloat(settings.getAnimationSeconds());
        this.putLFloat(settings.getIntegrityValue());
        this.putLInt(settings.getIntegritySeed());
        this.putVector3f(settings.getPivot());
    }
}
