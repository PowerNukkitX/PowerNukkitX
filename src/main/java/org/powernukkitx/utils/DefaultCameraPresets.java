package org.powernukkitx.utils;

import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraPreset;
import org.cloudburstmc.protocol.common.DefinitionRegistry;
import org.cloudburstmc.protocol.common.NamedDefinition;
import org.cloudburstmc.protocol.common.SimpleDefinitionRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * The presets in the order they are sent to the client - a preset's runtime id is its index
     * in this list, so the order has to stay in sync with the definitions below.
     */
    private static final List<CameraPreset> CAMERA_PRESETS = List.of(
            FIRST_PERSON, FIXED_BOOM, FOLLOW_ORBIT, FREE, THIRD_PERSON, THIRD_PERSON_FRONT
    );

    private static final Map<String, NamedDefinition> DEFINITIONS_BY_NAME = new HashMap<>();

    private static final DefinitionRegistry<NamedDefinition> DEFINITIONS;

    static {
        final SimpleDefinitionRegistry.Builder<NamedDefinition> builder = SimpleDefinitionRegistry.builder();
        for (int runtimeId = 0; runtimeId < CAMERA_PRESETS.size(); runtimeId++) {
            final NamedDefinition definition = new CameraPresetDefinition(CAMERA_PRESETS.get(runtimeId).getName(), runtimeId);
            DEFINITIONS_BY_NAME.put(definition.getIdentifier(), definition);
            builder.add(definition);
        }
        DEFINITIONS = builder.build();
    }

    public static List<CameraPreset> getAll() {
        return CAMERA_PRESETS;
    }

    /**
     * The registry the codec helper needs to serialize a {@code CameraInstructionPacket}.
     */
    public static DefinitionRegistry<NamedDefinition> getDefinitions() {
        return DEFINITIONS;
    }

    /**
     * Looks up the definition of a preset by name. The instance is shared, which
     * {@link SimpleDefinitionRegistry#isRegistered} relies on - it compares by reference.
     *
     * @return the definition, or {@code null} when no preset carries that name
     */
    public static NamedDefinition getDefinition(String name) {
        return DEFINITIONS_BY_NAME.get(name);
    }

    private record CameraPresetDefinition(String identifier, int runtimeId) implements NamedDefinition {
        @Override
        public String getIdentifier() {
            return this.identifier;
        }

        @Override
        public int getRuntimeId() {
            return this.runtimeId;
        }
    }
}
