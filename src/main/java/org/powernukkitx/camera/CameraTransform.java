package org.powernukkitx.camera;

import com.google.common.base.Preconditions;
import org.powernukkitx.math.Vector3;

import java.util.Objects;

/**
 * Defines a camera position and rotation.
 *
 * @param position camera position
 * @param pitch camera pitch
 * @param yaw camera yaw
 *
 * @author Curse
 */
public record CameraTransform(Vector3 position, float pitch, float yaw) {
    public CameraTransform {
        Objects.requireNonNull(
                position,
                "position"
        );

        Preconditions.checkArgument(
                Double.isFinite(position.x)
                        && Double.isFinite(position.y)
                        && Double.isFinite(position.z),
                "Camera position coordinates must be finite"
        );

        Preconditions.checkArgument(
                Float.isFinite(pitch)
                        && Float.isFinite(yaw),
                "Camera rotation must be finite"
        );
    }
}
