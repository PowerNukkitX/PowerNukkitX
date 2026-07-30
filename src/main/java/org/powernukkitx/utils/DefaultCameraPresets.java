package org.powernukkitx.utils;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraPreset;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.NamedDefinition;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;
import org.powernukkitx.camera.CustomCameraDefinition;
import org.powernukkitx.camera.CustomCameraRegistry;

import java.util.List;

/**
 * Contains the built-in camera presets and the global camera preset registry.
 *
 * @author Kaooot
 */
public final class DefaultCameraPresets {

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

    private static final CustomCameraRegistry REGISTRY = new CustomCameraRegistry();

    static {
        REGISTRY.register(FIRST_PERSON);
        REGISTRY.register(FIXED_BOOM);
        REGISTRY.register(FOLLOW_ORBIT);
        REGISTRY.register(FREE);
        REGISTRY.register(THIRD_PERSON);
        REGISTRY.register(THIRD_PERSON_FRONT);
    }

    private DefaultCameraPresets() {
    }

    /**
     * Registers a custom camera preset.
     *
     * @param definition custom camera definition
     * @return shared registered definition
     */
    public static NamedDefinition register(CustomCameraDefinition definition) {
        return REGISTRY.register(definition);
    }

    /**
     * Registers a camera preset directly.
     *
     * <p>Prefer {@link #register(CustomCameraDefinition)} for custom cameras.</p>
     *
     * @param preset camera preset
     * @return shared registered definition
     */
    public static NamedDefinition register(CameraPreset preset) {
        return REGISTRY.register(preset);
    }

    /**
     * Returns all registered camera presets in runtime-ID order.
     *
     * @return immutable camera preset list
     */
    public static List<CameraPreset> getAll() {
        return REGISTRY.getAll();
    }

    /**
     * Returns the registry the codec helper uses to serialize camera instructions.
     *
     * @return camera definition registry
     */
    public static DefinitionRegistry<NamedDefinition> getDefinitions() {
        return REGISTRY.getDefinitions();
    }

    /**
     * Looks up the shared definition of a camera preset by name.
     *
     * <p>The returned instance must be reused in camera instructions because
     * {@link SimpleDefinitionRegistry#isRegistered} compares definitions by reference.</p>
     *
     * @param name camera preset identifier
     * @return shared definition, or {@code null} when not registered
     */
    public static NamedDefinition getDefinition(String name) {
        return REGISTRY.getDefinition(name);
    }

    /**
     * Looks up a shared camera definition by runtime ID.
     *
     * @param runtimeId camera runtime ID
     * @return shared definition, or {@code null} when not registered
     */
    public static NamedDefinition getDefinition(int runtimeId) {
        return REGISTRY.getDefinition(runtimeId);
    }

    /**
     * Returns whether the camera preset is registered.
     *
     * @param identifier camera preset identifier
     * @return {@code true} when registered
     */
    public static boolean contains(String identifier) {
        return REGISTRY.contains(identifier);
    }

    /**
     * Returns the number of registered camera presets.
     *
     * @return camera preset count
     */
    public static int size() {
        return REGISTRY.size();
    }
}
