package org.powernukkitx.camera;

import com.google.common.base.Preconditions;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraSetInstruction;
import org.cloudburstmc.protocol.bedrock.data.camera.CameraSplineInstruction;
import org.cloudburstmc.protocol.bedrock.packet.CameraInstructionPacket;
import org.cloudburstmc.protocol.common.NamedDefinition;
import org.powernukkitx.Player;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.DefaultCameraPresets;

import java.util.Objects;

/**
 * Controls the active camera of a player.
 *
 * @author Curse
 */
public final class PlayerCamera {
    private final Player player;

    public PlayerCamera(Player player) {
        this.player = Objects.requireNonNull(player, "player");
    }

    /**
     * Sets the active camera preset.
     *
     * @param presetIdentifier camera preset identifier
     */
    public void setCamera(String presetIdentifier) {
        NamedDefinition preset = this.resolvePreset(presetIdentifier);

        CameraInstructionPacket packet = new CameraInstructionPacket();
        packet.setSetInstruction(
                CameraSetInstruction.builder()
                        .preset(preset)
                        .build()
        );

        this.player.sendPacket(packet);
    }

    /**
     * Sets the active camera preset and position.
     *
     * @param presetIdentifier camera preset identifier
     * @param position camera position
     */
    public void setCamera(String presetIdentifier, Vector3 position) {
        NamedDefinition preset = this.resolvePreset(presetIdentifier);

        CameraInstructionPacket packet = new CameraInstructionPacket();
        packet.setSetInstruction(
                CameraSetInstruction.builder()
                        .preset(preset)
                        .pos(toNetwork(position))
                        .build()
        );

        this.player.sendPacket(packet);
    }

    /**
     * Sets the active camera preset, position and rotation.
     *
     * @param presetIdentifier camera preset identifier
     * @param position camera position
     * @param pitch camera pitch
     * @param yaw camera yaw
     */
    public void setCamera(String presetIdentifier, Vector3 position, float pitch, float yaw) {
        NamedDefinition preset = this.resolvePreset(presetIdentifier);

        CameraInstructionPacket packet = new CameraInstructionPacket();
        packet.setSetInstruction(
                CameraSetInstruction.builder()
                        .preset(preset)
                        .pos(toNetwork(position))
                        .rot(Vector2f.from(pitch, yaw))
                        .build()
        );

        this.player.sendPacket(packet);
    }

    /**
     * Sets the active camera preset and transform.
     *
     * @param presetIdentifier camera preset identifier
     * @param transform camera transform
     */
    public void setCamera(String presetIdentifier, CameraTransform transform) {
        Objects.requireNonNull(transform, "transform");

        this.setCamera(
                presetIdentifier,
                transform.position(),
                transform.pitch(),
                transform.yaw()
        );
    }

    /**
     * Plays a dynamic spline camera animation.
     *
     * @param spline spline path
     * @param totalTimeSeconds total animation duration
     * @param animation animation key frames
     */
    public void playAnimation(CameraSpline spline, float totalTimeSeconds, CameraAnimation animation) {
        Objects.requireNonNull(spline, "spline");
        Objects.requireNonNull(animation, "animation");

        Preconditions.checkArgument(
                Float.isFinite(totalTimeSeconds) && totalTimeSeconds > 0.0f,
                "Camera animation duration must be finite and greater than zero"
        );

        spline.validate();

        validateAnimation(animation, totalTimeSeconds);

        CameraSplineInstruction instruction = new CameraSplineInstruction();

        instruction.setTotalTime(totalTimeSeconds);
        instruction.setType(spline.getType());

        for (Vector3 controlPoint : spline.getControlPoints()) {
            instruction.getCurve().add(toNetwork(controlPoint));
        }

        for (CameraProgressKeyFrame keyFrame : animation.getProgressKeyFrames()) {
            instruction.getProgressKeyFrames().add(
                    new CameraSplineInstruction.SplineProgressOption(
                            keyFrame.alpha(),
                            keyFrame.timeSeconds(),
                            keyFrame.easing()
                    )
            );
        }

        for (CameraRotationKeyFrame keyFrame : animation.getRotationKeyFrames()) {
            instruction.getRotationOption().add(
                    new CameraSplineInstruction.SplineRotationOption(
                            null,
                            -1.0f,
                            toNetwork(keyFrame.rotation()),
                            keyFrame.timeSeconds(),
                            keyFrame.easing()
                    )
            );
        }

        instruction.setSplineIdentifier("");
        instruction.setLoadFromJSON(false);

        CameraInstructionPacket packet = new CameraInstructionPacket();
        packet.setSplineInstruction(instruction);

        this.player.sendPacket(packet);
    }

    /**
     * Clears the active camera and returns control to the player.
     */
    public void clear() {
        CameraInstructionPacket packet = new CameraInstructionPacket();
        packet.setClear(true);

        this.player.sendPacket(packet);
    }

    private NamedDefinition resolvePreset(String presetIdentifier) {
        Preconditions.checkArgument(
                presetIdentifier != null && !presetIdentifier.isBlank(),
                "Camera preset identifier cannot be blank"
        );

        NamedDefinition preset = DefaultCameraPresets.getDefinition(
                presetIdentifier
        );

        Preconditions.checkArgument(
                preset != null,
                "Unknown camera preset: %s",
                presetIdentifier
        );

        return preset;
    }

    private static void validateAnimation(CameraAnimation animation, float totalTimeSeconds) {
        float previousProgressTime = -1.0f;

        for (CameraProgressKeyFrame keyFrame : animation.getProgressKeyFrames()) {
            Preconditions.checkArgument(
                    keyFrame.timeSeconds() <= totalTimeSeconds,
                    "Camera progress key frame time %s exceeds total animation duration %s",
                    keyFrame.timeSeconds(),
                    totalTimeSeconds
            );

            Preconditions.checkArgument(
                    keyFrame.timeSeconds() >= previousProgressTime,
                    "Camera progress key frames must be ordered by time"
            );

            previousProgressTime = keyFrame.timeSeconds();
        }

        float previousRotationTime = -1.0f;

        for (CameraRotationKeyFrame keyFrame : animation.getRotationKeyFrames()) {
            Preconditions.checkArgument(
                    keyFrame.timeSeconds() <= totalTimeSeconds,
                    "Camera rotation key frame time %s exceeds total animation duration %s",
                    keyFrame.timeSeconds(),
                    totalTimeSeconds
            );

            Preconditions.checkArgument(
                    keyFrame.timeSeconds() >= previousRotationTime,
                    "Camera rotation key frames must be ordered by time"
            );

            previousRotationTime = keyFrame.timeSeconds();
        }
    }

    private static Vector3f toNetwork(Vector3 vector) {
        Objects.requireNonNull(vector, "vector");

        Preconditions.checkArgument(
                Double.isFinite(vector.x)
                        && Double.isFinite(vector.y)
                        && Double.isFinite(vector.z),
                "Camera vector coordinates must be finite"
        );

        return Vector3f.from(
                (float) vector.x,
                (float) vector.y,
                (float) vector.z
        );
    }
}
