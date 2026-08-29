package org.powernukkitx.camera;

import org.cloudburstmc.protocol.bedrock.data.camera.CameraSplineType;

/**
 * Linear camera spline.
 *
 * @author Curse
 */
public final class LinearSpline extends CameraSpline {
    public LinearSpline() {
        super(CameraSplineType.LINEAR, 3);
    }
}
