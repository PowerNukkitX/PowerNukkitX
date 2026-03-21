package cn.nukkit.network.protocol;

import cn.nukkit.camera.data.EaseType;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.attribute.AttributeLayerSyncData;
import cn.nukkit.network.protocol.types.attribute.*;
import cn.nukkit.network.protocol.types.attribute.data.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientboundAttributeLayerSyncPacket extends DataPacket {

    private AttributeLayerSyncData data;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        final AttributeLayerSyncData.Type type = AttributeLayerSyncData.Type.from(byteBuf.readUnsignedVarInt());
        data = switch (type) {
            case UPDATE_ATTRIBUTE_LAYERS -> readUpdateAttributeLayersData(byteBuf);
            case UPDATE_ATTRIBUTE_LAYER_SETTINGS ->  readUpdateAttributeLayerSettingsData(byteBuf);
            case UPDATE_ENVIRONMENT_ATTRIBUTES -> readUpdateEnvironmentAttributesData(byteBuf);
            case REMOVE_ENVIRONMENT_ATTRIBUTES -> readRemoveEnvironmentAttributesData(byteBuf);
        };
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(data.getType().ordinal());
        switch (data.getType()) {
            case UPDATE_ATTRIBUTE_LAYERS:
                writeUpdateAttributeLayersData(byteBuf, (UpdateAttributeLayersData) data);
                break;
            case UPDATE_ATTRIBUTE_LAYER_SETTINGS:
                writeUpdateAttributeLayerSettingsData(byteBuf, (UpdateAttributeLayerSettingsData) data);
                break;
            case UPDATE_ENVIRONMENT_ATTRIBUTES:
                writeUpdateEnvironmentAttributesData(byteBuf, (UpdateEnvironmentAttributesData) data);
                break;
            case REMOVE_ENVIRONMENT_ATTRIBUTES:
                writeRemoveEnvironmentAttributesData(byteBuf, (RemoveEnvironmentAttributesData) data);
                break;
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_ATTRIBUTE_LAYER_SYNC_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    private void writeUpdateAttributeLayersData(HandleByteBuf byteBuf, UpdateAttributeLayersData data) {
        byteBuf.writeArray(data.getAttributeLayers(), this::writeAttributeLayerData);
    }

    private UpdateAttributeLayersData readUpdateAttributeLayersData(HandleByteBuf byteBuf) {
        final UpdateAttributeLayersData data = new UpdateAttributeLayersData();
        byteBuf.readArray(data.getAttributeLayers(), this::readAttributeLayerData);
        return data;
    }

    private void writeUpdateAttributeLayerSettingsData(HandleByteBuf byteBuf, UpdateAttributeLayerSettingsData data) {
        byteBuf.writeString(data.getAttributeLayerName());
        byteBuf.writeVarInt(data.getAttributeLayerDimensionId());
        writeAttributeLayerSettings(byteBuf, data.getAttributesLayerSettings());
    }

    private UpdateAttributeLayerSettingsData readUpdateAttributeLayerSettingsData(HandleByteBuf byteBuf) {
        final UpdateAttributeLayerSettingsData data = new UpdateAttributeLayerSettingsData();
        data.setAttributeLayerName(byteBuf.readString());
        data.setAttributeLayerDimensionId(byteBuf.readVarInt());
        data.setAttributesLayerSettings(readAttributeLayerSettings(byteBuf));
        return data;
    }

    private void writeUpdateEnvironmentAttributesData(HandleByteBuf byteBuf, UpdateEnvironmentAttributesData data) {
        byteBuf.writeString(data.getAttributeLayerName());
        byteBuf.writeVarInt(data.getAttributeLayerDimensionId());
        byteBuf.writeArray(data.getAttributes(), this::writeEnvironmentAttributeData);
    }

    private UpdateEnvironmentAttributesData readUpdateEnvironmentAttributesData(HandleByteBuf byteBuf) {
        final UpdateEnvironmentAttributesData data = new UpdateEnvironmentAttributesData();
        data.setAttributeLayerName(byteBuf.readString());
        data.setAttributeLayerDimensionId(byteBuf.readVarInt());
        byteBuf.readArray(data.getAttributes(), this::readEnvironmentAttributeData);
        return data;
    }

    private void writeRemoveEnvironmentAttributesData(HandleByteBuf byteBuf, RemoveEnvironmentAttributesData data) {
        byteBuf.writeString(data.getAttributeLayerName());
        byteBuf.writeVarInt(data.getAttributeLayerDimensionId());
        byteBuf.writeArray(data.getAttributes(), byteBuf::writeString);
    }

    private RemoveEnvironmentAttributesData readRemoveEnvironmentAttributesData(HandleByteBuf byteBuf) {
        final RemoveEnvironmentAttributesData data = new RemoveEnvironmentAttributesData();
        data.setAttributeLayerName(byteBuf.readString());
        data.setAttributeLayerDimensionId(byteBuf.readVarInt());
        byteBuf.readArray(data.getAttributes(), HandleByteBuf::readString);
        return data;
    }

    private void writeAttributeLayerData(HandleByteBuf byteBuf, AttributeLayerData data) {
        byteBuf.writeString(data.getName());
        byteBuf.writeVarInt(data.getDimensionId());
        writeAttributeLayerSettings(byteBuf, data.getSettings());
        byteBuf.writeArray(data.getAttributes(), this::writeEnvironmentAttributeData);
    }

    private AttributeLayerData readAttributeLayerData(HandleByteBuf byteBuf) {
        final AttributeLayerData data = new AttributeLayerData();
        data.setName(byteBuf.readString());
        data.setDimensionId(byteBuf.readVarInt());
        data.setSettings(readAttributeLayerSettings(byteBuf));
        byteBuf.readArray(data.getAttributes(), this::readEnvironmentAttributeData);
        return data;
    }

    private void writeAttributeLayerSettings(HandleByteBuf byteBuf, AttributeLayerSettings settings) {
        byteBuf.writeIntLE(settings.getPriority());
        byteBuf.writeUnsignedVarInt(settings.getWeight().getType().ordinal());
        if (settings.getWeight().getType() == AttributeLayerSettings.WeightData.Type.FLOAT) {
            byteBuf.writeFloatLE(settings.getWeight().getAsFloat());
        } else {
            byteBuf.writeString(settings.getWeight().getAsString());
        }
        byteBuf.writeBoolean(settings.isEnabled());
        byteBuf.writeBoolean(settings.isTransitionsPaused());
    }

    private AttributeLayerSettings readAttributeLayerSettings(HandleByteBuf byteBuf) {
        final AttributeLayerSettings settings = new AttributeLayerSettings();
        settings.setPriority(byteBuf.readIntLE());
        final AttributeLayerSettings.WeightData.Type weightType =
                AttributeLayerSettings.WeightData.Type.from(byteBuf.readUnsignedVarInt());
        if (weightType == AttributeLayerSettings.WeightData.Type.FLOAT) {
            settings.setWeight(new AttributeLayerSettings.WeightData(weightType, byteBuf.readFloatLE()));
        } else {
            settings.setWeight(new AttributeLayerSettings.WeightData(weightType, byteBuf.readString()));
        }
        settings.setEnabled(byteBuf.readBoolean());
        settings.setTransitionsPaused(byteBuf.readBoolean());
        return settings;
    }

    private void writeEnvironmentAttributeData(HandleByteBuf byteBuf, EnvironmentAttributeData data) {
        byteBuf.writeString(data.getAttributeName());
        byteBuf.writeNotNull(data.getFromAttribute(), attr -> writeAttributeData(byteBuf, attr));
        writeAttributeData(byteBuf, data.getAttribute());
        byteBuf.writeNotNull(data.getToAttribute(), attr -> writeAttributeData(byteBuf, attr));
        byteBuf.writeIntLE(data.getCurrentTransitionTicks());
        byteBuf.writeIntLE(data.getTotalTransitionTicks());
        byteBuf.writeString(data.getEaseType().getType());
    }

    private EnvironmentAttributeData readEnvironmentAttributeData(HandleByteBuf byteBuf) {
        final EnvironmentAttributeData data = new EnvironmentAttributeData();
        data.setAttributeName(byteBuf.readString());
        data.setFromAttribute(byteBuf.readOptional(null, () -> readAttributeData(byteBuf)));
        data.setAttribute(readAttributeData(byteBuf));
        data.setToAttribute(byteBuf.readOptional(null, () -> readAttributeData(byteBuf)));
        data.setCurrentTransitionTicks(byteBuf.readIntLE());
        data.setTotalTransitionTicks(byteBuf.readIntLE());
        data.setEaseType(EaseType.valueOf(byteBuf.readString()));
        return data;
    }

    private void writeAttributeData(HandleByteBuf byteBuf, AttributeData data) {
        byteBuf.writeUnsignedVarInt(data.getType().ordinal());
        switch (data.getType()) {
            case BOOL:
                writeBoolAttributeData(byteBuf, (BoolAttributeData) data);
                break;
            case FLOAT:
                writeFloatAttributeData(byteBuf, (FloatAttributeData) data);
                break;
            case COLOR:
                writeColorAttributeData(byteBuf, (ColorAttributeData) data);
                break;
        }
    }

    private AttributeData readAttributeData(HandleByteBuf byteBuf) {
        final AttributeData.Type type = AttributeData.Type.from(byteBuf.readUnsignedVarInt());
        return switch (type) {
            case BOOL -> readBoolAttributeData(byteBuf);
            case FLOAT -> readFloatAttributeData(byteBuf);
            case COLOR -> readColorAttributeData(byteBuf);
        };
    }

    private void writeBoolAttributeData(HandleByteBuf byteBuf, BoolAttributeData data) {
        byteBuf.writeBoolean(data.isValue());
        byteBuf.writeUnsignedVarInt(data.getOperation().ordinal());
    }

    private BoolAttributeData readBoolAttributeData(HandleByteBuf byteBuf) {
        final BoolAttributeData data = new BoolAttributeData();
        data.setValue(byteBuf.readBoolean());
        data.setOperation(BoolAttributeData.Operation.from(byteBuf.readUnsignedVarInt()));
        return data;
    }

    private void writeFloatAttributeData(HandleByteBuf byteBuf, FloatAttributeData data) {
        byteBuf.writeFloatLE(data.getValue());
        byteBuf.writeUnsignedVarInt(data.getOperation().ordinal());
        byteBuf.writeNotNull(data.getConstraintMin(), byteBuf::writeFloatLE);
        byteBuf.writeNotNull(data.getConstraintMax(), byteBuf::writeFloatLE);
    }

    private FloatAttributeData readFloatAttributeData(HandleByteBuf byteBuf) {
        final FloatAttributeData data = new FloatAttributeData();
        data.setValue(byteBuf.readFloatLE());
        data.setOperation(FloatAttributeData.Operation.from(byteBuf.readUnsignedVarInt()));
        data.setConstraintMin(byteBuf.readOptional(null, byteBuf::readFloatLE));
        data.setConstraintMax(byteBuf.readOptional(null, byteBuf::readFloatLE));
        return data;
    }

    private void writeColorAttributeData(HandleByteBuf byteBuf, ColorAttributeData data) {
        byteBuf.writeIntLE(data.getColor());
        byteBuf.writeUnsignedVarInt(data.getOperation().ordinal());
    }

    private ColorAttributeData readColorAttributeData(HandleByteBuf byteBuf) {
        final ColorAttributeData data = new ColorAttributeData();
        data.setColor(byteBuf.readIntLE());
        data.setOperation(ColorAttributeData.Operation.from(byteBuf.readUnsignedVarInt()));
        return data;
    }
}
