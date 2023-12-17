package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.camera.data.CameraPreset;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CameraPresetsPacket extends DataPacket {
    private final List<CameraPreset> presets = new ObjectArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.CAMERA_PRESETS_PACKET;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        putUnsignedVarInt(presets.size());
        for (var p : presets) {
            writePreset(p);
        }
    }

    public void writePreset(CameraPreset preset) {
        putString(preset.getIdentifier());
        putString(preset.getInheritFrom());
        putNotNull(preset.getPos(), (v) -> putLFloat(v.getX()));
        putNotNull(preset.getPos(), (v) -> putLFloat(v.getY()));
        putNotNull(preset.getPos(), (v) -> putLFloat(v.getZ()));
        putNotNull(preset.getPitch(), this::putLFloat);
        putNotNull(preset.getYaw(), this::putLFloat);
        putNotNull(preset.getListener(), (l) -> putByte((byte) l.ordinal()));
        putOptional(preset.getPlayEffect(), this::putBoolean);
    }
}
