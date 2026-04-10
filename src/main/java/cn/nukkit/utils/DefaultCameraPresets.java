package cn.nukkit.utils;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraPreset;

import java.util.List;
import java.util.Set;

/**
 * @author Kaooot
 */
public class DefaultCameraPresets {

    public static final CameraPreset FIRST_PERSON = CameraPreset.builder()
            .name("minecraft:first_person")
            .build();
    public static final CameraPreset FIXED_BOOM = CameraPreset.builder()
            .name("minecraft:fixed_boom")
            .viewOffset(Vector2f.ZERO)
            .entityOffset(Vector3f.ZERO)
            .build();
    public static final CameraPreset FOLLOW_ORBIT = CameraPreset.builder()
            .name("minecraft:follow_orbit")
            .viewOffset(Vector2f.ZERO)
            .entityOffset(Vector3f.ZERO)
            .radius(10.0f)
            .build();
    public static final CameraPreset FREE = CameraPreset.builder()
            .name("minecraft:free")
            .pos(Vector3f.ZERO)
            .pitch(0f)
            .yaw(0f)
            .build();
    public static final CameraPreset THIRD_PERSON = CameraPreset.builder()
            .name("minecraft:third_person")
            .build();
    public static final CameraPreset THIRD_PERSON_FRONT = CameraPreset.builder()
            .name("minecraft:third_person_front")
            .build();


    private static final Set<CameraPreset> CAMERA_PRESETS = new ObjectOpenHashSet<>();

    static {
        CAMERA_PRESETS.addAll(
                List.of(
                        FIRST_PERSON, FIXED_BOOM, FOLLOW_ORBIT, FREE, THIRD_PERSON, THIRD_PERSON_FRONT
                )
        );
    }

    public static Set<CameraPreset> getAll() {
        return CAMERA_PRESETS;
    }
}