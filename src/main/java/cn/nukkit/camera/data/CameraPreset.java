package cn.nukkit.camera.data;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.DoNotModify;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.UpdateSoftEnumPacket;
import cn.nukkit.network.protocol.types.camera.CameraAudioListener;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraPresetAimAssist;
import cn.nukkit.utils.OptionalValue;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a camera preset configuration for the camera system.
 * <p>
 * CameraPreset objects define the behavior, position, rotation, and other properties
 * for a camera view in the game. Presets can be registered and retrieved by identifier.
 * <p>
 * Usage:
 * <ul>
 *   <li>Use {@link #registerCameraPresets(CameraPreset...)} to register new presets.</li>
 *   <li>Access registered presets via {@link #getPresets()} or {@link #getPreset(String)}.</li>
 *   <li>Predefined presets: {@link #FIRST_PERSON}, {@link #FOLLOW_ORBIT}, {@link #FREE}, {@link #THIRD_PERSON}, {@link #THIRD_PERSON_FRONT}.</li>
 * </ul>
 * <p>
 * Each preset can specify position, rotation, limits, audio listener, effects, and aim assist options.
 * <p>
 * Thread safety: The preset registry is backed by a TreeMap and is not thread-safe for concurrent modification.
 *
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/6/11
 */


@Getter
public final class CameraPreset {

    private static final Map<String, CameraPreset> PRESETS = new TreeMap<>();

    /**
     * Returns the map of all registered camera presets.
     * <p>
     * The returned map is unmodifiable and contains all presets registered via {@link #registerCameraPresets(CameraPreset...)}.
     *
     * @return the map of registered camera presets, keyed by identifier
     */
    @DoNotModify
    public static Map<String, CameraPreset> getPresets() {
        return PRESETS;
    }

    /**
     * Retrieves a camera preset by its identifier.
     *
     * @param identifier the unique identifier of the camera preset
     * @return the CameraPreset instance, or null if not found
     */
    public @Nullable static CameraPreset getPreset(String identifier) {
        return getPresets().get(identifier);
    }

    /**
     * Registers one or more camera presets to the global registry.
     * <p>
     * Throws an exception if a preset with the same identifier already exists.
     * Updates the camera preset enum for command auto-completion and assigns unique IDs to each preset.
     * Notifies all online players of the new presets.
     *
     * @param presets the camera presets to register
     * @throws IllegalArgumentException if a preset identifier is already registered
     */
    public static void registerCameraPresets(CameraPreset... presets) {
        for (var preset : presets) {
            if (PRESETS.containsKey(preset.getIdentifier()))
                throw new IllegalArgumentException("Camera preset " + preset.getIdentifier() + " already exists!");
            PRESETS.put(preset.getIdentifier(), preset);
            CommandEnum.CAMERA_PRESETS.updateSoftEnum(UpdateSoftEnumPacket.Type.ADD, preset.getIdentifier());
        }
        int id = 0;
        // Reassign unique IDs to each preset
        for (var preset : presets) {
            preset.id = id++;
        }
        Server.getInstance().getOnlinePlayers().values().forEach(Player::sendCameraPresets);
    }

    public static final CameraPreset FIRST_PERSON;
    public static final CameraPreset FOLLOW_ORBIT;
    public static final CameraPreset FREE;
    public static final CameraPreset THIRD_PERSON;
    public static final CameraPreset THIRD_PERSON_FRONT;

    static {
        FIRST_PERSON = CameraPreset.builder()
                .identifier("minecraft:first_person")
                .build();
        FOLLOW_ORBIT = CameraPreset.builder()
                .identifier("minecraft:follow_orbit")
                .build();
        FREE = CameraPreset.builder()
                .identifier("minecraft:free")
                .pos(new Vector3f(0, 0, 0))
                .yaw(0f)
                .pitch(0f)
                .build();
        THIRD_PERSON = CameraPreset.builder()
                .identifier("minecraft:third_person")
                .build();
        THIRD_PERSON_FRONT = CameraPreset.builder()
                .identifier("minecraft:third_person_front")
                .build();

        registerCameraPresets(FIRST_PERSON, FOLLOW_ORBIT, FREE, THIRD_PERSON, THIRD_PERSON_FRONT);
    }

    private final String identifier;
    private final String inheritFrom;
    @Nullable
    private final Vector3f pos;
    @Nullable
    private final Float yaw;
    @Nullable
    private final Float pitch;
    @Nullable
    private final Float rotationSpeed;
    @NotNull
    private final OptionalValue<Boolean> snapToTarget;
    @Nullable
    private final Vector2f horizontalRotationLimit;
    @Nullable
    private final Vector2f verticalRotationLimit;
    @NotNull
    private final OptionalValue<Boolean> continueTargeting;
    @NotNull
    private final OptionalValue<Float> blockListeningRadius;
    @Nullable
    private final Vector2f viewOffset;
    @Nullable
    private final Vector3f entityOffset;
    @Nullable
    private final Float radius;
    @Nullable
    private final Float yawLimitMin;
    @Nullable
    private final Float yawLimitMax;
    @Nullable
    private final CameraAudioListener listener;
    @NotNull
    private final OptionalValue<Boolean> playEffect;
    @NotNull
    private final OptionalValue<CameraPresetAimAssist> aimAssist;


    private int id = 0;

    @Builder
    /**
     * Constructs a CameraPreset using the builder pattern.
     * <p>
     * Only a subset of fields is settable via the builder. For advanced configuration, use the full constructor.
     * <p>
     * You must call {@link #registerCameraPresets(CameraPreset...)} to make the preset available.
     *
     * @param identifier the unique identifier for the preset
     * @param inheritFrom the identifier of the preset to inherit from (optional)
     * @param pos the position of the camera (optional)
     * @param yaw the yaw rotation (optional)
     * @param pitch the pitch rotation (optional)
     * @param listener the audio listener configuration (optional)
     * @param playEffect whether to play camera effects (optional)
     */
    public CameraPreset(String identifier, String inheritFrom, @Nullable Vector3f pos, @Nullable Float yaw, @Nullable Float pitch, @Nullable CameraAudioListener listener, OptionalValue<Boolean> playEffect) {
        this(
                identifier,
                inheritFrom,
                pos,
                yaw,
                pitch,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                listener,
                playEffect == null ? null : playEffect.orElseGet(null),
                null,
                null,
                null,
                null
        );
    }

    /**
     * Full constructor for CameraPreset.
     * <p>
     * Allows setting all available fields for advanced camera configuration.
     *
     * @param identifier the unique identifier for the preset
     * @param inheritFrom the identifier of the preset to inherit from (optional)
     * @param pos the position of the camera (optional)
     * @param yaw the yaw rotation (optional)
     * @param pitch the pitch rotation (optional)
     * @param rotationSpeed the speed of camera rotation (optional)
     * @param snapToTarget whether the camera snaps to its target (optional)
     * @param blockListeningRadius the audio block listening radius (optional)
     * @param viewOffset the offset of the camera view (optional)
     * @param entityOffset the offset relative to the entity (optional)
     * @param radius the camera orbit radius (optional)
     * @param yawLimitMin minimum yaw limit (optional)
     * @param yawLimitMax maximum yaw limit (optional)
     * @param listener the audio listener configuration (optional)
     * @param playEffect whether to play camera effects (optional)
     * @param horizontalRotationLimit horizontal rotation limits (optional)
     * @param verticalRotationLimit vertical rotation limits (optional)
     * @param continueTargeting whether to continue targeting (optional)
     * @param aimAssist aim assist configuration (optional)
     */
    public CameraPreset(
            String identifier,
            String inheritFrom,
            @Nullable Vector3f pos,
            @Nullable Float yaw,
            @Nullable Float pitch,
            @Nullable Float rotationSpeed,
            @Nullable Boolean snapToTarget,
            @Nullable Float blockListeningRadius,
            @Nullable Vector2f viewOffset,
            @Nullable Vector3f entityOffset,
            @Nullable Float radius,
            @Nullable Float yawLimitMin,
            @Nullable Float yawLimitMax,
            @Nullable CameraAudioListener listener,
            @Nullable Boolean playEffect,
            @Nullable Vector2f horizontalRotationLimit,
            @Nullable Vector2f verticalRotationLimit,
            @Nullable Boolean continueTargeting,
            @Nullable CameraPresetAimAssist aimAssist
    ) {
        this.identifier = identifier;
        this.inheritFrom = inheritFrom != null ? inheritFrom : "";
        this.pos = pos;
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotationSpeed = rotationSpeed;
        this.snapToTarget = OptionalValue.ofNullable(snapToTarget);
        this.blockListeningRadius = OptionalValue.ofNullable(blockListeningRadius);
        this.viewOffset = viewOffset;
        this.entityOffset = entityOffset;
        this.radius = radius;
        this.yawLimitMin = yawLimitMin;
        this.yawLimitMax = yawLimitMax;
        this.listener = listener;
        this.playEffect = OptionalValue.ofNullable(playEffect);
        this.horizontalRotationLimit = horizontalRotationLimit;
        this.verticalRotationLimit = verticalRotationLimit;
        this.continueTargeting = OptionalValue.ofNullable(continueTargeting);
        this.aimAssist = OptionalValue.ofNullable(aimAssist);
    }
}
