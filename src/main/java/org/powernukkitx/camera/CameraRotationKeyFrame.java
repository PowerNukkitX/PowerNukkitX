package org.powernukkitx.camera;

import com.google.common.base.Preconditions;
import org.cloudburstmc.protocol.bedrock.data.camera.EasingType;
import org.powernukkitx.math.Vector3;

/**
 * Defines camera rotation at a specific animation time.
 *
 * @author Curse
 */
public record CameraRotationKeyFrame(float timeSeconds, Vector3 rotation, EasingType easing) {
    public CameraRotationKeyFrame(float timeSeconds, Vector3 rotation) {
        this(timeSeconds, rotation, EasingType.LINEAR);
    }

    public CameraRotationKeyFrame(float timeSeconds, float x, float y, float z) {
        this(
            timeSeconds,
            new Vector3(x, y, z),
            EasingType.LINEAR
        );
    }

    public CameraRotationKeyFrame {
        Preconditions.checkArgument(
            Float.isFinite(timeSeconds) && timeSeconds >= 0.0f,
            "Camera key frame time must be finite and non-negative"
        );

        Preconditions.checkNotNull(rotation, "rotation");

        Preconditions.checkArgument(
            Double.isFinite(rotation.x)
                && Double.isFinite(rotation.y)
                && Double.isFinite(rotation.z),
            "Camera rotation coordinates must be finite"
        );

        Preconditions.checkNotNull(easing, "easing");
    }
}
