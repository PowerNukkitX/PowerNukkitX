package org.powernukkitx.camera;

import com.google.common.base.Preconditions;
import org.cloudburstmc.protocol.bedrock.data.camera.EasingFunction;

/**
 * Defines camera progress along a spline at a specific time.
 *
 * @author Curse
 */
public record CameraProgressKeyFrame(float timeSeconds, float alpha, EasingFunction easing) {
    public CameraProgressKeyFrame(float timeSeconds, float alpha) {
        this(timeSeconds, alpha, EasingFunction.LINEAR);
    }

    public CameraProgressKeyFrame {
        Preconditions.checkArgument(
            Float.isFinite(timeSeconds) && timeSeconds >= 0.0f,
            "Camera key frame time must be finite and non-negative"
        );

        Preconditions.checkArgument(
            Float.isFinite(alpha) && alpha >= 0.0f && alpha <= 1.0f,
            "Camera spline alpha must be between 0 and 1"
        );

        Preconditions.checkNotNull(easing, "easing");
    }
}
