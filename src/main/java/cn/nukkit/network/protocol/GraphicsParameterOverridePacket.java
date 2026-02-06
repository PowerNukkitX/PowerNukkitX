package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GraphicsParameterOverridePacket extends DataPacket {

    private String biomeIdentifier;
    private ParameterType parameterType;
    private Map<Float, Vector3f> values;
    private boolean reset;
    /**
     * @since v924
     */
    private float floatValue;
    /**
     * @since v924
     */
    private Vector3f vec3Value;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        int size = byteBuf.readUnsignedVarInt();
        this.values = new HashMap<>(size);

        for (int i = 0; i < size; i++) {
            float key = byteBuf.readFloatLE();
            Vector3f vec = byteBuf.readVector3f();
            this.values.put(key, vec);
        }

        this.floatValue = byteBuf.readFloatLE();
        this.vec3Value = byteBuf.readVector3f();

        this.biomeIdentifier = byteBuf.readString();
        this.parameterType = ParameterType.values()[byteBuf.readUnsignedByte()];
        this.reset = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(values.size());
        for (var entry : values.entrySet()) {
            byteBuf.writeFloatLE(entry.getKey());
            byteBuf.writeVector3f(entry.getValue());
        }

        byteBuf.writeFloatLE(floatValue);
        byteBuf.writeVector3f(vec3Value);

        byteBuf.writeString(biomeIdentifier);
        byteBuf.writeByte(parameterType.ordinal());
        byteBuf.writeBoolean(reset);
    }


    @Override
    public int pid() {
        return ProtocolInfo.GRAPHICS_PARAMETER_OVERRIDE_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public enum ParameterType {
        SKY_ZENITH_COLOR,
        SKY_HORIZON_COLOR,
        HORIZON_BLEND_MIN,
        HORIZON_BLEND_MAX,
        HORIZON_BLEND_START,
        HORIZON_BLEND_MIE_START,
        RAYLEIGH_STRENGTH,
        SUN_MIE_STRENGTH,
        MOON_MIE_STRENGTH,
        SUN_GLARE_SHAPE,
        CHLOROPHYLL,
        CDOM,
        SUSPENDED_SEDIMENT,
        WAVES_DEPTH,
        WAVES_FREQUENCY,
        WAVES_FREQUENCY_SCALING,
        WAVES_SPEED,
        WAVES_SPEED_SCALING,
        WAVES_SHAPE,
        WAVES_OCTAVES,
        WAVES_MIX,
        WAVES_PULL,
        WAVES_DIRECTION_INCREMENT,
        MIDTONES_CONTRAST,
        HIGHLIGHTS_CONTRAST,
        SHADOWS_CONTRAST,
    }
}
