package cn.nukkit.network.protocol;

import cn.nukkit.camera.data.CameraPreset;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraAimAssist;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraAimAssistPreset;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraPresetAimAssist;
import cn.nukkit.utils.OptionalValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;

import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CameraPresetsPacket extends DataPacket {
    public final List<CameraPreset> presets = new ObjectArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(presets.size());
        for (var p : presets) {
            writePreset(byteBuf, p);
        }
    }

    public void writePreset(HandleByteBuf byteBuf, CameraPreset preset) {
        byteBuf.writeString(preset.getIdentifier());
        byteBuf.writeString(preset.getInheritFrom());
        byteBuf.writeNotNull(preset.getPos(), (v) -> byteBuf.writeFloatLE(v.getX()));
        byteBuf.writeNotNull(preset.getPos(), (v) -> byteBuf.writeFloatLE(v.getY()));
        byteBuf.writeNotNull(preset.getPos(), (v) -> byteBuf.writeFloatLE(v.getZ()));
        byteBuf.writeNotNull(preset.getPitch(), byteBuf::writeFloatLE);
        byteBuf.writeNotNull(preset.getYaw(), byteBuf::writeFloatLE);
        byteBuf.writeNotNull(preset.getRotationSpeed(), byteBuf::writeFloatLE);
        byteBuf.writeOptional(preset.getSnapToTarget(), byteBuf::writeBoolean);
        byteBuf.writeNotNull(preset.getHorizontalRotationLimit(), byteBuf::writeVector2f);
        byteBuf.writeNotNull(preset.getVerticalRotationLimit(), byteBuf::writeVector2f);
        byteBuf.writeOptional(preset.getContinueTargeting(), byteBuf::writeBoolean);
        byteBuf.writeOptional(preset.getBlockListeningRadius(), byteBuf::writeFloatLE);
        byteBuf.writeNotNull(preset.getViewOffset(), byteBuf::writeVector2f);
        byteBuf.writeNotNull(preset.getEntityOffset(), byteBuf::writeVector3f);
        byteBuf.writeNotNull(preset.getRadius(), byteBuf::writeFloatLE);
        byteBuf.writeNotNull(preset.getYawLimitMin(), byteBuf::writeFloatLE);
        byteBuf.writeNotNull(preset.getYawLimitMax(), byteBuf::writeFloatLE);
        byteBuf.writeNotNull(preset.getListener(), (l) -> byteBuf.writeByte((byte) l.ordinal()));
        byteBuf.writeOptional(preset.getPlayEffect(), byteBuf::writeBoolean);
        byteBuf.writeOptional(preset.getAlignTargetAndCameraForward(), byteBuf::writeBoolean);
        writeCameraPresetAimAssist(byteBuf, preset.getAimAssist());
        byteBuf.writeBoolean(false);
    }

    public void writeCameraPresetAimAssist(HandleByteBuf byteBuf, OptionalValue<CameraPresetAimAssist> data) {
        boolean present = data.isPresent();
        byteBuf.writeBoolean(present);
        if (present) {
            CameraPresetAimAssist value = data.get();
            byteBuf.writeOptional(value.getPresetId(), byteBuf::writeString);
            writeTargetMode(byteBuf, value.getTargetMode());
            byteBuf.writeOptional(value.getAngle(), byteBuf::writeVector2f);
            byteBuf.writeOptional(value.getDistance(), byteBuf::writeFloatLE);
        }
    }

    private void writeTargetMode(HandleByteBuf byteBuf, OptionalValue<CameraAimAssist> data) {
        boolean present = data.isPresent();
        byteBuf.writeBoolean(present);
        if (present) {
            byteBuf.writeIntLE(data.get().ordinal());
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.CAMERA_PRESETS_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
