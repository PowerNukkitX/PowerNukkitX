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
 * @author daoge_cmd
 * @date 2023/6/11
 * PowerNukkitX Project
 */


@Getter
public final class CameraPreset {

    private static final Map<String, CameraPreset> PRESETS = new TreeMap<>();

    @DoNotModify
    public static Map<String, CameraPreset> getPresets() {
        return PRESETS;
    }

    public @Nullable
    static CameraPreset getPreset(String identifier) {
        return getPresets().get(identifier);
    }

    public static void registerCameraPresets(CameraPreset... presets) {
        for (var preset : presets) {
            if (PRESETS.containsKey(preset.getIdentifier()))
                throw new IllegalArgumentException("Camera preset " + preset.getIdentifier() + " already exists!");
            PRESETS.put(preset.getIdentifier(), preset);
            CommandEnum.CAMERA_PRESETS.updateSoftEnum(UpdateSoftEnumPacket.Type.ADD, preset.getIdentifier());
        }
        int id = 0;
        //重新分配id
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

    /**
     * Remember to call the registerCameraPresets() method to register!
     */
    @Builder
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
