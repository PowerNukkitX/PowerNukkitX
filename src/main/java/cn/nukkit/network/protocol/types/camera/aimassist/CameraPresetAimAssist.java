package cn.nukkit.network.protocol.types.camera.aimassist;

import cn.nukkit.math.Vector2f;
import cn.nukkit.utils.OptionalValue;
import lombok.Data;

import java.util.Optional;

@Data
public class CameraPresetAimAssist {
    public OptionalValue<String> presetId;
    public OptionalValue<CameraAimAssist> targetMode;
    private OptionalValue<Vector2f> angle;
    private OptionalValue<Float> distance;
}
