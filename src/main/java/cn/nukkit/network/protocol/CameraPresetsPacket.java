package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.camera.data.CameraPreset;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Since("1.19.70-r1")
@PowerNukkitXOnly
@Getter
@Setter
public class CameraPresetsPacket extends DataPacket {
    private final List<CameraPreset> presets = new ObjectArrayList<>();

    @Override
    public byte pid() {
        return ProtocolInfo.CAMERA_PRESETS_PACKET;
    }

    @Override
    public void decode() {}

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
        putNotNull(preset.getPos(), (v) -> putLFloat(v.x()));
        putNotNull(preset.getPos(), (v) -> putLFloat(v.y()));
        putNotNull(preset.getPos(), (v) -> putLFloat(v.z()));
        putNotNull(preset.getPitch(), this::putLFloat);
        putNotNull(preset.getYaw(), this::putLFloat);
        putNotNull(preset.getListener(), (l) -> putByte((byte) l.ordinal()));
        putOptional(preset.getPlayEffect(), this::putBoolean);
    }
}
